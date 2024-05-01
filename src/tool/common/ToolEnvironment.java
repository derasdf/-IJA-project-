/*
 * ToolEnvironment.java
 * @author Aleksandrov Vladimir xaleks03
 * @author Volodymyr Burylov xburyl00
 */ 
// ToolEnvironment.java
package tool.common;

import common.Obstacle;

import java.util.List;

public interface ToolEnvironment {
    boolean obstacleAt(Position p, int size);
    int getWidth();
    int getHeight();
    List<ToolRobot> robots();
    List<Obstacle> myObstacleslist();
}