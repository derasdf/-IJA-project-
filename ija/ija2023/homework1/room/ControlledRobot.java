/*
 * ControlledRobot.java
 * @author Aleksandrov Vladimir xaleks03
 */
package ija.ija2023.homework1.room;

import ija.ija2023.homework1.common.Environment;
import ija.ija2023.homework1.common.Position;
import ija.ija2023.homework1.common.Robot;

public class ControlledRobot implements Robot {
    private int angle;
    private Environment environment;
    private Position position;

    private ControlledRobot(Environment environment, Position position) {
        this.environment = environment;
        this.position = position;
    }

    public static ControlledRobot create(Environment environment, Position position) {
        if (!environment.containsPosition(position) || environment.obstacleAt(position)) {
            return null; // Cannot create robot outside environment boundaries or at a position with an obstacle
        }
        ControlledRobot robot = new ControlledRobot(environment, position);
        if (!environment.addRobot(robot)) {
            return null; // Failed to add the robot to the environment
        }
        
        return robot;
    }
    
    @Override
    public void turn() {
        angle = (angle + 45) % 360; // Turn the robot by 45 degrees clockwise
    }

    @Override
    public int angle() {
        return angle;
    }

    @Override
public boolean canMove() {
    // Calculate the position of the cell in front of the robot based on its angle
    Position nextPosition = calculateNextPosition();

    // Check if the next position is within the boundaries of the environment
    if (!environment.containsPosition(nextPosition)) {
        return false;
    }

    // Check if the next position is empty (no obstacles or other robots)
    return !environment.obstacleAt(nextPosition) && !environment.robotAt(nextPosition);
}

private Position calculateNextPosition() {
    int row = position.getRow();
    int col = position.getCol();
    
    // Calculate the change in row and column based on the angle
    int deltaRow = 0, deltaCol = 0;
    if (angle == 0) {
        deltaRow = -1; // Move up
    } else if (angle == 45) {
        deltaRow = -1; // Move up
        deltaCol = 1;  // Move right
    } else if (angle == 90) {
        deltaCol = 1;  // Move right
    } else if (angle == 135) {
        deltaRow = 1;  // Move down
        deltaCol = 1;  // Move right
    } else if (angle == 180) {
        deltaRow = 1;  // Move down
    } else if (angle == 225) {
        deltaRow = 1;  // Move down
        deltaCol = -1; // Move left
    } else if (angle == 270) {
        deltaCol = -1; // Move left
    } else if (angle == 315) {
        deltaRow = -1; // Move up
        deltaCol = -1; // Move left
    }

    // Calculate the next position
    int nextRow = row + deltaRow;
    int nextCol = col + deltaCol;
    return new Position(nextRow, nextCol);
}


    @Override
    public boolean move() {
        // Check if the robot can move to the next position
        if (!canMove()) {
            return false;
        }

        // Calculate the next position
        Position nextPosition = calculateNextPosition();

        // Update the robot's current position
        this.position = nextPosition;

        return true; 
    }

    @Override
    public Position getPosition() {
        return position;
    }
  
}
