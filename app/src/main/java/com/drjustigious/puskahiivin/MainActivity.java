package com.drjustigious.puskahiivin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

    private CanvasView canvasView;
    private ConstraintLayout mainContentLayout;
    private SituationModel situationModel;
    private Paint paint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Construct and enter the main view
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        canvasView = new CanvasView(this);
        mainContentLayout = findViewById(R.id.mainContentLayout);
        mainContentLayout.addView(canvasView);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        // Initialize the drawing utilities
        paint = new Paint();
        paint.setAntiAlias(true);


        // Initialize the situation model
        situationModel = new SituationModel(canvasView);
    }


    @Override
    protected void onStop() {
        // The activity loses focus
        super.onStop();
        situationModel.stopTicking();
    }


    @Override
    protected void onStart() {
        // The activity (re)gains focus
        super.onStart();
        situationModel.startTicking();
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

        private int viewWidth, viewHeight;

        CanvasView(Context context) {
            super(context);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            this.viewWidth = w;
            this.viewHeight = h;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

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

            // draw some test shapes
            paint.setColor(Color.RED);
            canvas.drawRect(0, 0, viewWidth*situationModel.boxScale, viewHeight*situationModel.boxScale, paint);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.GRAY);
            paint.setTextSize(40);
            canvas.drawText("Testing...", 128, 512, paint);
        }
    }
}
