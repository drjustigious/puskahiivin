package com.drjustigious.puskahiivin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ConstraintLayout mainContentLayout = findViewById(R.id.mainContentLayout);
        mainContentLayout.addView(new CanvasView(this));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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

    private class CanvasView extends View {

        int initX = 200, initY = 200, radius = 100, rectWidth = 500, rectHeight = 400;

        public CanvasView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            Paint paint = new Paint();

            // TODO: Measure the actual screen size and draw accordingly
            // TODO: Map elements and transformations using android.graphics.Path
            /*
                // Path transformation example
                Path oval = new Path();
                Matrix matrix = new Matrix();
                RectF ovalRect = new RectF(width / OVAL_FACTOR, 0, width - (width / OVAL_FACTOR), height);

                oval.addOval(ovalRect, Path.Direction.CW);
                matrix.postRotate(ROTATION, width / 2, height / 2);
                oval.transform(matrix, oval);
                canvas.drawPath(oval, paint);
             */

            // clear the whole canvas
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.BLACK);
            canvas.drawPaint(paint);

            // draw blue circle with anti aliasing turned on
            paint.setAntiAlias(true);
            paint.setColor(Color.BLUE);
            canvas.drawCircle(initX, initY, radius, paint);

            // draw red rectangle with anti aliasing turned off
            paint.setAntiAlias(true);
            paint.setColor(Color.RED);
            canvas.drawRect(initX, initY + 300, rectWidth + radius, initY + rectHeight , paint);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.GREEN);
            paint.setTextSize(40);
            canvas.drawText("CoderzHeaven, Heaven of all working codes", initX, initY + 600, paint);

            //if (Build.VERSION.SDK_INT != Build.VERSION_CODES.M)
            //    canvas.restore();
        }
    }
}
