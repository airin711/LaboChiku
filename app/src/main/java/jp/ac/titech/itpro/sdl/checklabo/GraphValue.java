package jp.ac.titech.itpro.sdl.checklabo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by airi on 15/07/22.
 */
public class GraphValue extends View {

    float dx;
    float dy;
    String value = "";

    public GraphValue(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GraphValue(Context context, AttributeSet attrs, int defStyle){
        super(context,attrs,defStyle);
    }

    public GraphValue(Context context) {
        super(context);
    }

    public void setXY(float x, float y){
        dx = x;
        dy = y;
        postInvalidate();
    }

    public void setValue(int msec){
        String hour = String.valueOf(msec/(1000 * 60 * 60));
        String min = String.valueOf((msec/(1000 * 60)) % 60);
        value = hour + "時間" + min + "分";
        Log.d("gvalue", "hour= " + hour);
        Log.d("gvalue", "min= " + min);
        Log.d("gvalue", "value= " + value);
    }

    @Override
    public void onDraw(Canvas canvas) {

        Paint paint = new Paint();
        paint.setColor(Color.rgb(60, 179, 113));
        paint.setTextSize(30);
        paint.setStrokeWidth(8);
        canvas.drawText(value, dx-30, dy-20, paint);
    }

}
