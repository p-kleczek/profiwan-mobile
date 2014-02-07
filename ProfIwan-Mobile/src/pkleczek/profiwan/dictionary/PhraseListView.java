package pkleczek.profiwan.dictionary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ListView;

public class PhraseListView extends ListView {
	public static final int MSG_DRAW_ON = 1;
	public static final int MSG_DRAW_OFF = 2;

	// helpers
	// TODO: make it size-independent
	private static final float rectWidth = 60.0f;
	private static final float rectPadding = 10.0f;
	private final Paint rectPaint = new Paint();
	private final Paint textPaint = new Paint();
	private final RectF rect = new RectF();

	private boolean showLetter = false;
	private String section = "";

	private ListHandler listHandler = new ListHandler();

	// initialize helpers
	{
		rectPaint.setColor(Color.GRAY);
		rectPaint.setAlpha(200);

		textPaint.setColor(Color.CYAN);
		textPaint.setTextSize(rectWidth - rectPadding);
		textPaint.setTextAlign(Paint.Align.CENTER);
	}

	public PhraseListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public PhraseListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PhraseListView(Context context, String keyList) {
		super(context);
		init();
	}

	private void init() {
		setFastScrollEnabled(true);
		setWillNotDraw(false);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);

		float w = getWidth() / 2;
		float h = getHeight() / 2;

		if (showLetter) {
			rect.set(w - rectWidth / 2, h - rectWidth / 2, w + rectWidth / 2, h
					+ rectWidth / 2);
			canvas.drawRoundRect(rect, rectWidth * 0.1f, rectWidth * 0.1f,
					rectPaint);
			canvas.drawText(section, w, h - textPaint.ascent() / 3, textPaint);
		}
	}

	public ListHandler getHandler() {
		return listHandler;
	}

	class ListHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == MSG_DRAW_ON) {
				showLetter = true;
				section = ((String) msg.obj).toUpperCase();
			}

			if (msg.what == MSG_DRAW_OFF) {
				showLetter = false;
				PhraseListView.this.invalidate();
			}

		}

	}
}
