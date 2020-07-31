package com.thanoscorp.athene.utility;

/**
 *
 *  Used to Snap the calendar views to positions after TOUCH_UP
 *
 */

public class TouchSnapper {

    public static float MIN_ORDINATE = 0;
    public static float MAX_ORDINATE = 0;
    public static float GRAVITY = 1.005f;

    private static float midPoint;

    public static float getSnapOrdinates(float ordinate) {
        if (MIN_ORDINATE == 0 && MAX_ORDINATE == 0) return 0;
        if (MIN_ORDINATE == MAX_ORDINATE) return 0;
        midPoint = ((MAX_ORDINATE - MIN_ORDINATE) / 2);
        float subDrag;

        subDrag = ((ordinate - midPoint) / midPoint); // ranges between 0 - 1
        ordinate = ordinate * (((subDrag) * (GRAVITY - 1)) + 1);

        if (ordinate > MAX_ORDINATE) return MAX_ORDINATE;
        if (ordinate < MIN_ORDINATE) return MIN_ORDINATE;
        return ordinate;
    }

    public static float getEndOrdinate(float ordinate) {
        if (MIN_ORDINATE == 0 && MAX_ORDINATE == 0) return 0;
        if (MIN_ORDINATE == MAX_ORDINATE) return 0;
        midPoint = ((MAX_ORDINATE - MIN_ORDINATE) / 2);
        if (ordinate < (midPoint + (midPoint / 2))) return MIN_ORDINATE;
        return MAX_ORDINATE;
    }

    public static float getOtherOrdinate(float velocity){
        if (MIN_ORDINATE == 0 && MAX_ORDINATE == 0) return 0;
        if (MIN_ORDINATE == MAX_ORDINATE) return 0;
        if(velocity > 0) return MAX_ORDINATE;
        return MIN_ORDINATE;
    }

    public static void init(float min, float max) {
        MIN_ORDINATE = min;
        MAX_ORDINATE = max;
    }
}
