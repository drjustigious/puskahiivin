package com.drjustigious.puskahiivin;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.content.ContextCompat;

import static com.drjustigious.puskahiivin.MainActivity.getAppContext;

class MapGrid extends DrawableThing {

    private int divisionsMain = 9;
    private int divisionsSubgrid = 5;
    private float gridsizeMain = 256f;
    private float gridsizeSubgrid = gridsizeMain/divisionsSubgrid;

    private float[] dashIntervalsMain = new float[]{20f, 60f};
    private float[] dashIntervalsSubgrid = new float[]{5f, 5f};
    private float dashPhaseMain = 0;
    float dashPhaseSubgrid = 0;

    private int colorMain = ContextCompat.getColor(getAppContext(), R.color.colorGrid);
    private int colorSubgrid = ContextCompat.getColor(getAppContext(), R.color.colorGrid);
    private int thicknessMain = 3;
    private int thicknessSubgrid = 1;

    private Path gridPathMain;
    private Path gridPathSubgrid;
    private Path gridPathSubgridTemplate;
    private DashPathEffect dashPathEffectMain;
    private DashPathEffect dashPathEffectSubgrid;

    protected MapGrid() {
        this(0,0,0);
    }

    protected MapGrid(float x, float y, float orientation) {
        super(x, y, orientation);

        gridPathMain = new Path();
        gridPathSubgrid = new Path();
        gridPathSubgridTemplate = new Path();
        dashPathEffectMain = null; //new DashPathEffect(dashIntervalsMain, dashPhaseMain);
        dashPathEffectSubgrid = new DashPathEffect(dashIntervalsSubgrid, dashPhaseSubgrid);

        // Construct the main grid
        rebuildGridPaths();
    }

    private void rebuildGridPaths() {
        gridPathSubgridTemplate.reset();
        gridPathSubgrid.reset();
        gridPathMain.reset();

        // Subgrid template
        for (int i = 0; i < divisionsSubgrid; i++) {
            // Draw a horizontal line
            gridPathSubgridTemplate.moveTo(0, i*gridsizeSubgrid);
            gridPathSubgridTemplate.lineTo(gridsizeMain, i*gridsizeSubgrid);

            // Draw a vertical line
            gridPathSubgridTemplate.moveTo(i*gridsizeSubgrid, 0);
            gridPathSubgridTemplate.lineTo(i*gridsizeSubgrid, gridsizeMain);
        }

        // Visible grids
        for (int i = 0; i < divisionsMain; i++) {
            for (int j = 0; j < divisionsMain; j++) {
                // Tile the subgrid
                gridPathSubgrid.addPath(gridPathSubgridTemplate, i*gridsizeMain, j*gridsizeMain);

                // Draw a horizontal line
                gridPathMain.moveTo(0, i*gridsizeMain);
                gridPathMain.lineTo(gridsizeMain*divisionsMain, i*gridsizeMain);

                // Draw a vertical line
                gridPathMain.moveTo(i*gridsizeMain, 0);
                gridPathMain.lineTo(i*gridsizeMain, gridsizeMain*divisionsMain);
            }
        }

        // Draw the closing horizontal line on main grid
        gridPathMain.moveTo(0, divisionsMain*gridsizeMain);
        gridPathMain.lineTo(gridsizeMain*divisionsMain, divisionsMain*gridsizeMain);

        // Draw the closing vertical line on main grid
        gridPathMain.moveTo(divisionsMain*gridsizeMain, 0);
        gridPathMain.lineTo(divisionsMain*gridsizeMain, gridsizeMain*divisionsMain);

        // Offset the paths to get the origin at the center
        gridPathMain.offset(-gridsizeMain*divisionsMain/2f, -gridsizeMain*divisionsMain/2f);
        gridPathSubgrid.offset(-gridsizeMain*divisionsMain/2f, -gridsizeMain*divisionsMain/2f);
    }

    @Override
    protected void draw(Paint paint, Canvas canvas) {
        paint.setStyle(Paint.Style.STROKE);

        // Draw the subgrid
        paint.setColor(colorSubgrid);
        paint.setStrokeWidth(thicknessSubgrid);
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(dashPathEffectSubgrid);
        canvas.drawPath(gridPathSubgrid, paint);

        // Draw the main grid
        paint.setColor(colorMain);
        paint.setStrokeWidth(thicknessMain);
        paint.setPathEffect(dashPathEffectMain);
        canvas.drawPath(gridPathMain, paint);
    }
}
