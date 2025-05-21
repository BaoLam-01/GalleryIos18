package com.base.capva.text;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import com.base.capva.text.text_animate.BaseTextAnimate;
import com.base.capva.utils.CapVaGravity;

public class CustomTextView extends AppCompatEditText {

    private int gravity = CapVaGravity.START;

    private int lineY;
    private int viewWidth;
    public static final String TWO_CHINESE_BLANK = "  ";
    private BaseTextAnimate animate;

    public CustomTextView(Context context) {
        super(context);
        init();
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setEnabled(false);
        setPadding(0, 0, 0, 0);
        setBackgroundColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setCustomInsertionActionModeCallback(callback);
        }
        setCustomSelectionActionModeCallback(callback);
        setLongClickable(false);
        setTextIsSelectable(false);
        setTextSize(10);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    public void setAnimate(BaseTextAnimate animate) {
        this.animate = animate;
    }

    public void setTimeDelay(int timeDelay) {
        if (animate != null) {
            animate.setTimeDelay(timeDelay);
        }
    }

    public void prepare() {
        if (animate != null) {
            animate.prepare();
        }
    }

    public void startAnimation() {
        if (animate != null) {
            animate.startAnimation();
        }
    }

    public void stopAnimation() {
        if (animate != null) {
            animate.stopAnimation();
        }
    }

    public void setFrameInTime(int time) {
        if (animate != null) {
            animate.setFrameInTime(time);
        }
    }

    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
        getPaint().setColor(color);
    }

    private final ActionMode.Callback callback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            if (menu != null) {
                menu.clear();
            }
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {

        }
    };

    public void setGravity(int gravity) {
        this.gravity = gravity;
        if (gravity == CapVaGravity.JUSTIFY) {
            super.setGravity(Gravity.NO_GRAVITY);
        } else {
            super.setGravity(gravity);
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        if (gravity == CapVaGravity.JUSTIFY) {
//            drawTextJustify(canvas);
//        } else {
//            getLayout().draw(canvas);
//        }
        if (animate != null) {
            animate.onDraw(canvas);
        }
    }

    private void drawTextJustify(Canvas canvas) {
        TextPaint paint = getPaint();
        paint.setColor(getCurrentTextColor());
        paint.drawableState = getDrawableState();
        viewWidth = getMeasuredWidth();
        String text = getText().toString();
        lineY = 0;
        lineY += getTextSize();
        Layout layout = getLayout();

        if (layout == null) {
            return;
        }

        Paint.FontMetrics fm = paint.getFontMetrics();

        int textHeight = (int) (Math.ceil(fm.descent - fm.ascent));
        textHeight = (int) (textHeight * layout.getSpacingMultiplier() + layout.getSpacingAdd());

        for (int i = 0; i < layout.getLineCount(); i++) {
            int lineStart = layout.getLineStart(i);
            int lineEnd = layout.getLineEnd(i);
            float width = StaticLayout.getDesiredWidth(text, lineStart, lineEnd, getPaint());
            String line = text.substring(lineStart, lineEnd);
            if (needScale(line) && i < layout.getLineCount() - 1) {
                drawScaledText(canvas, lineStart, line, width);
            } else {
                canvas.drawText(line, 0, lineY, paint);
            }
            lineY += textHeight;
        }
    }

    private void drawScaledText(Canvas canvas, int lineStart, String line, float lineWidth) {
        float x = 0;
        if (isFirstLineOfParagraph(lineStart, line)) {
            String blanks = "  ";
            canvas.drawText(blanks, x, lineY, getPaint());
            float bw = StaticLayout.getDesiredWidth(blanks, getPaint());
            x += bw;

            line = line.substring(3);
        }

        int gapCount = line.length() - 1;
        int i = 0;
        if (line.length() > 2 && line.charAt(0) == 12288 && line.charAt(1) == 12288) {
            String substring = line.substring(0, 2);
            float cw = StaticLayout.getDesiredWidth(substring, getPaint());
            canvas.drawText(substring, x, lineY, getPaint());
            x += cw;
            i += 2;
        }

        float d = (viewWidth - lineWidth) / gapCount;
        for (; i < line.length(); i++) {
            String c = String.valueOf(line.charAt(i));
            float cw = StaticLayout.getDesiredWidth(c, getPaint());
            canvas.drawText(c, x, lineY, getPaint());
            x += cw + d;
        }
    }

    private boolean isFirstLineOfParagraph(int lineStart, String line) {
        return line.length() > 3 && line.charAt(0) == ' ' && line.charAt(1) == ' ';
    }

    private boolean needScale(String line) {
        if (line == null || line.length() == 0) {
            return false;
        } else {
            return line.charAt(line.length() - 1) != '\n';
        }
    }
}
