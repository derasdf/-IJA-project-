/*
 * Robot.java
 * @author Aleksandrov Vladimir xaleks03
 */
package common;

import tool.common.ToolRobot;
import tool.common.Position;

public interface Robot extends ToolRobot {
    int angle();
    boolean canMove();
    Position getPosition();
    int getSize();
    boolean move();
    void turn(int n);
    void turn();
}