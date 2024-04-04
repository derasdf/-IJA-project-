/*
 * Environment.java
 * @author Aleksandrov Vladimir xaleks03
 */

package ija.ija2023.homework1.common;

public interface Environment {
    boolean addRobot(Robot robot);
    boolean createObstacleAt(int row, int col);
    boolean obstacleAt(int row, int col);
    boolean obstacleAt(Position p);
    boolean robotAt(Position p);
    boolean containsPosition(Position pos);
}