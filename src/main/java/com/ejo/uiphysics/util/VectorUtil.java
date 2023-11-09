package com.ejo.uiphysics.util;

import com.ejo.glowlib.math.Angle;
import com.ejo.glowlib.math.Vector;
import com.ejo.uiphysics.elements.PhysicsObjectUI;

public class VectorUtil {

    public static Vector calculateVectorBetweenObjects(PhysicsObjectUI object1, PhysicsObjectUI object2) {
        return object1.getCenter().getSubtracted(object2.getCenter());
    }

    public static Vector calculateVectorBetweenPoints(Vector point1, Vector point2) {
        return point1.getSubtracted(point2);
    }

    public static Vector calculateVectorBetweenObjectAndPoint(PhysicsObjectUI object, Vector point) {
        return object.getCenter().getSubtracted(point);
    }

    /**
     * Since the Y direction is flipped on windows, this special method returns the logical vector with a flipped y direction
     * @param vector
     * @return
     */
    public static Angle getUIAngleFromVector(Vector vector) {
        return new Angle(Math.atan2(-vector.getY(),vector.getX()));
    }

}