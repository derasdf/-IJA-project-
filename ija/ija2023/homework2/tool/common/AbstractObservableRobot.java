/*
 * AbstractObservableRobot.java
 * @author Aleksandrov Vladimir xaleks03
 * @author Volodymyr Burylov xburyl00
 */
package ija.ija2023.homework2.tool.common;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractObservableRobot implements ToolRobot, Observable {
    private final List<Observable.Observer> observers;

    public AbstractObservableRobot() {
        this.observers = new ArrayList<>();
    }

    @Override
    public void addObserver(Observable.Observer o) {
        // Register a new observer
        observers.add(o);
    }

    @Override
    public void removeObserver(Observable.Observer o) {
        // Remove an observer
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        // Notify all registered observers about the change in the object's state
        for (Observable.Observer observer : observers) {
            observer.update();
        }
    }
}
