/*
 * Room.java
 * @author Aleksandrov Vladimir xaleks03
 */
package room;

import common.Environment;
import tool.common.Position;
import common.Robot;
import common.Obstacle;
import tool.common.ToolRobot;

import java.util.ArrayList;
import java.util.List;
public class Room implements Environment {
    private int width;
    private int height;
    private List<ControlledRobot> myRobots;
    private List<Obstacle> myObstacles;

    private Room(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Rozmery mistnosti musi byt vetsi nez 0");
        }
        this.width = width;
        this.height = height;
        this.myObstacles = new ArrayList<>();
        this.myRobots = new ArrayList<>();
    }

    public static Room create(int width, int height) {
        return new Room(width, height);
    }

    @Override
    public boolean addRobot(Robot robot) {
        Position pos = robot.getPosition();
        int size = robot.getSize();
        if (!containsPosition(pos, size)) {
            return false;
        }
        if (obstacleAt(pos, size) || robotAt(pos, size)) {
            return false;
        }
        myRobots.add((ControlledRobot)robot);
        return true;
    }

    @Override
    public boolean createObstacleAt(int width, int height, int size) {
        if (!containsPosition(new Position(width, height), size)) {
            return false;
        }
        Position pos = new Position(width, height);
        if (obstacleAt(pos, size) || robotAt(pos, size)) {
            return false;
        }
        Obstacle obstacle = new Obstacle(this, new Position(width, height), size);
        myObstacles.add(obstacle);
        return true;
    }

    @Override
    public boolean obstacleAt(int width, int height, int size) {
        int newRight = width + size;
        int newBottom = height + size;

        for (Obstacle obstacle : myObstacles) {
            Position pos = obstacle.getPosition();
            int obsX = pos.getWidth();
            int obsY = pos.getHeight();
            int obsSize = obstacle.getSize();
            int obsRight = obsX + obsSize;
            int obsBottom = obsY + obsSize;


            if(((newRight > obsX && newRight < obsRight) || (width > obsX && width < obsRight) || (width < obsX && newRight > obsRight)) && ((newBottom > obsY && newBottom < obsBottom) || (height > obsY && height < obsBottom) || (height < obsY && newBottom > obsBottom))) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean obstacleAt(Position p, int size) {
        int width = p.getWidth();
        int height = p.getHeight();

        int newRight = width + size;
        int newBottom = height + size;

        for (Obstacle obstacle : myObstacles) {

            Position pos = obstacle.getPosition();
            int obsX = pos.getWidth();
            int obsY = pos.getHeight();
            int obsSize = obstacle.getSize();
            int obsRight = obsX + obsSize;
            int obsBottom = obsY + obsSize;
            if(((newRight > obsX && newRight < obsRight) || (width > obsX && width < obsRight) || (width < obsX && newRight > obsRight)) && ((newBottom > obsY && newBottom < obsBottom) || (height > obsY && height < obsBottom) || (height < obsY && newBottom > obsBottom))) {
               return true;
            }

        }

        return false;
    }


    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public boolean robotAt(Position p, int size) {
        int width = p.getWidth();
        int height = p.getHeight();

        int newRight = width + size;
        int newBottom = height + size;

        for (ControlledRobot robot : myRobots) {
            Position pos = robot.getPosition();
            int robX = pos.getWidth();
            int robY = pos.getHeight();
            int robSize = robot.getSize();
            int robRight = robX + robSize;
            int robBottom = robY + robSize;


            if(((newRight > robX && newRight < robRight) || (width > robX && width < robRight) || (width < robX && newRight > robRight)) && ((newBottom > robY && newBottom < robBottom) || (height > robY && height < robBottom) || (height < robY && newBottom > robBottom))) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean containsPosition(Position pos, int size) {
        int widths = pos.getWidth();
        int heights = pos.getHeight();
        return widths >= 0 && widths + size <= width && heights >= 0 && heights + size <= height;
    }

    @Override
    public List<ControlledRobot> robots() {
        return new ArrayList<>(myRobots);

    }
    public List<Obstacle> myObstacleslist() {
        return new ArrayList<>(myObstacles);

    }

    public void clearRobots(){
        myRobots.clear();
    }
    public void clearObstacles(){
        myObstacles.clear();
    }

}