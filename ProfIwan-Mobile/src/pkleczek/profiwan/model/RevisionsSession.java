package pkleczek.profiwan.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.joda.time.DateTime;

import pkleczek.profiwan.model.PhraseEntry.RevisionEntry;
import pkleczek.profiwan.utils.DatabaseHelper;

public class RevisionsSession {

	private static final String TAG = RevisionsSession.class.getName();
	
	/**
	 * List of pending revisions.
	 */
	private LinkedList<PhraseEntry> pendingRevisions = new LinkedList<PhraseEntry>();

	/**
	 * Maps phrase's ID on a corresponding revision entry.
	 */
	private Map<Long, RevisionEntry> revisionEntries = new HashMap<Long, RevisionEntry>();

	private ListIterator<PhraseEntry> pendingRevisionsIterator = null;
	private PhraseEntry currentRevision = null;

	private boolean enteredCorrectly = false;
	private int wordsNumber = 0;
	private int correctWordsNumber = 0;
	private int revisionsNumber = 1;
	
	private DatabaseHelper dbHelper;

	public RevisionsSession(DatabaseHelper dbHelper) {
		this.dbHelper = dbHelper;
	}

	public void prepareRevisions() {
		List<PhraseEntry> dictionary = dbHelper.getDictionary();

		for (PhraseEntry pe : dictionary) {
			if (pe.isReviseNow()) {
				pendingRevisions.add(pe);
				revisionEntries.put(pe.getId(), prepareRevisionEntry(pe));
			}
		}

		Collections.shuffle(pendingRevisions);

		wordsNumber = pendingRevisions.size();
		pendingRevisionsIterator = pendingRevisions.listIterator();
		
		if (hasRevisions()) {
			currentRevision = getNextWord();
		}
	}

	private RevisionEntry prepareRevisionEntry(PhraseEntry phrase) {
		RevisionEntry revision = new RevisionEntry();
		revision.date = DateTime.now();

		List<RevisionEntry> revisions = phrase.getRevisions();
		if (!revisions.isEmpty()) {
			RevisionEntry re = revisions.get(revisions.size() - 1);

			if (re.isToContinue()) {
				revision = re;
			}
		}

		return revision;
	}

	public boolean hasRevisions() {
		return !pendingRevisions.isEmpty();
	}
	
	public boolean isEnteredCorrectly(CharSequence input) {
		return currentRevision.getLangBText().equals(input);
	}

	public boolean processTypedWord(String input) {
		currentRevision = pendingRevisionsIterator.next();
		enteredCorrectly = isEnteredCorrectly(input);

		RevisionEntry re = revisionEntries.get(currentRevision.getId());
		if (re.mistakes == 0) {
			dbHelper.createRevision(re, currentRevision.getId());
		}

		if (enteredCorrectly) {
			re.mistakes = -re.mistakes;
			confirmRevision(currentRevision);
		} else {
			re.mistakes--;
		}

		dbHelper.updateRevision(re);

		return enteredCorrectly;
	}

	public PhraseEntry getCurrentPhrase() {
		return currentRevision;
	}

	private void confirmRevision(PhraseEntry pe) {
		RevisionEntry re = new RevisionEntry();
		pe.getRevisions().add(re);
		pendingRevisionsIterator.remove();
		revisionEntries.remove(pe.getId());
		correctWordsNumber++;
	}

	public void acceptRevision() {
		if (!enteredCorrectly) {
			confirmRevision(pendingRevisionsIterator.previous());
		}
	}

	public void editPhrase(String newText) {
		currentRevision.setLangBText(newText);
		dbHelper.updatePhrase(currentRevision);
	}

	public int getWordsNumber() {
		return wordsNumber;
	}

	public int getCorrectWordsNumber() {
		return correctWordsNumber;
	}

	public int getRevisionsNumber() {
		return revisionsNumber;
	}

	public int getPendingRevisionsSize() {
		return pendingRevisions.size();
	}

	public void nextWord() {
		if (!hasRevisions()) {
			return;
		}

		if (!pendingRevisionsIterator.hasNext()) {
			pendingRevisionsIterator = pendingRevisions.listIterator();
		}
		
		currentRevision = pendingRevisionsIterator.next();

		revisionsNumber++;
	}

	public PhraseEntry getNextWord() {
		return pendingRevisions.get(pendingRevisionsIterator.nextIndex());
	}
}
