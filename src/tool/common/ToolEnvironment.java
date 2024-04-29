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
    boolean obstacleAt(Position p);
    int rows();
    int cols();
    List<ToolRobot> robots();
    List<Obstacle> myObstacleslist();
}