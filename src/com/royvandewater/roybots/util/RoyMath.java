package com.royvandewater.roybots.util;

public class RoyMath {

    /**
     * Constrains the input angle between π and -π
     * 
     * @param angle
     * @return angle where -π <= angle <= π
     */
    public static double constrain(double angle) {
        if (angle < -1 * Math.PI)
            return constrain(angle + (2 * Math.PI));
        else if (angle > Math.PI)
            return constrain(angle - (2 * Math.PI));
        return angle;
    }
}
