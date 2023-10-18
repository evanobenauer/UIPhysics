package com.ejo.uiphysics.elements;

import com.ejo.glowlib.math.Vector;
import com.ejo.glowlib.misc.ColorE;
import com.ejo.glowui.scene.Scene;
import com.ejo.glowui.scene.elements.ElementUI;
import com.ejo.glowui.scene.elements.shape.*;

/**
 * The PhysicsObject class is a container for any shape. The class uses the data from the shape and calculates kinematics to move
 * said shape anywhere on the screen.
 * Due to UI coordinate conventions, UP is -y, DOWN is +y. This is important when calculating forces
 * NOTE: Physics objects will NOT render properly in economy mode as they do not post empty events. You MUST use standard mode to render properly
 */
//TODO: Make the physics line an incompressible tension force applier
public class PhysicsLineUI extends PhysicsObjectUI implements IShape {

    public PhysicsLineUI(LineUI line, double mass, double charge, Vector velocity, Vector netForce, double omega, double netTorque) {
        super(line, mass, charge, velocity, netForce, omega, netTorque);
    }
}
