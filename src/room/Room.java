/*
 * Room.java
 * @author Aleksandrov Vladimir xaleks03
 * @author Burylov Volodymyr xburyl00
 */
package room;

import common.Environment;
import tool.common.Position;
import common.Robot;
import common.Obstacle;
import common.Collectable;
import tool.common.ToolRobot;

import java.util.ArrayList;
import java.util.List;
public class Room implements Environment {
    private double width;
    private double height;
    private List<ControlledRobot> myRobots;
    private List<Obstacle> myObstacles;
    private List<Collectable> myCollectables;

    private Room(double width, double height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Rozmery mistnosti musi byt vetsi nez 0");
        }
        this.width = width;
        this.height = height;
        this.myObstacles = new ArrayList<>();
        this.myCollectables = new ArrayList<>();
        this.myRobots = new ArrayList<>();
    }

    public static Room create(double width, double height) {
        return new Room(width, height);
    }

    @Override
    public boolean addRobot(Robot robot) {
        Position pos = robot.getPosition();
        int size = robot.getSize();
        if (!containsPosition(pos, size)) {
            return false;
        }
        if (obstacleAt(pos, size, null) || robotAt(pos, size, null)|| collectableAt(pos, size, null)) {
            return false;
        }
        myRobots.add((ControlledRobot)robot);
        return true;
    }

    @Override
    public boolean createObstacleAt(Obstacle obstacle) {
        Position pos = obstacle.getPosition();
        int size = obstacle.getSize();
        System.out.println("Room.createObstacleAt " + containsPosition(new Position(width, height), size) + " " + obstacleAt(pos, size, null) + " " + robotAt(pos, size, null));
        if (!containsPosition(obstacle.getPosition(), size)) {
            return false;
        }
        if (obstacleAt(pos, size, null) || robotAt(pos, size, null)|| collectableAt(pos, size, null)) {
            return false;
        }
        myObstacles.add(obstacle);
        System.out.println("Obstacles in func " + myObstacleslist().size() );
        return true;
    }
    @Override
    public boolean createCollectableAt(Collectable collectable) {
        Position pos = collectable.getPosition();
        int size = collectable.getSize();
        System.out.println("Room.createCollectableAt " + containsPosition(pos, size) + " " + collectableAt(pos, size, null) + " " + robotAt(pos, size, null));
        if (!containsPosition(collectable.getPosition(), size)) {
            return false;
        }
        if (obstacleAt(pos, size, null) ||collectableAt(pos, size, null) || robotAt(pos, size, null)) {
            return false;
        }
        myCollectables.add(collectable);
        System.out.println("collectableS in func " + myCollectableslist().size() );
        return true;
    }

    @Override
    public boolean obstacleAt(double width, double height, int size, Obstacle checkingObstacle) {
        double newRight = width + size;
        double newBottom = height + size;

        for (Obstacle obstacle : myObstacles) {
            if (obstacle != checkingObstacle) {  // Проверяем, что это не тот же самый робот
                Position pos = obstacle.getPosition();
                double obsX = pos.getWidth();
                double obsY = pos.getHeight();
                int obsSize = obstacle.getSize();
                double obsRight = obsX + obsSize;
                double obsBottom = obsY + obsSize;

                if (((newRight > obsX && newRight < obsRight) || (width > obsX && width < obsRight) || (width < obsX && newRight > obsRight)) &&
                        ((newBottom > obsY && newBottom < obsBottom) || (height > obsY && height < obsBottom) || (height < obsY && newBottom > obsBottom))) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean obstacleAt(Position p, int size, Obstacle checkingObstacle) {
        double width = p.getWidth();
        double height = p.getHeight();

        double newRight = width + size;
        double newBottom = height + size;

        for (Obstacle obstacle : myObstacles) {
            if (obstacle != checkingObstacle) {
                System.out.println(obstacle);// Проверяем, что это не тот же самый робот
                Position pos = obstacle.getPosition();
                double obsX = pos.getWidth();
                double obsY = pos.getHeight();
                int obsSize = obstacle.getSize();
                double obsRight = obsX + obsSize;
                double obsBottom = obsY + obsSize;
                System.out.println(newRight + " " + obsX);
                if (((newRight > obsX && newRight < obsRight) || (width > obsX && width < obsRight) || (width < obsX && newRight > obsRight)) &&
                        ((newBottom > obsY && newBottom < obsBottom) || (height > obsY && height < obsBottom) || (height < obsY && newBottom > obsBottom))) {
                    return true;
                }
            }

        }

        return false;
    }

    @Override
    public boolean collectableAt(Position p, int size, Collectable checkingCollectable) {
        for (Collectable collectable : myCollectables) {
            if (collectable != checkingCollectable) {
                Position pos = collectable.getPosition();
                double colX = pos.getWidth();
                double colY = pos.getHeight();
                int colSize = collectable.getSize();

                // Check if the position overlaps with the collectable's position
                if (p.getWidth() < colX + colSize &&
                        p.getWidth() + size > colX &&
                        p.getHeight() < colY + colSize &&
                        p.getHeight() + size > colY) {
                    return true;
                }
            }
        }
        return false;
    }

    public Collectable getCollectableAt(Position p, int size) {
        for (Collectable collectable : myCollectables) {
            if (!collectableAt(p, size, collectable)) {
                return collectable;
            }
        }
        return null;
    }
    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public boolean robotAt(Position p, int size, ControlledRobot checkingRobot) {
        double width = p.getWidth();
        double height = p.getHeight();

        double newRight = width + size;
        double newBottom = height + size;

        for (ControlledRobot robot : myRobots) {
            if (robot != checkingRobot) {  // Проверяем, что это не тот же самый робот
                Position pos = robot.getPosition();
                double robX = pos.getWidth();
                double robY = pos.getHeight();
                int robSize = robot.getSize();
                double robRight = robX + robSize;
                double robBottom = robY + robSize;

                if (((newRight > robX && newRight < robRight) || (width > robX && width < robRight) || (width < robX && newRight > robRight)) &&
                        ((newBottom > robY && newBottom < robBottom) || (height > robY && height < robBottom) || (height < robY && newBottom > robBottom))) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean containsPosition(Position pos, int size) {
        double widths = pos.getWidth();
        double heights = pos.getHeight();
        return widths >= 0 && widths + size <= width && heights >= 0 && heights + size <= height;
    }

    @Override
    public List<ControlledRobot> robots() {
        return new ArrayList<>(myRobots);

    }
    public List<Obstacle> myObstacleslist() {
        return new ArrayList<>(myObstacles);
    }
    public List<Collectable> myCollectableslist() {
        return new ArrayList<>(myCollectables);
    }
    public void clearRobots(){
        myRobots.clear();
    }
    public void clearObstacles(){
        myObstacles.clear();
    }
    public void clearCollectables(){
        myCollectables.clear();
    }
    public void removeRobot(ControlledRobot robot){
        myRobots.remove(robot);
    }
    public void removeObstacle(Obstacle obstacle){
        myObstacles.remove(obstacle);
    }
    public void removeCollectable(Collectable collectable){
        myCollectables.remove(collectable);
    }


}