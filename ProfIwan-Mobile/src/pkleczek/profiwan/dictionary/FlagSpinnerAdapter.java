package pkleczek.profiwan.dictionary;

import pkleczek.profiwan.R;
import pkleczek.profiwan.utils.Language;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class FlagSpinnerAdapter extends ArrayAdapter<Language> {

	public FlagSpinnerAdapter(Context ctx) {
		super(ctx, R.layout.flag_spinner_row, Language.values());
	}

	@Override
	public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
		return getCustomView(position, cnvtView, prnt);
	}

	@Override
	public View getView(int pos, View cnvtView, ViewGroup prnt) {
		return getCustomView(pos, cnvtView, prnt);
	}

	public View getCustomView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.flag_spinner_row, parent,
				false);

		TextView text = (TextView) rowView
				.findViewById(R.id.flag_spinner_row_text);
		SpannableStringBuilder ssb = new SpannableStringBuilder(" ");

		int iconId = Language.values()[position].getFlagIconId();

		Bitmap smiley = BitmapFactory.decodeResource(getContext()
				.getResources(), iconId);
		ssb.setSpan(new ImageSpan(smiley), 0, 1,
				Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		text.setText(ssb, BufferType.SPANNABLE);

		return rowView;
	}
}
