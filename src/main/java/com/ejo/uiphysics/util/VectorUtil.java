package com.ejo.uiphysics.util;

import com.ejo.glowlib.math.Vector;
import com.ejo.uiphysics.elements.PhysicsObjectUI;

public class VectorUtil {

    public static Vector calculateVectorBetweenObjects(PhysicsObjectUI object1, PhysicsObjectUI object2) {
        return object1.getCenter().getAdded(object2.getCenter().getMultiplied(-1));
    }

    public static Vector calculateVectorBetweenPoints(Vector point1, Vector point2) {
        return point1.getAdded(point2.getMultiplied(-1));
    }

}