package com.drjustigious.puskahiivin;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ALL_PERMISSIONS = 1;

    private static Context appContext;
    private CanvasView canvasView;
    private ConstraintLayout mainContentLayout;
    private SituationModel situationModel;
    private LocationTracker locationTracker;
    private Paint paint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appContext = getApplicationContext();


        // Check and, if necessary, ask for required system resource permissions
        checkPermissions();

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

    private void checkPermissions() {
        String[] requiredPermissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        };

        boolean gotAllPermissions = true;
        for (String permission : requiredPermissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                gotAllPermissions = false;
            }
        }

        if (gotAllPermissions) {
            log("System resource permissions OK");
        }
        else {
            log("Requesting additional system resource permissions");
            ActivityCompat.requestPermissions(this, requiredPermissions, REQUEST_ALL_PERMISSIONS);
        }
    }


    @Override
    protected void onStop() {
        // The activity loses focus
        super.onStop();

        if (mServiceBound) {
            unbindService(mServiceConnection);
            mServiceBound = false;
        }

        situationModel.stopTicking();
    }


    @Override
    protected void onStart() {
        // The activity (re)gains focus
        super.onStart();

        Intent intent = new Intent(this, LocationTracker.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

        situationModel.startTicking();
    }

    boolean mServiceBound = false;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationTracker.LocalBinder myBinder = (LocationTracker.LocalBinder) service;
            locationTracker = myBinder.getService();
            locationTracker.setContext(appContext);
            locationTracker.restartLocation();

            situationModel.setLocationTracker(locationTracker);

            mServiceBound = true;
            log("Bound to location tracker service");
        }
    };


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

    protected static Context getAppContext() {
        return appContext;
    }



    private class CanvasView extends View {

        private int viewWidth, viewHeight;
        private Matrix drawingTransform = new Matrix();

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

            canvas.save();

            // draw some test shapes
            //paint.setColor(Color.RED);
            //canvas.drawRect(0, 0, viewWidth*situationModel.boxScale, viewHeight*situationModel.boxScale, paint);

            // Modeled physical things and other drawables
            for (DrawableThing thing : DrawableThing.listInstances) {
                canvas.scale(2f,2f);
                canvas.translate(thing.x, thing.y);
                canvas.rotate(thing.orientation);
                thing.draw(paint, canvas);
                canvas.restore();
            }

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.GRAY);
            paint.setTextSize(40);
            canvas.drawText("Testing...", 128, 512, paint);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ALL_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    log("All permissions granted");
                } else {
                    log("All permissions denied");
                }
                return;
            }
        }
    }

    private void log(String message) {
        System.out.println("[MainActivity] "+message);
    }
}
