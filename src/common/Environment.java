/*
 * Environment.java
 * @author Aleksandrov Vladimir xaleks03
 */

package common;

import tool.common.ToolEnvironment;
import tool.common.Position;
public interface Environment extends ToolEnvironment {
    boolean addRobot(Robot robot);
    boolean createObstacleAt(int width, int height, int size);
    boolean obstacleAt(int width, int height, int size);
    boolean obstacleAt(Position p, int size);
    boolean robotAt(Position p, int size);
    boolean containsPosition(Position pos, int size);
    void clearObstacles();
    void clearRobots();
}