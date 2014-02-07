package pkleczek.profiwan.dictionary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pkleczek.profiwan.R;
import pkleczek.profiwan.model.PhraseEntry;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

class PhraseEntryArrayAdapter extends ArrayAdapter<PhraseEntry> implements
		Filterable {
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

		textViewLangAText.setText(filtered.get(position).getLangAText());
		textViewLangBText.setText(filtered.get(position).getLangBText());

		return rowView;
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
}
