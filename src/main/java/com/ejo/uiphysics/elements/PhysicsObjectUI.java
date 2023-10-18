package com.ejo.uiphysics.elements;

import com.ejo.glowui.scene.Scene;
import com.ejo.glowui.scene.elements.ElementUI;
import com.ejo.glowui.scene.elements.shape.*;
import com.ejo.glowlib.math.Vector;
import com.ejo.glowlib.misc.ColorE;

/**
 * The PhysicsObject class is a container for any shape. The class uses the data from the shape and calculates kinematics to move
 * said shape anywhere on the screen.
 * Due to UI coordinate conventions, UP is -y, DOWN is +y. This is important when calculating forces
 * NOTE: Physics objects will NOT render properly in economy mode as they do not post empty events. You MUST use standard mode to render properly
 */
public class PhysicsObjectUI extends ElementUI implements IShape {

    protected final IShape shape;

    private double mass;
    private double charge;

    private Vector velocity;
    private Vector acceleration;
    private Vector netForce;

    public Vector prevNetForce;
    public Vector prevPrevNetForce;

    public double spin;
    private double omega;
    private double alpha;
    private double netTorque;

    private double deltaT;

    private boolean disabled;

    public PhysicsObjectUI(IShape shape, double mass, double charge, Vector velocity, Vector netForce, double omega, double netTorque) {
        super(shape.getPos(), shape.shouldRender(),true);
        this.shape = shape;
        this.mass = mass;
        this.charge = charge;

        this.velocity = velocity;
        this.acceleration = Vector.NULL;
        this.netForce = netForce;

        this.spin = 0;
        this.omega = omega;
        this.alpha = 0;
        this.netTorque = netTorque;

        this.deltaT = .1f;
        this.disabled = false;
    }

    public PhysicsObjectUI(IShape shape, double mass, Vector velocity, Vector netForce, double omega, double netTorque) {
        this(shape,mass,0,velocity,netForce,omega,netTorque);
    }

    public PhysicsObjectUI(IShape shape, double mass, double charge, Vector velocity, Vector netForce) {
        this(shape,mass,charge,velocity,netForce,0,0);
    }

    public PhysicsObjectUI(IShape shape, double mass, Vector velocity, Vector netForce) {
        this(shape,mass,0,velocity,netForce,0,0);
    }

    @Override
    protected void drawElement(Scene scene, Vector mousePos) {
        shape.draw(scene, mousePos);
    }

    @Override
    protected void tickElement(Scene scene, Vector mousePos) {
        if (!isPhysicsDisabled()) {
            updateAccFromForce();
            updateKinematics();
            updateAlphaFromTorque();
            updateRotationalKinematics();
            prevPrevNetForce = prevNetForce;
            prevNetForce = getNetForce();
        } else {
            resetMovement();
        }
    }

    @Override
    public boolean updateMouseOver(Vector mousePos) {
        return shape.updateMouseOver(mousePos);
    }


    private void updateKinematics() {
        setVelocity(getVelocity().getAdded(getAcceleration().getMultiplied(getDeltaT())));
        setCenter(getCenter().getAdded(getVelocity().getMultiplied(getDeltaT())));
    }

    private void updateAccFromForce() {
        setAcceleration(getNetForce().getMultiplied(1 / getMass()));
    }

    private void updateRotationalKinematics() {
        setOmega(getOmega() + getAlpha()*getDeltaT());
        setSpin(getSpin() + getOmega()*getDeltaT());
    }

    private void updateAlphaFromTorque() {
        setAlpha(getNetTorque() / getMomentOfInertia());
    }

    public void resetMovement() {
        setNetForce(Vector.NULL);
        setAcceleration(Vector.NULL);
        setVelocity(Vector.NULL);
        setNetTorque(0);
        setAlpha(0);
        setOmega(0);
    }

    //TODO: Add collisions with shapes and lines here
    // Rectangle code here is temporary and should be replaced with a universal shape collision detection
    public boolean isColliding(LineUI line) {
        return false;
    }

    public boolean isColliding(IShape shape) {
        if (getShape() instanceof RectangleUI rect && shape instanceof RectangleUI shapeRect) {
            boolean isXColliding = (shape.getPos().getX() + shapeRect.getSize().getX() >= getPos().getX() && shape.getPos().getX() <= getPos().getX() + rect.getSize().getX());
            boolean isYColliding = (shape.getPos().getY() + shapeRect.getSize().getY() >= getPos().getY() && shape.getPos().getY() <= getPos().getY() + rect.getSize().getY());
            return isXColliding && isYColliding;
        }
        return false;
    }

    public boolean isColliding(PhysicsObjectUI object) {
        if (getShape() instanceof RectangleUI rect && object.getShape() instanceof RectangleUI objRect) {
            boolean isXColliding = (object.getPos().getX() + objRect.getSize().getX() >= getPos().getX() && object.getPos().getX() <= getPos().getX() + rect.getSize().getX());
            boolean isYColliding = (object.getPos().getY() + objRect.getSize().getY() >= getPos().getY() && object.getPos().getY() <= getPos().getY() + rect.getSize().getY());
            return isXColliding && isYColliding;
        }
        return false;
    }

    public boolean isColliding(PhysicsSurfaceUI surface) {
        if (getShape() instanceof RectangleUI rect) return surface.isObjectInCollisionBounds(this,rect.getSize().getX(),rect.getSize().getY());
        return false;
    }


    public Vector setPos(Vector pos) {
        return shape.setPos(pos);
    }

    public Vector setCenter(Vector pos) {
        return shape.setCenter(pos);
    }


    public double setMass(double mass) {
        return this.mass = mass;
    }

    public void setCharge(double charge) {
        this.charge = charge;
    }


    public Vector setVelocity(Vector velocity) {
        return this.velocity = velocity;
    }

    public Vector setAcceleration(Vector acceleration) {
        return this.acceleration = acceleration;
    }

    public Vector setNetForce(Vector netForce) {
        return this.netForce = netForce;
    }


    public double setSpin(double spin) {
        return this.spin = spin;
    }

    public double setOmega(double omega) {
        return this.omega = omega;
    }

    public double setAlpha(double alpha) {
        return this.alpha = alpha;
    }

    public double setNetTorque(double netTorque) {
        return this.netTorque = netTorque;
    }


    public double setDeltaT(double deltaT) {
        return this.deltaT = deltaT;
    }

    public void setPhysicsDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public void setColor(ColorE color) {
        shape.setColor(color);
    }



    public Vector getPos() {
        return shape.getPos();
    }

    public Vector getCenter() {
        return shape.getCenter();
    }

    public ColorE getColor() {
        return shape.getColor();
    }


    public double getMass() {
        return mass;
    }

    public double getCharge() {
        return charge;
    }


    public Vector getVelocity() {
        return velocity;
    }

    public Vector getAcceleration() {
        return acceleration;
    }

    public Vector getNetForce() {
        return netForce;
    }


    public double getSpin() {
        return spin;
    }

    public double getOmega() {
        return omega;
    }

    public double getAlpha() {
        return alpha;
    }

    public double getNetTorque() {
        return netTorque;
    }


    public double getDeltaT() {
        return deltaT;
    }

    public double getMomentOfInertia() {
        double I = getMass();
        if (getShape() instanceof RegularPolygonUI poly) I = (double) 2 /5 * getMass() * Math.pow(poly.getRadius(),2);
        if (getShape() instanceof CircleUI circle) I = (double) 2 /5 * getMass() * Math.pow(circle.getRadius(),2);
        if (getShape() instanceof RectangleUI rect) I = (double) 1 /12 * getMass() * (Math.pow(rect.getSize().getX(),2) + Math.pow(rect.getSize().getY(),2));
        if (getShape() instanceof LineUI line) I = (double) 1/ 12 * getMass() * Math.pow(line.getLength(),2);
        return I;
    }

    public boolean isPhysicsDisabled() {
        return disabled;
    }


    public IShape getShape() {
        return shape;
    }

}
