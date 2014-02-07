package pkleczek.profiwan.dictionary;

import java.util.ArrayList;
import java.util.List;

import pkleczek.profiwan.R;
import pkleczek.profiwan.model.PhraseEntry;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

class PhraseEntryArrayAdapter extends ArrayAdapter<PhraseEntry> implements
		Filterable, SectionIndexer {

	// TODO: support other languages
	// private static String sections = "abcdefghilmnopqrstuvz";
	private static String sections = "абфлвшухй";

	private final Context context;
	private final List<PhraseEntry> objects;
	private List<PhraseEntry> filtered;
	private PhraseLangBFilter filter;

	public PhraseEntryArrayAdapter(Context context, List<PhraseEntry> objects) {
		super(context, R.layout.dictionary_entry, objects);
		this.context = context;

		this.objects = new ArrayList<PhraseEntry>(objects);
		filtered = new ArrayList<PhraseEntry>(objects);
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

		RelativeLayout header = (RelativeLayout) rowView
				.findViewById(R.id.dictionary_entry_section);
		char firstChar = selectedPhrase.getLangBText().toUpperCase().charAt(0);
		if (position == 0) {
			setSection(header, selectedPhrase.getLangBText());
		} else {
			// XXX: should be "filtered" or "objects"?
			String preLabel = filtered.get(position - 1).getLangBText();
			char preFirstChar = preLabel.toUpperCase().charAt(0);
			if (firstChar != preFirstChar) {
				setSection(header, selectedPhrase.getLangBText());
			} else {
				header.setVisibility(View.GONE);
			}
		}

		return rowView;
	}

	private void setSection(ViewGroup header, String label) {
		TextView text = new TextView(context);
		header.setBackgroundColor(0xffaabbcc);
		text.setTextColor(Color.WHITE);
		text.setText(label.substring(0, 1).toUpperCase());
		text.setTextSize(20);
		text.setPadding(5, 0, 0, 0);
		text.setGravity(Gravity.CENTER_VERTICAL);
		header.addView(text);
	}

	@Override
	public Filter getFilter() {
		if (filter == null) {
			filter = new PhraseLangBFilter();
		}

		return filter;
	}

	/**
	 * Filters for phrases which "language B" text starts with the given string.
	 * 
	 * @author Paweł Kłeczek
	 * 
	 */
	private class PhraseLangBFilter extends Filter {
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

				notifyDataSetChanged();
			}
		}

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
	}

	@Override
	public int getPositionForSection(int section) {
		if (section == 35) {
			return 0;
		}
		for (int i = 0; i < filtered.size(); i++) {
			PhraseEntry pe = filtered.get(i);
			char firstChar = pe.getLangBText().toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}

	// public int getPositionForSection(int sectionIndex) {
	// Log.d("ListView", "Get position for section");
	//
	// for (int i = 0; i < this.getCount(); i++) {
	// PhraseEntry pe = this.getItem(i);
	// String item = pe.getLangBText().toLowerCase();
	// if (item.charAt(0) == sections.charAt(sectionIndex)) {
	// return i;
	// }
	// }
	// return 0;
	// }

	@Override
	public int getSectionForPosition(int position) {
		Log.d("ListView", "Get section");

		return 0;
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}
