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
    private int angle;
    private Environment environment;
    private Position position;
    private int size;
    private int speed;
    private int turnAngle;

    private int detectionRange;
    private ControlledRobot(Environment environment, Position position, int size, int speed, int turnAngle, int detectionRange) {
        this.environment = environment;
        this.position = position;
        this.size = size;
        this.angle = 0;
        this.speed = speed;
        this.turnAngle = turnAngle;
        this.detectionRange = detectionRange; // Инициализируйте новое поле
    }


    public static ControlledRobot create(Environment environment, Position position, int size, int speed, int turnAngle, int detectionRange) {
        Position posCheck = new Position(position.getWidth() - detectionRange, position.getWidth() - detectionRange);
        if (!environment.containsPosition(posCheck, size + 2*detectionRange) || environment.obstacleAt(posCheck, size + 2*detectionRange, null) || environment.robotAt(posCheck, size + 2*detectionRange, null)) {
            return null;
        }
        ControlledRobot robot = new ControlledRobot(environment, position, size, speed, turnAngle, detectionRange);
        if (!environment.addRobot(robot)) {
            return null;
        }
        return robot;
    }

    @Override
    public void turn(int n) {
        angle = (angle + n) % 360;
        this.notifyObservers();
    }
    @Override
    public void turn() {
        angle = (angle + 45) % 360;
        this.notifyObservers();
    }
    @Override
    public int angle() {
        return angle;
    }

    @Override
    public boolean canMove() {
        return false;
    }
/*
    @Override
    public boolean canMove() {

        Position nextPosition = calculateNextPosition();


        if (!environment.containsPosition(nextPosition, size)) {
            return false;
        }


        return !environment.obstacleAt(nextPosition) && !environment.robotAt(nextPosition, size);
    }
    */


    /*private Position calculateNextPosition() {
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
    }*/


   /* @Override
    public boolean move() {
        if (!canMove()) {
            return false;
        }
        Position nextPosition = calculateNextPosition();
        this.position = nextPosition;
        this.notifyObservers();
        return true;
    }

    */

    @Override
    public Position getPosition() {
        return position;
    }
    public void setPosition(Position position) {
        this.position = position;
    }

    public int getSize() {
        return size;
    }

    @Override
    public boolean move() {
        return false;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getTurnAngle() {
        return turnAngle;
    }

    public void setTurnAngle(int turnAngle) {
        this.turnAngle = turnAngle;
    }
    public int getDetectionRange() {
        return detectionRange;
    }

    public void setDetectionRange(int detectionRange) {
        this.detectionRange = detectionRange;
    }
    public void setAngle(int angle) {
        this.angle = angle;
    }
    public int getAngle() {
        return angle;
    }

}