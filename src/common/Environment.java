/*
 * Environment.java
 * @author Aleksandrov Vladimir xaleks03
 */

package common;

import room.ControlledRobot;
import tool.common.ToolEnvironment;
import tool.common.Position;
public interface Environment extends ToolEnvironment {
    boolean addRobot(Robot robot);
    boolean createObstacleAt(Obstacle obstacle);
    boolean obstacleAt(double width, double height, int size, Obstacle checkingObstacle);
    boolean obstacleAt(Position p, int size, Obstacle checkingObstacle);
    boolean robotAt(Position p, int size, ControlledRobot checkingRobot);
    boolean containsPosition(Position pos, int size);
    void clearObstacles();
    void clearRobots();
    void removeRobot(ControlledRobot robot);
    void removeObstacle(Obstacle obstacle);
}