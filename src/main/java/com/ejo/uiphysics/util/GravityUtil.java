package com.ejo.uiphysics.util;

import com.ejo.glowlib.math.Vector;
import com.ejo.glowlib.math.VectorMod;
import com.ejo.uiphysics.elements.PhysicsObjectUI;

import java.util.ArrayList;

public class GravityUtil {

    public static double g = 9.80665;
    public static double G = 6.6743 * Math.pow(10, -11);

    public static <T extends PhysicsObjectUI> Vector calculateGravityForce(PhysicsObjectUI object, ArrayList<T> physicsObjects, double G) {
        if (object.isPhysicsDisabled()) return Vector.NULL;

        VectorMod gravityForce = Vector.NULL.getMod();

        //Calculate the force on obj from every other object in the list
        for (PhysicsObjectUI otherObject : physicsObjects) {
            if (!object.equals(otherObject) && !otherObject.isPhysicsDisabled()) {
                Vector gravityForceFromOtherObject = calculateGravitationalField(G,otherObject,object.getCenter()).getMultiplied(object.getMass());
                if (!(String.valueOf(gravityForceFromOtherObject.getMagnitude())).equals("NaN"))
                    gravityForce.add(gravityForceFromOtherObject);
            }
        }
        return gravityForce;
    }


    public static Vector calculateGravitationalField(double G, PhysicsObjectUI object, Vector location) {
        Vector distance = VectorUtil.calculateVectorBetweenPoints(object.getCenter(),location);
        return distance.getUnitVector().getMultiplied(G * object.getMass() / Math.pow(distance.getMagnitude(), 2));
    }

    public static void calculateSurfaceGravity(PhysicsObjectUI object) {
        if (!object.isPhysicsDisabled()) {
            object.setNetForce(object.getNetForce().getAdded(new Vector(0, g * object.getMass())));
        }
    }

}
