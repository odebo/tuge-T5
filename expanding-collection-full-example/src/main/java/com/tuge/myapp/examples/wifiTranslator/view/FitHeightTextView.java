package com.tuge.myapp.examples.wifiTranslator.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.Nullable;


/**
 * Created by akitaka on 2017/9/20.
 *
 * @filename FitHeightTextView
 * @describe 根据高度自适应字体文字大小
 * @email 960576866@qq.com
 */

public class FitHeightTextView extends  TextView {

    private static final int MAX_SIZE = 1000;

    private static final int MIN_SIZE = 5;

    private TextPaint mTextPaint;

    private float mSpacingMult = 1.0f;

    private float mSpacingAdd = 0.0f;

    private boolean needAdapt = false;

    private boolean adapting = false;

    public FitHeightTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public FitHeightTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FitHeightTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        mTextPaint = new TextPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (adapting) {
            return;
        }
        if (needAdapt) {
            adaptTextSize();
        } else {
            super.onDraw(canvas);
        }
    }

    private void adaptTextSize() {
        CharSequence text = getText();
        int viewWidth = getMeasuredWidth();
        int viewHeight = getMeasuredHeight();

        if (viewWidth == 0 || viewHeight == 0 || TextUtils.isEmpty(text)) {
            return;
        }

        adapting = true;
        /* binary search */
        int bottom = MIN_SIZE, top = MAX_SIZE, mid = 0;
        while (bottom <= top) {
            mid = (bottom + top) / 2;
            mTextPaint.setTextSize(mid);
            int textWidth = (int) mTextPaint.measureText(text, 0, text.length());
            int textHeight = getTextHeight(text, viewWidth);
            if (textWidth < viewWidth && textHeight < viewHeight) {
                bottom = mid + 1;
            } else {
                top = mid - 1;
            }
        }

        int newSize = mid - 1;
        setTextSize(TypedValue.COMPLEX_UNIT_PX, newSize);

        adapting = false;
        needAdapt = false;

        invalidate();
    }

    private int getTextHeight(CharSequence text, int targetWidth) {
        StaticLayout layout = new StaticLayout(text, mTextPaint, targetWidth,
                Layout.Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, true);
        return layout.getHeight();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        needAdapt = true;
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        needAdapt = true;
    }

    @Override
    public void setLineSpacing(float add, float mult) {
        super.setLineSpacing(add, mult);
        mSpacingMult = mult;
        mSpacingAdd = add;
    }
}
// {
//
//
//    // Attributes
//    private Paint testPaint;
//    private float cTextSize;
//
//
//    public FitHeightTextView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//
//    /**
//     * Re size the font so the specified text fits in the text box * assuming
//     * the text box is the specified width.
//     * 在此方法中学习到：getTextSize返回值是以像素(px)为单位的，而setTextSize()是以sp为单位的，
//     * 因此要这样设置setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
//     */
//    private void refitText(String text, int textWidth) {
//        if (textWidth > 0) {
//            testPaint = new Paint();
//            testPaint.set(this.getPaint());
//// 获得当前TextView的有效宽度
//            int availableWidth = textWidth - this.getPaddingLeft()
//                    - this.getPaddingRight();
//            float[] widths = new float[text.length()];
//            Rect rect = new Rect();
//            testPaint.getTextBounds(text, 0, text.length(), rect);
//// 所有字符串所占像素宽度
//            int textWidths = rect.width();
//            cTextSize = this.getTextSize();// 这个返回的单位为px
//            while (textWidths > availableWidth) {
//                cTextSize = cTextSize - 1;
//                testPaint.setTextSize(cTextSize);// 这里传入的单位是px
//                textWidths = testPaint.getTextWidths(text, widths);
//            }
//            this.setTextSize(TypedValue.COMPLEX_UNIT_PX, cTextSize);// 这里制定传入的单位是px
//        }
//    };
//
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        refitText(getText().toString(), this.getWidth());
//    }
//}

