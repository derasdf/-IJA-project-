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
        observers.add(o);
    }

    @Override
    public void removeObserver(Observable.Observer o) {
        observers.remove(o);
    }
    public List<Observable.Observer> getObservers()
    {
        return this.observers;
    }

    @Override
    public void notifyObservers() {
        if (observers != null && !observers.isEmpty()) {
            for (Observable.Observer observer : observers) {
                observer.update(this);
            }
        }
    }
}