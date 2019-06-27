package com.drjustigious.puskahiivin;
import android.os.Handler;
import android.view.View;

import static java.lang.Math.min;
import static java.lang.Math.max;

class SituationModel {

    float boxScale = 0;


    private float boxScaleSpeed = 1;
    private boolean scaleGoingUp = true;

    private static final long MINIMUM_TICK_WAIT = 10; // Always wait at least this time (in ms) between ticks
    private static final long MAXIMUM_TICK_WAIT = 2000; // Never wait longer than this time (in ms) between ticks

    private long tickInterval = 200; // Target UI tick time interval, in ms
    private long tickStartTime, tickEndTime;
    private long timeUntilNextTick;
    private float dt = tickInterval/1000.0f;

    private Handler tickingHandler = new Handler();
    private View canvasView; // The view which the situation model should invalidate when a redraw is needed

    private Runnable tick = new Runnable() {
        public void run() {
            // Start timing the duration of this model update tick
            tickStartTime = System.currentTimeMillis();

            /* == BEGIN TICK ACTIONS == */
            // Perform the actual model updates
            if (scaleGoingUp) {
                boxScale += boxScaleSpeed*dt;
                if (boxScale > 1) {
                    boxScale = 1;
                    scaleGoingUp = false;
                }
            }
            else {
                boxScale -= boxScaleSpeed*dt;
                if (boxScale < 0) {
                    boxScale = 0;
                    scaleGoingUp = true;
                }
            }

            canvasView.invalidate();

            /* == END OF TICK ACTIONS == */
            // Calculate and regulate the time to wait until next tick,
            // trying to keep the tick interval constant regardless of load
            tickEndTime = System.currentTimeMillis();
            timeUntilNextTick = tickInterval - (tickEndTime - tickStartTime);

            timeUntilNextTick = min(timeUntilNextTick, MAXIMUM_TICK_WAIT);
            timeUntilNextTick = max(timeUntilNextTick, MINIMUM_TICK_WAIT);

            dt = timeUntilNextTick/1000.0f;
            tickingHandler.postDelayed(tick, timeUntilNextTick);
        }
    };


    SituationModel(View canvasView) {
        this.canvasView = canvasView;
        this.startTicking();
    }

    void setTickInterval(long newInterval) {
        this.tickInterval = newInterval;
    }

    void startTicking() {
        tickingHandler.postDelayed(tick, tickInterval);
    }

    void stopTicking() {
        tickingHandler.removeCallbacks(tick);
    }
}
