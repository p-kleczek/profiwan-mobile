package pkleczek.profiwan.dictionary;

import java.util.List;

import pkleczek.profiwan.R;
import pkleczek.profiwan.model.PhraseEntry;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

class PhraseEntryArrayAdapter extends ArrayAdapter<PhraseEntry> {
	private final Context context;
	private final List<PhraseEntry> objects;

	public PhraseEntryArrayAdapter(Context context, List<PhraseEntry> objects) {
		super(context, R.layout.dictionary_entry, objects);
		this.context = context;
		this.objects = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.dictionary_entry, parent, false);
		TextView textViewLangAText = (TextView) rowView
				.findViewById(R.id.dictionary_textViewLangAText);
		TextView textViewLangBText = (TextView) rowView
				.findViewById(R.id.dictionary_textViewLangBText);

		textViewLangAText.setText(objects.get(position).getLangAText());
		textViewLangBText.setText(objects.get(position).getLangBText());
		
		return rowView;
	}
}
