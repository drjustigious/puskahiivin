package com.drjustigious.puskahiivin;
import android.os.Handler;
import android.view.View;

import static java.lang.Math.min;
import static java.lang.Math.max;

class SituationModel {

    float boxScale = 0;


    private float boxScaleSpeed = 0.96f;
    private boolean scaleGoingUp = true;

    private static final long MINIMUM_TICK_WAIT = 10; // Always wait at least this time (in ms) between ticks
    private static final long MAXIMUM_TICK_WAIT = 2000; // Never wait longer than this time (in ms) between ticks

    private long tickInterval = 100; // Target UI tick time interval, in ms
    private long tickEndTime = tickInterval;
    private long previousTickEndTime = 0;
    private long timeUntilNextTick;
    private float dt = tickInterval/1000.0f;

    private Handler tickingHandler = new Handler();
    private boolean isTicking = false;
    private View canvasView; // The view which the situation model should invalidate when a redraw is needed
    private MapGrid mapGrid = new MapGrid(256f,512f,45f);

    private Runnable tick = new Runnable() {
        public void run() {

            mapGrid.orientation += 15f*dt;

            // Check if any drawable things have been created or removed since last tick
            DrawableThing.updateInstanceLists();

            // Schedule a redraw for the associated view
            canvasView.invalidate();

            // Calculate and regulate the time to wait until next tick,
            // trying to keep the tick interval constant regardless of load
            tickEndTime = System.currentTimeMillis();
            timeUntilNextTick = 2*tickInterval - max(tickEndTime - previousTickEndTime, tickInterval);

            timeUntilNextTick = min(timeUntilNextTick, MAXIMUM_TICK_WAIT);
            timeUntilNextTick = max(timeUntilNextTick, MINIMUM_TICK_WAIT);

            dt = timeUntilNextTick/1000.0f;
            previousTickEndTime = tickEndTime;
            tickingHandler.postDelayed(tick, timeUntilNextTick);
        }
    };


    SituationModel(View canvasView) {
        this.canvasView = canvasView;
    }

    void setTickInterval(long newInterval) {
        this.tickInterval = newInterval;
    }

    void startTicking() {
        // Start the update ticker, unless it is already going

        if (!isTicking) {
            tickingHandler.postDelayed(tick, tickInterval);
        }
        else {
            System.out.println("Tried to start SituationModel ticker, but it is already running");
        }
        isTicking = true;
    }

    void stopTicking() {
        // Stop the update ticker

        if (isTicking) {
            tickingHandler.removeCallbacks(tick);
        }
        else {
            System.out.println("Tried to stop SituationModel ticker, but it has already been stopped");
        }
        isTicking = false;
    }
}
