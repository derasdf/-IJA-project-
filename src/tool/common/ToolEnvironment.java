/*
 * ToolEnvironment.java
 * @author Aleksandrov Vladimir xaleks03
 * @author Volodymyr Burylov xburyl00
 */ 
// ToolEnvironment.java
package tool.common;

import common.Obstacle;
import room.ControlledRobot;

import java.util.List;

public interface ToolEnvironment {
    boolean obstacleAt(Position p, int size);
    int getWidth();
    int getHeight();
    List<ControlledRobot> robots();
    List<Obstacle> myObstacleslist();
}