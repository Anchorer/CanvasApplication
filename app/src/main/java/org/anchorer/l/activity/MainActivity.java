package org.anchorer.l.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.anchorer.l.R;
import org.anchorer.l.view.CanvasView;

public class MainActivity extends AppCompatActivity {

    private CanvasView mCanvasView;
//    private EditText mXView, mYView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCanvasView = (CanvasView) findViewById(R.id.main_canvas);
//        mXView = (EditText) findViewById(R.id.main_x);
//        mYView = (EditText) findViewById(R.id.main_y);

        findViewById(R.id.fab_revert).setOnClickListener(v -> mCanvasView.revert());
        findViewById(R.id.fab_clear).setOnClickListener(v -> mCanvasView.clearCanvas());
//        findViewById(R.id.main_draw).setOnClickListener(v -> {
//            int x = Integer.parseInt(mXView.getText().toString());
//            int y = Integer.parseInt(mYView.getText().toString());
//            mCanvasView.drawToPoint(x, y);
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
