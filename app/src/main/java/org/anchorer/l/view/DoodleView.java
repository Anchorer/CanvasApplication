package org.anchorer.l.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Anchorer on 2016/1/5.
 */
public class DoodleView extends View {
    private Canvas bitmapCanvas; // used to draw on bitmap
    private Paint paintLine; // used to draw lines onto bitmap
    private Path mPath;
    private ArrayList<Path> paths = new ArrayList<Path>();
    private ArrayList<Path> undonePaths = new ArrayList<Path>();
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

// DoodleView constructor initializes the DoodleView

    public DoodleView(Context context, AttributeSet attrs)
    {
        super(context, attrs); // pass context to View's constructor
        setFocusable(true);
        setFocusableInTouchMode(true);

        // set the initial display settings for the painted line
        paintLine = new Paint();
        paintLine.setAntiAlias(true); // smooth edges of drawn line
        paintLine.setDither(true);
        paintLine.setColor(Color.BLACK); // default color is black
        paintLine.setStyle(Paint.Style.STROKE); // solid line
        paintLine.setStrokeJoin(Paint.Join.ROUND);
        paintLine.setStrokeWidth(5); // set the default line width
        paintLine.setStrokeCap(Paint.Cap.ROUND); // rounded line ends

        bitmapCanvas = new Canvas();
        mPath = new Path();
    } // end DoodleView constructor

    @Override
    protected void onDraw(Canvas canvas)
    {
        for (Path p : paths){canvas.drawPath(p, paintLine);}
        canvas.drawPath(mPath, paintLine);
        Log.i("OnDRAWING", "REACH ON DRAW");
    }

    public boolean onTouchEvent(MotionEvent event)
    {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }

    private void touch_start(float x, float y)
    {
        undonePaths.clear();
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y)
    {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE)
        {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up()
    {
        mPath.lineTo(mX, mY);
        bitmapCanvas.drawPath(mPath, paintLine);// commit the path to our offscreen
        paths.add(mPath);
        mPath = new Path();
    }

    public void onClickUndo()
    {
        if (paths.size()>0)
        {
            undonePaths.add(paths.remove(paths.size()-1));
            invalidate();
        }
        else Toast.makeText(getContext(), "nothing more to undo", Toast.LENGTH_SHORT).show();
    }
}
