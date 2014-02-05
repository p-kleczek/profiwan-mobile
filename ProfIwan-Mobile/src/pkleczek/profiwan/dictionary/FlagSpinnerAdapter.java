package pkleczek.profiwan.dictionary;

import java.util.List;

import pkleczek.profiwan.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class FlagSpinnerAdapter extends ArrayAdapter<Flag> {

	private Context context;
	private List<Flag> objects;

	public FlagSpinnerAdapter(Context context, List<Flag> objects) {
		super(context, R.layout.flag_spinner_row, objects);

		this.context = context;
		this.objects = objects;
	}

	LayoutInflater mInflater;

	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = mInflater.inflate(R.layout.flag_spinner_row, parent,
				false);

		return rowView;
	}
}
