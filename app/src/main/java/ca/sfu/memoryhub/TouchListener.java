package ca.sfu.memoryhub;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import ca.sfu.memoryhub.puzzle;

public class TouchListener implements View.OnTouchListener {
    private float xDelta;
    private float yDelta;
//    private boolean canMove = true;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();

        Log.i("onTouch", String.valueOf(x) + "    " + String.valueOf(y));

        double[] sol = (double[]) v.getTag();

        int stop = (int) sol[2];
        if(stop == 1) {
            return true;
        }

        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
        switch (event.getAction() & event.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                xDelta = x - lParams.leftMargin;
                yDelta = y - lParams.topMargin;

                break;
            case MotionEvent.ACTION_MOVE:
                lParams.leftMargin = (int) (x - xDelta);
                lParams.topMargin = (int) (y - yDelta);
                v.setLayoutParams(lParams);


                break;
            case MotionEvent.ACTION_UP:
                int[] onScreenCoords = new int[2];
                v.getLocationOnScreen(onScreenCoords);
                double xDiff = abs(sol[0] - onScreenCoords[0]);
                double yDiff = abs(sol[1] - onScreenCoords[1]);
                int tol = 50;
                if(xDiff - 40 <= tol && yDiff-150 <= tol){
                    sol[2] = 1;
                    Log.i("TOUCH", "in here: ");

                    v.setX((float) sol[0]);
                    v.setY((float)sol[1]);

                }
                Log.i("Y AND X DIFF", String.valueOf(onScreenCoords[0]) + " && " + String.valueOf(onScreenCoords[1]));
                break;
        }

        return true;
    }
}