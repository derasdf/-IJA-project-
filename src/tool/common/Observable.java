/*
 * Observable.Observer.java
 * @author Aleksandrov Vladimir xaleks03
 * @author Volodymyr Burylov xburyl00
 */
package tool.common;

public interface Observable {
    // Interface representing objects that can notify observers about changes

    // Inner interface representing objects that can be registered as observers
    interface Observer {
        // Method called when the observed object notifies its observers
        void update(Observable o);
    }

    // Method to register a new observer
    void addObserver(Observer o);

    // Method to remove an observer
    void removeObserver(Observer o);

    // Method to notify all registered observers about the change in the object's state
    void notifyObservers();
}
