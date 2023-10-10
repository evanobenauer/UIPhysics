package com.ejo.uiphysics.elements;

import com.ejo.glowlib.math.Angle;
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

    public PhysicsSurfaceUI(Vector pos, Vector size, ColorE color, double staticFriction, double kineticFriction) {
        super(new RectangleUI(pos,size,color), 1, 0, Vector.NULL, Vector.NULL);
        this.staticFriction = staticFriction;
        this.kineticFriction = kineticFriction;
        setPhysicsDisabled(true);
    }

    @Override
    protected void tickElement(Scene scene, Vector mousePos) {
        super.tickElement(scene, mousePos);

        for (PhysicsObjectUI object : getPhysicsObjects()) {
            if (object.getShape() instanceof RectangleUI rect) {
                double xSize = rect.getSize().getX();
                double ySize = rect.getSize().getY();
                if (isCollidingTop(object, xSize, ySize)) {
                    applyHorizontalFriction(object);
                    doCollisionTop(object, ySize);
                }
                if (isCollidingBottom(object, xSize, ySize)) {
                    applyHorizontalFriction(object);
                    doCollisionBottom(object, ySize);
                }
                if (isCollidingRight(object, xSize, ySize)) {
                    //applyVerticalFriction(object);
                    doCollisionRight(object, xSize);
                }
                if (isCollidingLeft(object, xSize, ySize)) {
                    //applyVerticalFriction(object);
                    doCollisionLeft(object, xSize);
                }
            }
        }
    }

    public void updateCollisionObjects(ArrayList<PhysicsObjectUI> objects) {
        this.physicsObjects = objects;
    }

    private void applyHorizontalFriction(PhysicsObjectUI object) {
        if (Math.abs(object.getVelocity().getMagnitude()) < 2) object.setVelocity(Vector.NULL); //Check in case of oscillation around 0 due to deltaT not being infinitely small
        if (object.getVelocity().getX() == 0) {
            if (object.getNetForce().getX() > 0) object.setNetForce(object.getNetForce().getAdded(object.getNetForce().getX() < staticFriction*Math.abs(object.getNetForce().getY()) ? -object.getNetForce().getX() : 0,0));
            if (object.getNetForce().getX() < 0) object.setNetForce(object.getNetForce().getAdded(object.getNetForce().getX() > -staticFriction*Math.abs(object.getNetForce().getY()) ? -object.getNetForce().getX() : 0,0));
            return;
        }
        if (object.getVelocity().getX() > 0) object.setNetForce(object.getNetForce().getAdded(-kineticFriction*Math.abs(object.getNetForce().getY()),0));
        if (object.getVelocity().getX() < 0) object.setNetForce(object.getNetForce().getAdded(kineticFriction*Math.abs(object.getNetForce().getY()),0));
    }

    private void applyVerticalFriction(PhysicsObjectUI object) {
        if (Math.abs(object.getVelocity().getMagnitude()) < 2) object.setVelocity(Vector.NULL); //Check in case of oscillation around 0 due to deltaT not being infinitely small
        if (object.getVelocity().getY() == 0) {
            if (object.getNetForce().getY() > 0) object.setNetForce(object.getNetForce().getAdded(0,object.getNetForce().getY() < staticFriction*Math.abs(object.getNetForce().getX()) ? -object.getNetForce().getY() : 0));
            if (object.getNetForce().getY() < 0) object.setNetForce(object.getNetForce().getAdded(0,object.getNetForce().getY() > -staticFriction*Math.abs(object.getNetForce().getX()) ? -object.getNetForce().getY() : 0));
            return;
        }
        if (object.getVelocity().getY() > 0) object.setNetForce(object.getNetForce().getAdded(0,-kineticFriction*Math.abs(object.getNetForce().getX())));
        if (object.getVelocity().getY() < 0) object.setNetForce(object.getNetForce().getAdded(0,kineticFriction*Math.abs(object.getNetForce().getX())));
    }


    private void doCollisionTop(PhysicsObjectUI object, double ySize) {
        if (object.getNetForce().getY() > 0) object.setNetForce(object.getNetForce().getAdded(0,-object.getNetForce().getY()));
        object.setVelocity(new Vector(object.getVelocity().getX(),object.getVelocity().getY() > 0 ? 0 : object.getVelocity().getY()));
        object.setPos(new Vector(object.getPos().getX(),getPos().getY() - ySize));
    }

    private void doCollisionBottom(PhysicsObjectUI object, double ySize) {
        if (object.getNetForce().getY() < 0) object.setNetForce(object.getNetForce().getAdded(0,-object.getNetForce().getY()));
        object.setVelocity(new Vector(object.getVelocity().getX(),object.getVelocity().getY() < 0 ? 0 : object.getVelocity().getY()));
        object.setPos(new Vector(object.getPos().getX(),getPos().getY() + getSize().getY()));
    }

    private void doCollisionLeft(PhysicsObjectUI object, double xSize) {
        if (object.getNetForce().getX() > 0) object.setNetForce(object.getNetForce().getAdded(-object.getNetForce().getX(),0));
        object.setVelocity(new Vector(object.getVelocity().getX() > 0 ? 0 : object.getVelocity().getX(),object.getVelocity().getY()));
        object.setPos(new Vector(getPos().getX() - xSize,object.getPos().getY()));
    }

    private void doCollisionRight(PhysicsObjectUI object, double xSize) {
        if (object.getNetForce().getX() < 0) object.setNetForce(object.getNetForce().getAdded(-object.getNetForce().getX(),0));
        object.setVelocity(new Vector(object.getVelocity().getX() < 0 ? 0 : object.getVelocity().getX(),object.getVelocity().getY()));
        object.setPos(new Vector(getPos().getX() + getSize().getX(),object.getPos().getY()));
    }


    public boolean isCollidingTop(PhysicsObjectUI object, double xSize, double ySize) {
        Vector dirVec = VectorUtil.calculateVectorBetweenPoints(object.getCenter(),getCenter()).getUnitVector();
        Vector dirCornerVec = VectorUtil.calculateVectorBetweenPoints(getPos(),getCenter()).getUnitVector();
        Angle cornerAngle = new Angle(Math.atan2(-dirCornerVec.getY(),dirCornerVec.getX()));
        Angle posAngle = new Angle(Math.atan2(-dirVec.getY(),dirVec.getX()));
        if (posAngle.getDegrees() >= (180 - cornerAngle.getDegrees()) && posAngle.getDegrees() < cornerAngle.getDegrees()) return isInCollisionBounds(object,xSize,ySize);
        return false;
    }

    public boolean isCollidingBottom(PhysicsObjectUI object, double xSize, double ySize) {
        Vector dirVec = VectorUtil.calculateVectorBetweenPoints(object.getCenter(),getCenter()).getUnitVector();
        Vector dirCornerVec = VectorUtil.calculateVectorBetweenPoints(getPos(),getCenter()).getUnitVector();
        Angle cornerAngle = new Angle(Math.atan2(-dirCornerVec.getY(),dirCornerVec.getX()));
        Angle posAngle = new Angle(Math.atan2(-dirVec.getY(),dirVec.getX()));
        if (posAngle.getDegrees() > -cornerAngle.getDegrees() && posAngle.getDegrees() < -(180 - cornerAngle.getDegrees())) return isInCollisionBounds(object,xSize,ySize);
        return false;
    }

    public boolean isCollidingLeft(PhysicsObjectUI object, double xSize, double ySize) {
        Vector dirVec = VectorUtil.calculateVectorBetweenPoints(object.getCenter(),getCenter()).getUnitVector();
        Vector dirCornerVec = VectorUtil.calculateVectorBetweenPoints(getPos(),getCenter()).getUnitVector();
        Angle cornerAngle = new Angle(Math.atan2(-dirCornerVec.getY(),dirCornerVec.getX()));
        Angle posAngle = new Angle(Math.atan2(-dirVec.getY(),dirVec.getX()));
        if (Math.abs(posAngle.getDegrees()) > cornerAngle.getDegrees()) return isInCollisionBounds(object,xSize,ySize);
        return false;
    }

    public boolean isCollidingRight(PhysicsObjectUI object, double xSize, double ySize) {
        Vector dirVec = VectorUtil.calculateVectorBetweenPoints(object.getCenter(),getCenter()).getUnitVector();
        Vector dirCornerVec = VectorUtil.calculateVectorBetweenPoints(getPos(),getCenter()).getUnitVector();
        Angle cornerAngle = new Angle(Math.atan2(-dirCornerVec.getY(),dirCornerVec.getX()));
        Angle posAngle = new Angle(Math.atan2(-dirVec.getY(),dirVec.getX()));
        if (Math.abs(posAngle.getDegrees()) < 180 - cornerAngle.getDegrees()) return isInCollisionBounds(object,xSize,ySize);
        return false;
    }

    private boolean isInCollisionBounds(PhysicsObjectUI object, double xSize, double ySize) {
        boolean isXColliding = (object.getPos().getX() + xSize >= getPos().getX() && object.getPos().getX() <= getPos().getX() + getSize().getX());
        boolean isYColliding = (object.getPos().getY() + ySize >= getPos().getY() && object.getPos().getY() <= getPos().getY() + getSize().getY());
        return isXColliding && isYColliding;
    }


    public void setStaticFriction(double staticFriction) {
        this.staticFriction = staticFriction;
    }

    public void setKineticFriction(double kineticFriction) {
        this.kineticFriction = kineticFriction;
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