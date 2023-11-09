package com.ejo.uiphysics.elements;

import com.ejo.glowlib.util.NumberUtil;
import com.ejo.glowui.scene.Scene;
import com.ejo.glowui.scene.elements.ElementUI;
import com.ejo.glowui.scene.elements.shape.*;
import com.ejo.glowlib.math.Vector;
import com.ejo.glowlib.misc.ColorE;
import com.ejo.uiphysics.util.VectorUtil;

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

    private boolean tickNetReset;

    private double deltaT;

    private boolean physicsDisabled;

    private double debugVectorForceScale;
    private double debugVectorCap;

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

        this.tickNetReset = false;
        this.physicsDisabled = false;

        this.debugVectorForceScale = 1;
        this.debugVectorCap = 100;
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
        if (scene.getWindow().isDebug()) {
            if (isPhysicsDisabled()) return;
            //Force
            Vector netForce = doTickNetReset() ? prevNetForce : getNetForce();
            if (netForce.getMagnitude() != 0) {
                LineUI lineUI = new LineUI(getCenter(), netForce.getTheta(), NumberUtil.getBoundValue(netForce.getMagnitude() * getDebugVectorForceScale(),0,getDebugVectorCap()).doubleValue(), ColorE.BLUE, LineUI.Type.PLAIN, 4);
                lineUI.draw();
            }

            //Velocity
            if (getVelocity().getMagnitude() != 0) {
                LineUI lineUI2 = new LineUI(getCenter(), getVelocity().getTheta(), NumberUtil.getBoundValue(getVelocity().getMagnitude(),0,getDebugVectorCap()).doubleValue(), ColorE.RED, LineUI.Type.PLAIN, 2);
                lineUI2.draw();
            }
        }
    }

    @Override
    protected void tickElement(Scene scene, Vector mousePos) {
        if (!isPhysicsDisabled()) {
            updateAccFromForce();
            updateAlphaFromTorque();

            updateKinematics();
            updateRotationalKinematics();

            prevPrevNetForce = prevNetForce;
            prevNetForce = getNetForce();

            if (doTickNetReset()) {
                setNetForce(Vector.NULL);
                setNetTorque(0);
            }
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

    public boolean isColliding(IShape shape) {
        if (getShape() instanceof RectangleUI rectangle && shape instanceof RectangleUI shapeRect) {
            boolean isXColliding = (shape.getPos().getX() + shapeRect.getSize().getX() >= getPos().getX() && shape.getPos().getX() <= getPos().getX() + rectangle.getSize().getX());
            boolean isYColliding = (shape.getPos().getY() + shapeRect.getSize().getY() >= getPos().getY() && shape.getPos().getY() <= getPos().getY() + rectangle.getSize().getY());
            return isXColliding && isYColliding;
        }

        //TODO: These don't work perfectly....
        if (getShape() instanceof CircleUI circle && shape instanceof PolygonUI polygon) {
            for (Vector vertex: polygon.getVertices()) {
                if (vertex.getAdded(polygon.getPos()).getSubtracted(circle.getCenter()).getMagnitude() < circle.getRadius())
                    return true;
            }
        }

        if (getShape() instanceof PolygonUI polygon && shape instanceof CircleUI circle) {
            for (Vector vertex: polygon.getVertices()) {
                if (vertex.getAdded(polygon.getPos()).getSubtracted(circle.getCenter()).getMagnitude() < circle.getRadius())
                    return true;
            }
        }

        if (getShape() instanceof LineUI && shape instanceof CircleUI circle) {

        }

        if (getShape() instanceof CircleUI && shape instanceof CircleUI circle) {

        }
        //TODO: Use SAT detection
        if (getShape() instanceof PolygonUI polygon && shape instanceof PolygonUI otherPolygon) {
        }

        return false;
    }

    public boolean isColliding(PhysicsObjectUI object) {
        return isColliding(object.getShape());
    }

    //Combines the colliding objects into 1 new object
    public void doInelasticCollision() {

    }

    //Keeps separate objects with pushback collision code
    public void doElasticCollision() {

    }


    public PhysicsObjectUI setColor(ColorE color) {
        shape.setColor(color);
        return this;
    }

    public PhysicsObjectUI setPos(Vector pos) {
        shape.setPos(pos);
        return this;
    }

    public PhysicsObjectUI setCenter(Vector pos) {
        shape.setCenter(pos);
        return this;
    }


    public PhysicsObjectUI setMass(double mass) {
        this.mass = mass;
        return this;
    }

    public PhysicsObjectUI setCharge(double charge) {
        this.charge = charge;
        return this;
    }


    public PhysicsObjectUI setVelocity(Vector velocity) {
        this.velocity = velocity;
        return this;
    }

    //Set private as acceleration is handled by netForce
    private PhysicsObjectUI setAcceleration(Vector acceleration) {
        this.acceleration = acceleration;
        return this;
    }

    public PhysicsObjectUI setNetForce(Vector netForce) {
        this.netForce = netForce;
        return this;
    }

    public PhysicsObjectUI addForce(Vector force) {
        return setNetForce(getNetForce().getAdded(force));
    }


    public PhysicsObjectUI setSpin(double spin) {
        this.spin = spin;
        return this;
    }

    public PhysicsObjectUI setOmega(double omega) {
        this.omega = omega;
        return this;
    }

    //Set private as alpha is handled by netTorque
    private PhysicsObjectUI setAlpha(double alpha) {
        this.alpha = alpha;
        return this;
    }

    public PhysicsObjectUI setNetTorque(double netTorque) {
        this.netTorque = netTorque;
        return this;
    }

    public PhysicsObjectUI addTorque(double torque) {
        return setNetTorque(getNetTorque() + torque);
    }


    public PhysicsObjectUI setDeltaT(double deltaT) {
        this.deltaT = deltaT;
        return this;
    }

    public PhysicsObjectUI setPhysicsDisabled(boolean disabled) {
        this.physicsDisabled = disabled;
        return this;
    }

    public void setTickNetReset(boolean tickNetReset) {
        this.tickNetReset = tickNetReset;
    }

    public void setDebugVectorForceScale(double debugVectorForceScale) {
        this.debugVectorForceScale = debugVectorForceScale;
    }

    public void setDebugVectorCap(double debugVectorCap) {
        this.debugVectorCap = debugVectorCap;
    }


    public ColorE getColor() {
        return shape.getColor();
    }

    public Vector getPos() {
        return shape.getPos();
    }

    public Vector getCenter() {
        return shape.getCenter();
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

    public boolean isPhysicsDisabled() {
        return physicsDisabled;
    }

    public boolean doTickNetReset() {
        return tickNetReset;
    }


    public double getDebugVectorForceScale() {
        return debugVectorForceScale;
    }

    public double getDebugVectorCap() {
        return debugVectorCap;
    }


    public double getMomentOfInertia() {
        double I = getMass();
        if (getShape() instanceof RegularPolygonUI poly) I = (double) 2 /5 * getMass() * Math.pow(poly.getRadius(),2);
        if (getShape() instanceof CircleUI circle) I = (double) 2 /5 * getMass() * Math.pow(circle.getRadius(),2);
        if (getShape() instanceof RectangleUI rect) I = (double) 1 /12 * getMass() * (Math.pow(rect.getSize().getX(),2) + Math.pow(rect.getSize().getY(),2));
        if (getShape() instanceof LineUI line) I = (double) 1/ 12 * getMass() * Math.pow(line.getLength(),2);
        return I;
    }

    public IShape getShape() {
        return shape;
    }

}
