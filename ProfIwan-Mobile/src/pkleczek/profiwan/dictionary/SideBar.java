package pkleczek.profiwan.dictionary;

import pkleczek.profiwan.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SectionIndexer;

/**
 * A search bar used to quickly navigate through a list of phrases (like in
 * Android's contacts).
 * 
 * @author Paweł Kłeczek
 * 
 */
public class SideBar extends View {
	/**
	 * An array of all sections to be drawn.
	 */
	private String[] sections;

	private String selectedSection = "";

	private SectionIndexer sectionIndexer = null;
	private PhraseListView phraseList;

	private final Paint _paint = new Paint();

	/**
	 * Height of one item in a bar.
	 */
	private int itemHeight;

	public SideBar(Context context) {
		super(context);
		init();
	}

	public SideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		// TODO : set background
		// setBackgroundColor(getResources().getColor(R.color.sidebar_background));
	}

	public void setListView(PhraseListView listView) {
		phraseList = listView;
		sectionIndexer = (SectionIndexer) listView.getAdapter();
	}

	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);

		int idx = getTouchedItemIndex(event.getY());

		if (event.getAction() == MotionEvent.ACTION_DOWN
				|| event.getAction() == MotionEvent.ACTION_MOVE) {

			if (sectionIndexer == null) {
				sectionIndexer = (SectionIndexer) phraseList.getAdapter();
			}

			selectedSection = (String) sectionIndexer.getSections()[idx];

			sendDrawOnMessage();

			int position = sectionIndexer.getPositionForSection(idx);
			phraseList.setSelection(position);
		}

		if (event.getAction() == MotionEvent.ACTION_UP) {
			selectedSection = "";
			phraseList.getHandler().sendEmptyMessageDelayed(
					PhraseListView.MSG_DRAW_OFF, 100);
		}

		return true;
	}

	private void sendDrawOnMessage() {
		Message msg = new Message();
		msg.what = PhraseListView.MSG_DRAW_ON;
		msg.obj = selectedSection;
		phraseList.getHandler().sendMessage(msg);
	}

	private int getTouchedItemIndex(float eventY) {
		int idx = (int) (eventY / itemHeight);

		idx = Math.max(idx, 0);
		idx = Math.min(idx, sections.length - 1);

		return idx;
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		sections = (String[]) sectionIndexer.getSections();

		final int normalFontColor = getResources().getColor(
				R.color.sidebar_font_normal);
		final int selectedFontColor = getResources().getColor(
				R.color.sidebar_font_selected);

		final int padding = 4;

		itemHeight = getMeasuredHeight() / sections.length;

		int textSize = Math.min(getMeasuredWidth(), itemHeight) - padding;
		_paint.setTextSize(textSize);
		_paint.setTextAlign(Paint.Align.CENTER);
		float widthCenter = getMeasuredWidth() / 2;

		for (int i = 0; i < sections.length; i++) {
			if (sections[i].equals(selectedSection)) {
				_paint.setColor(selectedFontColor);
			} else {
				_paint.setColor(normalFontColor);
			}

			canvas.drawText(sections[i], widthCenter, itemHeight / 2
					+ (i * itemHeight), _paint);
		}
	}

}