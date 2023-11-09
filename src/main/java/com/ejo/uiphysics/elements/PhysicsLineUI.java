package com.ejo.uiphysics.elements;

import com.ejo.glowlib.math.Vector;
import com.ejo.glowlib.misc.ColorE;
import com.ejo.glowui.scene.Scene;
import com.ejo.glowui.scene.elements.ElementUI;
import com.ejo.glowui.scene.elements.shape.*;

//TODO: Make the physics line an incompressible tension force applier
@Deprecated //(Not Implemented)
public class PhysicsLineUI extends PhysicsObjectUI implements IShape {

    public PhysicsLineUI(LineUI line, double mass, double charge, Vector velocity, Vector netForce, double omega, double netTorque) {
        super(line, mass, charge, velocity, netForce, omega, netTorque);
    }
}
