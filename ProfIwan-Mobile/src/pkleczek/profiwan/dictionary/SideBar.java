package pkleczek.profiwan.dictionary;

import pkleczek.profiwan.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SectionIndexer;

public class SideBar extends View {
	private String[] sections;

	private SectionIndexer sectionIndexer = null;
	private ListView list;

	private final Paint paint = new Paint();

	private int m_nItemHeight;

	public SideBar(Context context) {
		super(context);
		init();
	}

	public SideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
//		setBackgroundColor(getResources().getColor(R.color.sidebar_background));
		setBackgroundColor(0xffffff);
	}

	public SideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void setListView(ListView _list) {
		list = _list;
		sectionIndexer = (SectionIndexer) _list.getAdapter();

		sections = (String[]) sectionIndexer.getSections();
	}

	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);

		int i = (int) event.getY();
		int idx = i / m_nItemHeight;
		if (idx >= sections.length) {
			idx = sections.length - 1;
		} else if (idx < 0) {
			idx = 0;
		}

		if (event.getAction() == MotionEvent.ACTION_DOWN
				|| event.getAction() == MotionEvent.ACTION_MOVE) {
			if (sectionIndexer == null) {
				sectionIndexer = (SectionIndexer) list.getAdapter();
			}
			int position = sectionIndexer.getPositionForSection(idx);
			if (position == -1) {
				return true;
			}
			list.setSelection(position);
		}
		return true;
	}

	protected void onDraw(Canvas canvas) {
		m_nItemHeight = getMeasuredHeight() / sections.length;

		paint.setColor(getResources().getColor(R.color.sidebar_font));

		int padding = 4;
		int textSize = Math.min(getMeasuredWidth(), m_nItemHeight) - padding;
		paint.setTextSize(textSize);

		paint.setTextAlign(Paint.Align.CENTER);
		float widthCenter = getMeasuredWidth() / 2;

		for (int i = 0; i < sections.length; i++) {
			// TODO: draw in the center of a slot (consider Y-axis!)
			canvas.drawText(sections[i], widthCenter, m_nItemHeight / 2
					+ (i * m_nItemHeight), paint);
		}
		super.onDraw(canvas);
	}
}