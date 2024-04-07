/*
 * Observable.java
 * @author Aleksandrov Vladimir xaleks03
 * @author Volodymyr Burylov xburyl00
 */ 
package ija.ija2023.homework2.tool.common;

public interface Observable {

    interface Observer {
        void update(Observable o);
    }


    void addObserver(Observer o);
    

    void removeObserver(Observer o);

    void notifyObservers();
}
