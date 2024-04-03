/*
 * Environment.java
 * @author Aleksandrov Vladimir xaleks03
 */

package ija.ija2023.homework2.common;

import ija.ija2023.homework2.tool.common.ToolEnvironment;
import ija.ija2023.homework2.tool.common.Position;
public interface Environment extends ToolEnvironment {
    boolean addRobot(Robot robot);
    boolean createObstacleAt(int row, int col);
    boolean obstacleAt(int row, int col);
    boolean obstacleAt(Position p);
    boolean robotAt(Position p);
    boolean containsPosition(Position pos);
}