package org.anchorer.l.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 画布
 * Created by Anchorer on 2016/1/4.
 */
public class CanvasView extends View {

    private final String TAG = "TEST";

    private Path mPath;
    private Paint mPaint;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private float mX, mY;
    private static final float TOLERANCE = 5;

    private boolean mNeedSaveFile;

    public CanvasView(Context c, AttributeSet attrs) {
        super(c, attrs);

        // we set a new Path
        mPath = new Path();

        // and we set a new Paint with the desired attributes
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(4f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(TAG, "CanvasView onSizeChanged: " + w + ", " + h);
        super.onSizeChanged(w, h, oldw, oldh);
        initBitmapAndCanvas(w, h);
    }

    /**
     * 初始化Bitmap和Canvas
     * @param width
     * @param height
     */
    private void initBitmapAndCanvas(int width, int height) {
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int[] allPixels = new int[mBitmap.getWidth() * mBitmap.getHeight()];
        mBitmap.getPixels(allPixels, 0, mBitmap.getWidth(), 0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        for (int i = 0; i < allPixels.length; i++) {
            allPixels[i] = Color.WHITE;
        }
        mBitmap.setPixels(allPixels, 0, mBitmap.getWidth(), 0, 0, mBitmap.getWidth(), mBitmap.getHeight());

        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw the mPath with the mPaint on the canvas when onDraw
        canvas.drawPath(mPath, mPaint);
//        mCanvas.drawPath(mPath, mPaint);
        saveBitmapToFile();
    }

    /**
     * Save Bitmap To SDCard
     */
    private void saveBitmapToFile() {
        if (mNeedSaveFile) {
            mNeedSaveFile = false;
            try {
                String path = Environment.getExternalStorageDirectory() + "/test/" + System.currentTimeMillis() + ".jpg";
                File newFile = new File(path);
                if (!newFile.exists()) {
                    newFile.createNewFile();
                }
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 80, new FileOutputStream(newFile));
            } catch (Exception e) {
                Log.e(TAG, "Exception", e);
            }
        }
    }

    //override the onTouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                moveTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                upTouch();
                invalidate();
                break;
        }
        return true;
    }

    // when ACTION_DOWN start touch according to the x,y values
    private void startTouch(float x, float y) {
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    // when ACTION_MOVE move touch according to the x,y values
    private void moveTouch(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOLERANCE || dy >= TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    // when ACTION_UP stop touch
    private void upTouch() {
        mPath.lineTo(mX, mY);
        mNeedSaveFile = true;
    }

    /**
     * 向目标坐标画线段
     * @param targetPointX  目标X坐标
     * @param targetPointY  目标Y坐标
     */
    public void drawToPoint(int targetPointX, int targetPointY) {
        mPath.lineTo(targetPointX, targetPointY);
        mX = targetPointX;
        mY = targetPointY;
        invalidate();
    }

    /**
     * 清空画布
     */
    public void clearCanvas() {
        mPath.reset();
        invalidate();
    }
}
