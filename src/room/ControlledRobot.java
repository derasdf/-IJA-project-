/*
 * ControlledRobot.java
 * @author Aleksandrov Vladimir xaleks03
 */
package room;

import common.Environment;
import tool.common.AbstractObservableRobot;
import tool.common.Position;
import common.Robot;

public class ControlledRobot extends AbstractObservableRobot implements Robot {
    private int angle = 0;
    private Environment environment;
    private Position position;
    private ControlledRobot(Environment environment, Position position) {
        this.environment = environment;
        this.position = position;
    }

    public static ControlledRobot create(Environment environment, Position position) {
        if (!environment.containsPosition(position) || environment.obstacleAt(position)) {
            return null;
        }
        ControlledRobot robot = new ControlledRobot(environment, position);
        if (!environment.addRobot(robot)) {
            return null;
        }

        return robot;
    }

    @Override
    public void turn(int n) {
        angle = (angle + n * 45) % 360;
        this.notifyObservers();
    }
    @Override
    public void turn() {
        angle = (angle + 45) % 360;
        this.notifyObservers();
    }
    @Override
    public int angle() {
        return this.angle;
    }

    @Override
    public boolean canMove() {

        Position nextPosition = calculateNextPosition();


        if (!environment.containsPosition(nextPosition)) {
            return false;
        }


        return !environment.obstacleAt(nextPosition) && !environment.robotAt(nextPosition);
    }

    private Position calculateNextPosition() {
        int row = position.getRow();
        int col = position.getCol();


        int deltaRow = 0, deltaCol = 0;
        if (angle == 0) {
            deltaRow = -1;
        } else if (angle == 45) {
            deltaRow = -1;
            deltaCol = 1;
        } else if (angle == 90) {
            deltaCol = 1;
        } else if (angle == 135) {
            deltaRow = 1;
            deltaCol = 1;
        } else if (angle == 180) {
            deltaRow = 1;
        } else if (angle == 225) {
            deltaRow = 1;
            deltaCol = -1;
        } else if (angle == 270) {
            deltaCol = -1;
        } else if (angle == 315) {
            deltaRow = -1;
            deltaCol = -1;
        }


        int nextRow = row + deltaRow;
        int nextCol = col + deltaCol;
        return new Position(nextRow, nextCol);
    }


    @Override
    public boolean move() {
        if (!canMove()) {
            return false;
        }
        Position nextPosition = calculateNextPosition();
        this.position = nextPosition;
        this.notifyObservers();
        return true;
    }

    @Override
    public Position getPosition() {
        return position;
    }



}