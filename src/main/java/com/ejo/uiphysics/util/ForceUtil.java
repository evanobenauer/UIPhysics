package com.ejo.uiphysics.util;

import com.ejo.glowlib.math.Vector;
import com.ejo.uiphysics.elements.PhysicsObjectUI;

public class ForceUtil {


    public static void addForce(PhysicsObjectUI object, Vector force) {
        object.setNetForce(object.getNetForce().getAdded(force));
    }
}
