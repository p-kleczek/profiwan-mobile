package pkleczek.profiwan.dictionary;

import pkleczek.profiwan.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class DictionaryEditActivity extends Activity {

	private static Integer[] imageIconDatabase = {
			R.drawable.flag_pl,
			R.drawable.flag_rus };

	private String[] imageNameDatabase = { "pl", "rus" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dictionary_edit);
		
		configureSpinner(R.id.dictionary_spin_knownLanguage);
		configureSpinner(R.id.dictionary_spin_revisedLanguage);
	}

	private void configureSpinner(int resId) {
		Spinner spin = (Spinner) findViewById(resId);
		spin.setAdapter(new MyAdapter(this, R.layout.flag_spinner_row,
				imageNameDatabase));
	}

	public class MyAdapter extends ArrayAdapter<String> {

		public MyAdapter(Context ctx, int txtViewResourceId, String[] objects) {
			super(ctx, txtViewResourceId, objects);
		}

		@Override
		public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
			return getCustomView(position, cnvtView, prnt);
		}

		@Override
		public View getView(int pos, View cnvtView, ViewGroup prnt) {
			return getCustomView(pos, cnvtView, prnt);
		}

		public View getCustomView(int position, View convertView,
				ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View rowView = inflater.inflate(R.layout.flag_spinner_row, parent,
					false);

//			ImageView icon = (ImageView) rowView
//					.findViewById(R.id.flag_spinner_row_icon);
//			icon.setImageResource(imageIconDatabase[position]);
			
			TextView text = (TextView) rowView.findViewById(R.id.flag_spinner_row_text);
			SpannableStringBuilder ssb = new SpannableStringBuilder(" ");
			Bitmap smiley = BitmapFactory.decodeResource( getResources(), imageIconDatabase[position] );
			ssb.setSpan( new ImageSpan( smiley ), 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE );	
			text.setText( ssb, BufferType.SPANNABLE );

			return rowView;
		}
	}
}
