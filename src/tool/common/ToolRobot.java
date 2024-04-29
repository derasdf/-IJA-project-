/*
 * ToolRobot.java
 * @author Aleksandrov Vladimir xaleks03
 * @author Volodymyr Burylov xburyl00
 */ 
package tool.common;

public interface ToolRobot extends Observable {

    int angle();
    void turn(int n);
    

    Position getPosition();
}
