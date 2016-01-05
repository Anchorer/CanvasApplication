package org.anchorer.l.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.anchorer.l.consts.Const;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * 画布
 * Created by Anchorer on 2016/1/4.
 */
public class CanvasView extends View {

    private final String TAG = "TEST";

    // canvas tools
    private Canvas mBitmapCanvas;
    private Path mPath;
    private Paint mPaint;
    private ArrayList<Path> mPaths = new ArrayList<>();
    private ArrayList<Path> mUndoPaths = new ArrayList<>();

    // variables
    private float mX, mY;
    private boolean mNeedSaveFile;
    private static final float TOLERANCE = 5;

    public CanvasView(Context c, AttributeSet attrs) {
        super(c, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);

        // we set a new Path
        mPath = new Path();

        // and we set a new Paint with the desired attributes
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(4f);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        // init canvas
        mBitmapCanvas = new Canvas();

        // set DrawableCache
        setDrawingCacheBackgroundColor(c.getResources().getColor(android.R.color.white));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (Path p : mPaths) {
            canvas.drawPath(p, mPaint);
        }
        canvas.drawPath(mPath, mPaint);

        // TODO move to child thread
//        saveBitmapToFile();
    }

    /**
     * Save Bitmap To SDCard
     */
    private void saveBitmapToFile() {
        if (mNeedSaveFile) {
            mNeedSaveFile = false;
            try {
                setDrawingCacheEnabled(true);
                Bitmap bitmap = getDrawingCache(true);
                String path = Environment.getExternalStorageDirectory() + Const.PATH_BITMAP + System.currentTimeMillis() + ".jpg";
                File newFile = new File(path);
                if (!newFile.exists()) {
                    newFile.createNewFile();
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, new FileOutputStream(newFile));
                setDrawingCacheEnabled(false);
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
        mUndoPaths.clear();
        mPath.reset();
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
        mBitmapCanvas.drawPath(mPath, mPaint);
        mPaths.add(mPath);
        mPath = new Path();
        mNeedSaveFile = true;
    }

    /**
     * 向目标坐标画线段
     * @param targetPointX  目标X坐标
     * @param targetPointY  目标Y坐标
     */
/*
    public void drawToPoint(int targetPointX, int targetPointY) {
        mPath.lineTo(targetPointX, targetPointY);
        mX = targetPointX;
        mY = targetPointY;
        invalidate();
    }
*/

    /**
     * 清空画布
     */
    public void clearCanvas() {
        mPath.reset();
        invalidate();
    }

    /**
     * 撤销
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void revert() {
        if (mPaths.size() > 0) {
            mUndoPaths.add(mPaths.remove(mPaths.size() - 1));
            invalidate();
        } else {
            Snackbar.make(this, "已无法撤销", Snackbar.LENGTH_SHORT).show();
        }
    }

}
