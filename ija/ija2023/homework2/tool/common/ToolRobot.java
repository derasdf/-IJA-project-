/*
 * ToolRobot.java
 * @author Aleksandrov Vladimir xaleks03
 * @author Volodymyr Burylov xburyl00
 */ 
package ija.ija2023.homework2.tool.common;

public interface ToolRobot extends Observable {
    // Interface representing a robot that can move in the environment
    
    // Method to get the current angle of the robot
    int angle();
    
    // Method to rotate the robot clockwise by a specified number of steps
    void turn(int n);
    
    // Method to get the current position of the robot in the environment
    Position getPosition();
}
