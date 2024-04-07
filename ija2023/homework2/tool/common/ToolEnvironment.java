/*
 * ToolEnvironment.java
 * @author Aleksandrov Vladimir xaleks03
 * @author Volodymyr Burylov xburyl00
 */ 
// ToolEnvironment.java
package ija.ija2023.homework2.tool.common;

import ija.ija2023.homework2.common.Obstacle;

import java.util.List;

public interface ToolEnvironment {
    boolean obstacleAt(Position p);
    int rows();
    int cols();
    List<ToolRobot> robots();
    List<Obstacle> myObstacleslist();
}