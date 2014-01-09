package pkleczek.profiwan.model;

import org.joda.time.DateTime;

/**
 * The <code>RevisionEntry</code> class stores all information about a revision
 * relevant for generation of further revisions.
 * 
 * @author Pawel
 * 
 */
public class RevisionEntry {

	private long id;

	private DateTime date = null;

	/**
	 * How many times a mistake was made during the given revision.
	 */
	private int mistakes;

	public boolean isToContinue() {
		return (date.isAfter(DateTime.now().withTimeAtStartOfDay()) && mistakes < 0);
	}

	@Override
	public String toString() {
		return String.format("%s [%d]\n", date.toString(), //$NON-NLS-1$
				mistakes);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public DateTime getDate() {
		return date;
	}

	public void setDate(DateTime date) {
		this.date = date;
	}

	public int getMistakes() {
		return mistakes;
	}

	public void setMistakes(int mistakes) {
		this.mistakes = mistakes;
	}

}
