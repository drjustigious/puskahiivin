package com.drjustigious.puskahiivin;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

abstract class DrawableThing {
    static List<DrawableThing> listInstances = new ArrayList<DrawableThing>();
    static List<DrawableThing> listAddedInstances = new ArrayList<DrawableThing>();
    static List<DrawableThing> listRemovedInstances = new ArrayList<DrawableThing>();

    float x, y, orientation;

    abstract protected void draw(Paint paint, Canvas canvas);

    protected DrawableThing(float x, float y, float orientation) {
        listAddedInstances.add(this);
        this.x = x;
        this.y = y;
        this.orientation = orientation;
    }

    protected DrawableThing(float x, float y) {
        this(x, y, 0);
    }

    protected DrawableThing() {
        this(0, 0, 0);
    }

    protected void remove() {
        listRemovedInstances.add(this);
    }

    protected static void updateInstanceLists() {
        // Process the lists of added and destroyed instances to update the
        // list of active instances

        // Add pending instances
        for (DrawableThing addedInst : listAddedInstances) {
            listInstances.add(addedInst);
        }
        listAddedInstances.clear();

        // Remove pending instances
        for (DrawableThing removedInst : listRemovedInstances) {
            // This will fail silently if listInstances does not contain removedInst
            listInstances.remove(removedInst);
        }
        listRemovedInstances.clear();
    }
}
