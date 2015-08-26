package com.angcyo.camerademo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by angcyo on 2015-04-09 009.
 */
public class TestView extends View {
    public TestView(Context context) {
        super(context);
    }

    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getContext().getResources().getDisplayMetrics()));
        paint.setColor(Color.parseColor("#ff0000"));
//        paint.setTextSize(20);

        canvas.drawText("Hello", 0, 80, paint);
//        canvas.drawText();
//        canvas.drawColor(Color.parseColor("#2000FF00"));
//        paint.setColor(Color.RED);


//        Bitmap bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
//        Canvas canvas1 = new Canvas(bitmap);
//        Paint paint1 = new Paint();
//        paint1.setColor(Color.GREEN);
//        canvas1.drawCircle(getWidth() / 2, getHeight() / 2, 260, paint1);
//        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//        Canvas canvas1 = new Canvas(bitmap1);
//        canvas1.drawBitmap(bitmap1, getWidth() / 2 - 100, getHeight() / 2 - 100, null);

//        paint1.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        canvas1.drawCircle(getWidth() / 2, getHeight() / 2, 30, paint1);

//        canvas.drawBitmap(bitmap, 20, 20, paint1);

//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        canvas.drawCircle(getWidth() / 2, getHeight() / 2, 60, paint);
    }
}
