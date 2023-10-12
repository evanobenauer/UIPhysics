package com.ejo.uiphysics.elements;

import com.ejo.glowui.scene.Scene;
import com.ejo.glowui.scene.elements.shape.IShape;
import com.ejo.glowlib.math.Vector;

//TODO: Make draggable object set a velocity
public class PhysicsDraggableUI extends PhysicsObjectUI {

    private boolean dragging;
    private Vector dragPos = Vector.NULL;

    public PhysicsDraggableUI(IShape shape, double mass, double charge, Vector velocity, Vector netForce, double omega, double netTorque) {
        super(shape,mass,charge,velocity,netForce,omega,netTorque);
        this.dragging = false;
    }

    public PhysicsDraggableUI(IShape shape, double mass, Vector velocity, Vector netForce, double omega, double netTorque) {
        this(shape,mass,0,velocity,netForce,omega,netTorque);
    }

    public PhysicsDraggableUI(IShape shape, double mass, double charge, Vector velocity, Vector netForce) {
        this(shape,mass,charge,velocity,netForce,0,0);
    }

    public PhysicsDraggableUI(IShape shape, double mass, Vector velocity, Vector netForce) {
        this(shape,mass,0,velocity,netForce,0,0);
    }

    @Override
    protected void tickElement(Scene scene, Vector mousePos) {
        if (isDragging()) {
            resetMovement();
            setPos(scene.getWindow().getScaledMousePos().getAdded(dragPos.getMultiplied(-1)));
        } else {
            dragPos = scene.getWindow().getScaledMousePos().getAdded(getPos().getMultiplied(-1));
        }
        super.tickElement(scene,mousePos);
    }

    @Override
    public void onMouseClick(Scene scene, int button, int action, int mods, Vector mousePos) {
        if (button == 0 && action == 0) setDragging(false);
        if (isMouseOver()) if (button == 0 && action == 1) setDragging(true);
    }

    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    public boolean isDragging() {
        return dragging;
    }

}
