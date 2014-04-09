package org.michaelevans.chromahashview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Michael Evans <michaelcevans10@gmail.com>
 */
public class ChromaHashView extends EditText {
    private static int DEFAULT_NUM_OF_VALUES = 3;
    private static int MINIMUM_CHARACTER_THRESHOLD = 6;

    Paint paint = new Paint();
    private String[] colors;
    MessageDigest md5 = null;

    public ChromaHashView(Context context) {
        super(context);
        init();
    }

    public ChromaHashView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChromaHashView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException nsaex) {
        }
        setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if(s.length() > 0) {
                    md5.reset();
                    md5.update((text).getBytes(Charset.forName("UTF-8")));
                    byte[] result = md5.digest();
                    StringBuilder hexString = new StringBuilder();
                    for (byte aResult : result) {
                        String newByteFull = "00" + Integer.toHexString(0xFF & aResult);
                        hexString.append(newByteFull.substring(newByteFull.length() - 2));
                    }
                    String md5hash = hexString.toString();

                    colors = new String[]{md5hash.substring(0, 6), md5hash.substring(6, 12), md5hash.substring(12, 18), md5hash.substring(18, 24), md5hash.substring(24, 30)};
                    if (s.length() < MINIMUM_CHARACTER_THRESHOLD) {
                        for (int i = 0; i < colors.length; i++) {
                            colors[i] = colorToGreyScale(colors[i]);
                        }
                    }
                }else{
                    colors = null;
                }
            }
        });
    }

    private String colorToGreyScale(String color) {
        String r = color.substring(0, 2);
        return r + r + r;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (colors != null) {
            setPadding(getPaddingLeft(), getPaddingTop(), (20 * DEFAULT_NUM_OF_VALUES + 30), getPaddingBottom());
            for (int i = 0; i < DEFAULT_NUM_OF_VALUES; i++) {
                paint.setColor(Color.parseColor("#" + colors[i]));
                canvas.drawRect(getWidth() + getScrollX() - 20 * i - 35, 15, getWidth() + getScrollX() - 20 * i - 15, getHeight() - 15, paint);
            }
        }
        super.onDraw(canvas);
    }

}
