package com.ejo.uiphysics.elements;

import com.ejo.glowlib.math.Angle;
import com.ejo.glowlib.math.MathE;
import com.ejo.glowlib.math.Vector;
import com.ejo.glowlib.misc.ColorE;
import com.ejo.glowui.scene.Scene;
import com.ejo.glowui.scene.elements.shape.RectangleUI;
import com.ejo.uiphysics.util.VectorUtil;

import java.util.ArrayList;

/**
 * This is an unfinished, EXPERIMENTAL feature. There may be bugs, slow loading times, and more. Use at your own risk
 */
public class PhysicsSurfaceUI extends PhysicsObjectUI {

    private ArrayList<PhysicsObjectUI> physicsObjects = new ArrayList<>();

    private double staticFriction;
    private double kineticFriction;

    public PhysicsSurfaceUI(RectangleUI rectangle, double staticFriction, double kineticFriction) {
        super(rectangle, 1, 0, Vector.NULL, Vector.NULL);
        this.staticFriction = staticFriction;
        this.kineticFriction = kineticFriction;
    }

    @Override
    protected void tickElement(Scene scene, Vector mousePos) {
        super.tickElement(scene, mousePos);

        for (PhysicsObjectUI object : getPhysicsObjects()) {
            if (object.getShape() instanceof RectangleUI rect) {
                double xSize = rect.getSize().getX();
                double ySize = rect.getSize().getY();
                Vector relativeForce = object.getNetForce().getSubtracted(getNetForce());
                if (isColliding(object, CollisionType.TOP)) {
                    if (relativeForce.getY() > 0) applyFriction(object,new Angle(0,true),true);
                    doCollisionTop(object, ySize);
                }
                if (isColliding(object, CollisionType.BOTTOM)) {
                    if (relativeForce.getY() < 0) applyFriction(object,new Angle(0,true),true);
                    doCollisionBottom(object, ySize);
                }
                if (isColliding(object, CollisionType.RIGHT)) {
                    if (relativeForce.getX() < 0) applyFriction(object,new Angle(90,true),false);
                    doCollisionRight(object, xSize);
                }
                if (isColliding(object, CollisionType.LEFT)) {
                    if (relativeForce.getX() > 0) applyFriction(object,new Angle(90,true),false);
                    doCollisionLeft(object, xSize);
                }
            }
        }
    }

    public void updateCollisionObjects(ArrayList<PhysicsObjectUI> objects) {
        this.physicsObjects = objects;
    }


    private void applyFriction(PhysicsObjectUI object, Angle platformAngle, boolean doOscillationPrevention) {
        Vector relativeVelocity = object.getVelocity().getSubtracted(getVelocity());
        Vector relativeForce = object.getNetForce().getSubtracted(getNetForce());

        Vector relativeVelocityParallel = platformAngle.getUnitVector().getMultiplied(relativeVelocity.getDot(platformAngle.getUnitVector())); // The 'X' component relative to the platform
        Vector relativeForceParallel = platformAngle.getUnitVector().getMultiplied(relativeForce.getDot(platformAngle.getUnitVector())); // The 'X' component relative to the platform
        Vector relativeForcePerpendicular = relativeForce.getSubtracted(relativeForceParallel); // The 'Y' component relative to the platform

        double parallelVelocityComponentSign = MathE.roundDouble(relativeVelocityParallel.getTheta().getDegrees(),0) == MathE.roundDouble(platformAngle.getDegrees(),0) ? 1 : -1;
        double parallelForceComponentSign = MathE.roundDouble(relativeForceParallel.getTheta().getDegrees(),0) == MathE.roundDouble(platformAngle.getDegrees(),0) ? 1 : -1;
        double perpendicularForceComponentSign = MathE.roundDouble(relativeForcePerpendicular.getTheta().getDegrees(),0) == MathE.roundDouble(platformAngle.getDegrees(),0) ? 1 : -1;

        double parallelVelocityComponent = parallelVelocityComponentSign * relativeVelocityParallel.getMagnitude();
        double parallelForceComponent = parallelForceComponentSign * relativeForceParallel.getMagnitude(); //Pushing Force
        double perpendicularForceComponent = perpendicularForceComponentSign * relativeForcePerpendicular.getMagnitude(); //Normal Force

        //Static Friction -- UNFINISHED, but works alright
        if (Math.abs(parallelVelocityComponent) == 0) {
            if (Math.abs(parallelForceComponent) < staticFriction * Math.abs(perpendicularForceComponent)) {
                return; //If static friction passes, do NOT apply any new friction forces
            }
        }

        //Kinetic Friction
        double kineticForce = kineticFriction * Math.abs(perpendicularForceComponent);
        if (parallelVelocityComponent > 0) kineticForce *= -1;
        if (parallelVelocityComponent == 0 && parallelForceComponent > 0) kineticForce *= -1;
        object.addForce(platformAngle.getUnitVector().getMultiplied(kineticForce));


        // -----------------------------------------------------------------------------------------------
        //NOTE: Friction balancing forces will sometimes cause an oscillation around 0 due to deltaT not being infinitely small.
        //NOTE: Oscillation prevention is currently only coded for cardinal direction oscillations, not rotated platforms
        //NOTE: This currently does not work for accelerating platforms
        //Oscillation Prevention: If the last frame of an objects force was the exact opposite to the current force, set the force to null AND set the velocity to the reference frame velocity
        if (doOscillationPrevention) {
            if ((object.prevNetForce.getX() == -object.getNetForce().getX() && object.prevNetForce.getX() == -object.prevPrevNetForce.getX() && object.prevNetForce.getX() != 0)
            || (object.prevNetForce.getY() == -object.getNetForce().getY() && object.prevNetForce.getY() == -object.prevPrevNetForce.getY() && object.prevNetForce.getY() != 0)) {
                object.addForce(object.prevNetForce);
                object.setVelocity(getVelocity()); //Sets the objects velocity to the platforms velocity
            }
        }
    }

    private void doCollisionTop(PhysicsObjectUI object, double ySize) {
        if (object.getNetForce().getY() > 0) object.addForce(new Vector(0,-object.getNetForce().getY()));
        object.setVelocity(new Vector(object.getVelocity().getX(),object.getVelocity().getY() > 0 ? getVelocity().getY() : object.getVelocity().getY()));
        object.setPos(new Vector(object.getPos().getX(),getPos().getY() - ySize));
    }

    private void doCollisionBottom(PhysicsObjectUI object, double ySize) {
        if (object.getNetForce().getY() < 0) object.addForce(new Vector(0,-object.getNetForce().getY()));
        object.setVelocity(new Vector(object.getVelocity().getX(),object.getVelocity().getY() < 0 ? getVelocity().getY() : object.getVelocity().getY()));
        object.setPos(new Vector(object.getPos().getX(),getPos().getY() + getSize().getY()));
    }

    private void doCollisionLeft(PhysicsObjectUI object, double xSize) {
        if (object.getNetForce().getX() > 0) object.addForce(new Vector(-object.getNetForce().getX(),0));
        object.setVelocity(new Vector(object.getVelocity().getX() > 0 ? getVelocity().getX() : object.getVelocity().getX(),object.getVelocity().getY()));
        object.setPos(new Vector(getPos().getX() - xSize,object.getPos().getY()));
    }

    private void doCollisionRight(PhysicsObjectUI object, double xSize) {
        if (object.getNetForce().getX() < 0) object.addForce(new Vector(-object.getNetForce().getX(),0));
        object.setVelocity(new Vector(object.getVelocity().getX() < 0 ? getVelocity().getX() : object.getVelocity().getX(),object.getVelocity().getY()));
        object.setPos(new Vector(getPos().getX() + getSize().getX(),object.getPos().getY()));
    }

    //TODO: Make collision type into angle like we had for friction
    public boolean isColliding(PhysicsObjectUI object, CollisionType type) {
        if (!object.isColliding(this)) return false;
        Vector dirVec = VectorUtil.calculateVectorBetweenPoints(object.getCenter(),getCenter()).getUnitVector();
        Vector dirCornerVec = VectorUtil.calculateVectorBetweenPoints(getPos(),getCenter()).getUnitVector();
        Angle cornerAngle = new Angle(Math.atan2(-dirCornerVec.getY(),dirCornerVec.getX()));
        Angle posAngle = new Angle(Math.atan2(-dirVec.getY(),dirVec.getX()));
        return switch (type) {
            case TOP -> posAngle.getDegrees() > (180 - cornerAngle.getDegrees()) && posAngle.getDegrees() < cornerAngle.getDegrees();
            case BOTTOM -> posAngle.getDegrees() > -cornerAngle.getDegrees() && posAngle.getDegrees() < -(180 - cornerAngle.getDegrees());
            case LEFT -> Math.abs(posAngle.getDegrees()) > cornerAngle.getDegrees();
            case RIGHT -> Math.abs(posAngle.getDegrees()) < 180 - cornerAngle.getDegrees();
        };
    }

    public enum CollisionType {
        TOP,
        BOTTOM,
        LEFT,
        RIGHT
    }

    public PhysicsSurfaceUI setStaticFriction(double staticFriction) {
        this.staticFriction = staticFriction;
        return this;
    }

    public PhysicsSurfaceUI setKineticFriction(double kineticFriction) {
        this.kineticFriction = kineticFriction;
        return this;
    }


    public double getStaticFriction() {
        return staticFriction;
    }

    public double getKineticFriction() {
        return kineticFriction;
    }

    public Vector getSize() {
        return getRectangle().getSize();
    }

    public RectangleUI getRectangle() {
        return (RectangleUI) getShape();
    }


    public ArrayList<PhysicsObjectUI> getPhysicsObjects() {
        return physicsObjects;
    }

}
