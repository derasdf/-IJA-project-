/*
 * Robot.java
 * @author Aleksandrov Vladimir xaleks03
 */
package ija.ija2023.homework1.common;

public interface Robot {
    int angle();
    boolean canMove();
    Position getPosition();
    boolean move();
    void turn();
}
