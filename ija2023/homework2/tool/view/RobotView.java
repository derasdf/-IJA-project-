/*
 * RobotView.java
 * @author Aleksandrov Vladimir xaleks03
 * @author Volodymyr Burylov xburyl00
 */ 
package ija.ija2023.homework2.tool.view;

import ija.ija2023.homework2.tool.common.Observable;

public class RobotView implements ComponentView, Observable.Observer {
    public RobotView() {
        // Constructor implementation
    }

    @Override
    public void update() {
        // Implementation of the update method from the Observable.Observer interface
        // This method is called when the observed object notifies its observers
    }
}