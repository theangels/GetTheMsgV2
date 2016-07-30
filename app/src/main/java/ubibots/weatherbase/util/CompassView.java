package ubibots.weatherbase.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

import ubibots.weatherbase.R;

/**
 * 自定义View类
 *
 * @author liuyazhuang
 */
public class CompassView extends View {

    //显示的方向
    private float bearing;

    private Paint markerPaint;
    private Paint textPaint;
    private Paint circlePaint;

    private String northString;
    private String eastString;
    private String southString;
    private String westString;

    private int textHeight;

    public CompassView(Context context) {
        super(context);
        initCompassView(context);
    }

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initCompassView(context);
    }

    public CompassView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initCompassView(context);
    }

    /**
     * 初始化视图的各个属性
     */
    protected void initCompassView(Context context) {
        setFocusable(true);

        Resources r = this.getResources();

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(ContextCompat.getColor(context, R.color.background_color));
        circlePaint.setStrokeWidth(1);
        circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        northString = r.getString(R.string.cardinal_north);
        eastString = r.getString(R.string.cardinal_east);
        southString = r.getString(R.string.cardinal_south);
        westString = r.getString(R.string.cardinal_west);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(ContextCompat.getColor(context, R.color.text_color));
        textPaint.setTextSize(16);

        textHeight = (int) textPaint.measureText("yY");

        markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        markerPaint.setColor(ContextCompat.getColor(context, R.color.marker_color));
    }


    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        // TODO Auto-generated method stub
        super.dispatchPopulateAccessibilityEvent(event);
        if (isShown()) {
            String bearingStr = String.valueOf(bearing);
            if (bearingStr.length() > AccessibilityEvent.MAX_TEXT_LENGTH) {
                bearingStr = bearingStr.substring(0, AccessibilityEvent.MAX_TEXT_LENGTH);
                event.getText().add(bearingStr);
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //罗盘是一个尽可能填充更多的空间的圆，通过设置最短的边界，高度或者宽度来设置测量的尺寸
        int measureWidth = measure(widthMeasureSpec);
        int measureHeight = measure(heightMeasureSpec);

        int d = Math.min(measureWidth, measureHeight);
        setMeasuredDimension(d, d);
    }

    /**
     * 解码数据值
     *
     * @param measureSpec
     * @return
     */
    private int measure(int measureSpec) {
        int result = 0;
        //对测量说明进行解码
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        //如果没有指定界限，则返回默认大小200
        if (specMode == MeasureSpec.UNSPECIFIED) {
            result = 200;
        } else {
            //由于是希望填充可用的空间，所以总是返回整个可用的边界
            result = specSize;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
//      super.onDraw(canvas);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        int px = measuredWidth / 2;
        int py = measuredHeight / 2;
        //取较小的值为半径
        int radius = Math.min(px, py);
        //绘制背景
        canvas.drawCircle(px, py, radius, circlePaint);
        canvas.save();
        canvas.rotate(-bearing, px, py);

        int textWidth = (int) textPaint.measureText("W");
        int cadinalX = px - textWidth / 2;
        int cadinalY = py - radius + textHeight;
        //每15度绘制一个标记，每45度绘制一个文本
        for (int i = 0; i < 24; i++) {
            //绘制一个标记
            canvas.drawLine(px, px - radius, py, py - radius + 10, markerPaint);
            canvas.save();
            canvas.translate(0, textHeight);
            //绘制基本方位
            if (i % 6 == 0) {
                String dirString = "";
                switch (i) {
                    case 0:
                        dirString = northString;
                        int arrowY = 2 * textHeight;
                        canvas.drawLine(px, arrowY, px - 5, 3 * textHeight, markerPaint);
                        canvas.drawLine(px, arrowY, px + 5, 3 * textHeight, markerPaint);

                        break;
                    case 6:
                        dirString = eastString;
                        break;
                    case 12:
                        dirString = southString;
                        break;
                    case 18:
                        dirString = westString;
                        break;
                    default:
                        break;
                }
                canvas.drawText(dirString, cadinalX, cadinalY, textPaint);
            } else if (i % 3 == 0) {
                //每45度绘制文本
                String angle = String.valueOf(i * 15);
                float angleTextWidth = textPaint.measureText(angle);

                int angleTextX = (int) (px - angleTextWidth / 2);
                int angleTextY = py - radius + textHeight;
                canvas.drawText(angle, angleTextX, angleTextY, textPaint);
            }
            canvas.restore();
            canvas.rotate(15, px, py);
        }
        canvas.restore();
    }
}