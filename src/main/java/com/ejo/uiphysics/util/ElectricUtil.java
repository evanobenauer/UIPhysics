package com.ejo.uiphysics.util;

import com.ejo.glowlib.math.Vector;
import com.ejo.glowlib.math.VectorMod;
import com.ejo.uiphysics.elements.PhysicsObjectUI;

import java.util.ArrayList;

public class ElectricUtil {

    public static double epsilon0 = Math.pow(8.854187817,-12);
    public static double k = 1 / (4 * Math.PI * epsilon0);

    public static <T extends PhysicsObjectUI> Vector calculateElectricForce(PhysicsObjectUI object, ArrayList<T> physicsObjects, double k) {
        if (object.isPhysicsDisabled()) return Vector.NULL;

        VectorMod electricForce = Vector.NULL.getMod();

        //Calculate the force on obj from every other object in the list
        for (PhysicsObjectUI otherObject : physicsObjects) {
            if (!object.equals(otherObject) && !otherObject.isPhysicsDisabled()) {
                Vector electricForceFromOtherObject = calculateElectricField(k,otherObject,object.getCenter()).getMultiplied(object.getCharge());
                if (!(String.valueOf(electricForceFromOtherObject.getMagnitude())).equals("NaN"))
                    electricForce.add(electricForceFromOtherObject);
            }
        }
        return electricForce;
    }


    public static Vector calculateElectricField(double k, PhysicsObjectUI object, Vector location) {
        Vector distance = VectorUtil.calculateVectorBetweenPoints(object.getCenter(),location);
        return distance.getUnitVector().getMultiplied(k * object.getCharge() / Math.pow(distance.getMagnitude(), 2));
    }

    public static double calculateVoltage(double k, PhysicsObjectUI shape, Vector pos) {
        Vector distance = VectorUtil.calculateVectorBetweenPoints(shape.getPos(),pos);
        if (distance.getMagnitude() == 0) return 0;
        return k * shape.getCharge() / (distance.getMagnitude());
    }
}
