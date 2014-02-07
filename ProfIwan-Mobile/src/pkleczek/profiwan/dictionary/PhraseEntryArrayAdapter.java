package pkleczek.profiwan.dictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pkleczek.profiwan.R;
import pkleczek.profiwan.model.PhraseEntry;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SectionIndexer;
import android.widget.TextView;

class PhraseEntryArrayAdapter extends ArrayAdapter<PhraseEntry> implements
		Filterable, SectionIndexer {

	/**
	 * Filters for phrases which "language B" text starts with the given string.
	 * 
	 * @author Paweł Kłeczek
	 * 
	 */
	private class PhraseLangBFilter extends Filter {
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			// TODO: locale = language set as revised
			constraint = constraint.toString().toLowerCase();
			FilterResults result = new FilterResults();

			if (constraint != null && constraint.length() > 0) {
				List<PhraseEntry> filt = new ArrayList<PhraseEntry>();
				List<PhraseEntry> lItems = new ArrayList<PhraseEntry>();

				synchronized (this) {
					lItems.addAll(objects);
				}

				for (PhraseEntry pe : lItems) {
					if (pe.getLangBText().startsWith(constraint.toString())) {
						filt.add(pe);
					}
				}

				result.count = filt.size();
				result.values = filt;
			} else {
				synchronized (this) {
					result.count = objects.size();
					result.values = objects;
				}
			}

			return result;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {

			clear();

			if (results.count == 0) {
				notifyDataSetInvalidated();
			} else {
				filtered = (List<PhraseEntry>) results.values;

				for (PhraseEntry pe : filtered) {
					add(pe);
				}

				updateIndexer();

				notifyDataSetChanged();
			}
		}
	}

	// TODO: use only one language in dictionary (at the time)?
	private String[] sections;
	private final Context context;
	private PhraseLangBFilter filter;
	private List<PhraseEntry> filtered;

	/**
	 * Maps section to its first occurence in filtered list.
	 */
	private final Map<String, Integer> alphaIndexer = new HashMap<String, Integer>();

	private final List<PhraseEntry> objects;

	public PhraseEntryArrayAdapter(Context context, List<PhraseEntry> objects) {
		super(context, R.layout.dictionary_entry, objects);
		this.context = context;

		this.objects = new ArrayList<PhraseEntry>(objects);
		Collections.sort(this.objects);
		filtered = new ArrayList<PhraseEntry>(this.objects);

		updateIndexer();
	}

	@Override
	public Filter getFilter() {
		if (filter == null) {
			filter = new PhraseLangBFilter();
		}

		return filter;
	}

	@Override
	public int getPositionForSection(int sectionIndex) {
		return alphaIndexer.get(sections[sectionIndex]);
	}

	@Override
	public int getSectionForPosition(int position) {
		// Iterate over the sections to find the closest index
		// that is not greater than the position
		int closestIndex = 0;
		int latestDelta = Integer.MAX_VALUE;

		for (int i = 0; i < sections.length; i++) {
			int current = alphaIndexer.get(sections[i]);
			if (current == position) {
				// If position matches an index, return it immediately
				return i;
			} else if (current < position) {
				// Check if this is closer than the last index we inspected
				int delta = position - current;
				if (delta < latestDelta) {
					closestIndex = i;
					latestDelta = delta;
				}
			}
		}

		return closestIndex;
	}

	@Override
	public Object[] getSections() {
		return sections;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.dictionary_entry, parent,
				false);
		TextView textViewLangAText = (TextView) rowView
				.findViewById(R.id.dictionary_textViewLangAText);
		TextView textViewLangBText = (TextView) rowView
				.findViewById(R.id.dictionary_textViewLangBText);

		PhraseEntry selectedPhrase = filtered.get(position);
		textViewLangAText.setText(selectedPhrase.getLangAText());
		textViewLangBText.setText(selectedPhrase.getLangBText());

		return rowView;
	}

	/**
	 * Retain only those
	 */
	private void updateIndexer() {
		alphaIndexer.clear();
		
		for (int i = 0; i < filtered.size(); i++) {
			PhraseEntry pe = filtered.get(i);
			String start = pe.getLangBText().substring(0, 1).toLowerCase();

			if (!alphaIndexer.containsKey(start)) {
				alphaIndexer.put(start, i);
			}
		}

		List<String> keys = new ArrayList<String>(alphaIndexer.keySet());
		Collections.sort(keys);
		sections = keys.toArray(new String[0]);
	}
}
