package com.ejo.uiphysics.util;

import com.ejo.glowlib.math.Vector;
import com.ejo.glowui.scene.elements.shape.CircleUI;
import com.ejo.glowui.scene.elements.shape.LineUI;
import com.ejo.glowui.scene.elements.shape.PolygonUI;

import java.util.ArrayList;

public class CollisionUtil {

    //TODO: Optimize SAT by only activating the code when a certain distance away

    //Polygon / Polygon
    public static boolean isCollidingSAT(PolygonUI polygon, PolygonUI otherPolygon) {
        ArrayList<Vector> axisList = new ArrayList<>();

        //Add Polygon Axes
        for (int i = 0; i < polygon.getVertices().length; i++) addPolygonAxes(axisList,polygon,i);
        for (int i = 0; i < otherPolygon.getVertices().length; i++) addPolygonAxes(axisList,otherPolygon,i);

        for (Vector axis : axisList) {
            double[] polygon1MinMax = getPolygonMinMax(axis, polygon);
            double[] polygon2MinMax = getPolygonMinMax(axis, otherPolygon);
            if (isSeparated(polygon1MinMax[0], polygon1MinMax[1], polygon2MinMax[0], polygon2MinMax[1])) return false;
        }
        return true;
    }

    //Polygon / Circle
    public static boolean isCollidingSAT(PolygonUI polygon, CircleUI circle) {
        ArrayList<Vector> axisList = new ArrayList<>();

        Vector closestVertex = polygon.getVertices()[0];
        for (int i = 0; i < polygon.getVertices().length; i++) {
            addPolygonAxes(axisList,polygon,i);
            Vector vertex = polygon.getVertices()[i];
            if (circle.getCenter().getSubtracted(vertex.getAdded(polygon.getPos())).getMagnitude() < circle.getCenter().getSubtracted(closestVertex.getAdded(polygon.getPos())).getMagnitude())
                closestVertex = vertex;
        }
        axisList.add(circle.getPos().getSubtracted(closestVertex.getAdded(polygon.getPos())).getUnitVector());


        for (Vector axis : axisList) {
            double[] polygonMinMax = getPolygonMinMax(axis, polygon);
            double circleMax = axis.getDot(circle.getCenter()) + circle.getRadius();
            double circleMin = axis.getDot(circle.getCenter()) - circle.getRadius();
            if (isSeparated(polygonMinMax[0], polygonMinMax[1], circleMin, circleMax)) return false;
        }
        return true;
    }

    //Polygon / Line
    public static boolean isCollidingSAT(PolygonUI polygon, LineUI line) {
        ArrayList<Vector> axisList = new ArrayList<>();

        //Add Polygon Axes
        for (int i = 0; i < polygon.getVertices().length; i++) addPolygonAxes(axisList,polygon,i);

        //TODO: Make a universal getNormal method and make an addLineAxis
        for (int i = 0; i < line.getVertices().length; i++) {
            Vector vertex = line.getVertices()[i];
            Vector vertex2 = line.getVertices()[i + 1 >= line.getVertices().length ? 0 : i + 1];
            Vector sideVector = vertex2.getSubtracted(vertex);
            Vector perpendicular = sideVector.getCross(Vector.K);
            Vector normalAxis = perpendicular.getUnitVector();
            boolean isDuplicate = false;
            for (Vector currentAxis : axisList) {
                if (normalAxis.equals(currentAxis)) {
                    isDuplicate = true;
                    break;
                }
            }
            if (!isDuplicate) axisList.add(normalAxis);
        }

        for (Vector axis : axisList) {
            double[] polygonMinMax = getPolygonMinMax(axis, polygon);
            double[] lineMinMax = getLineMinMax(axis,line);
            if (isSeparated(polygonMinMax[0], polygonMinMax[1], lineMinMax[0], lineMinMax[1])) return false;
        }
        return true;
    }

    public static boolean isCollidingSAT(CircleUI circle, LineUI line) {
        return false;
    }

    private static void addPolygonAxes(ArrayList<Vector> axisList, PolygonUI polygon, int sideIndex) {
        Vector axis = getNormalAxis(polygon,sideIndex);
        boolean isDuplicate = false;
        for (Vector currentAxis : axisList) {
            if (axis.equals(currentAxis)) {
                isDuplicate = true;
                break;
            }
        }
        if (!isDuplicate) axisList.add(axis);
    }

    private static Vector getNormalAxis(PolygonUI poly, int vertexIndex) {
        Vector vertex = poly.getVertices()[vertexIndex];
        Vector vertex2 = poly.getVertices()[vertexIndex + 1 >= poly.getVertices().length ? 0 : vertexIndex + 1];
        Vector sideVector = vertex2.getSubtracted(vertex);
        Vector perpendicular = sideVector.getCross(Vector.K);
        return perpendicular.getUnitVector();
    }

    private static double[] getPolygonMinMax(Vector axis, PolygonUI polygon) {
        double polygonMax = 0;
        double polygonMin = 0;
        for (int i = 0; i < polygon.getVertices().length; i++) {
            Vector vertex = polygon.getVertices()[i].getAdded(polygon.getPos());
            double axisComponent = axis.getDot(vertex);
            if (i == 0) {
                polygonMin = axisComponent;
                polygonMax = axisComponent;
                continue;
            }
            if (axisComponent > polygonMax) polygonMax = axisComponent;
            if (axisComponent < polygonMin) polygonMin = axisComponent;
        }
        return new double[]{polygonMin, polygonMax};
    }

    private static double[] getLineMinMax(Vector axis, LineUI line) {
        double polygonMax = 0;
        double polygonMin = 0;
        for (int i = 0; i < line.getVertices().length; i++) {
            Vector vertex = line.getVertices()[i];
            double axisComponent = axis.getDot(vertex);
            if (i == 0) {
                polygonMin = axisComponent;
                polygonMax = axisComponent;
                continue;
            }
            if (axisComponent > polygonMax) polygonMax = axisComponent;
            if (axisComponent < polygonMin) polygonMin = axisComponent;
        }
        return new double[]{polygonMin, polygonMax};
    }

    private static boolean isSeparated(double min1, double max1, double min2, double max2) {
        return !(((max1 < max2 && max1 > min2) || (min1 < max2 && min1 > min2)) || ((max2 < max1 && max2 > min1) || (min2 < max1 && min2 > min1)));
    }

}