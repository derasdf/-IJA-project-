/*
 * Robot.java
 * @author Aleksandrov Vladimir xaleks03
 */
package ija.ija2023.homework2.common;

import ija.ija2023.homework2.tool.common.ToolRobot;
import ija.ija2023.homework2.tool.common.Position;

public interface Robot extends ToolRobot {
    int angle();
    boolean canMove();
    Position getPosition();
    boolean move();
    void turn(int n);
}