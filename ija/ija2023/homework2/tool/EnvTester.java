/*
 * EnvTester.java
 * @author Aleksandrov Vladimir xaleks03
 * @author Volodymyr Burylov xburyl00
 */
package ija.ija2023.homework2.tool;

import ija.ija2023.homework2.tool.common.Observable;
import ija.ija2023.homework2.tool.common.ToolEnvironment;
import ija.ija2023.homework2.tool.common.ToolRobot;

import java.util.ArrayList;
import java.util.List;

public class EnvTester implements Observable.Observer {
    private final ToolEnvironment environment;
    private final List<ToolRobot> notifiedRobots;

    public EnvTester(ToolEnvironment env) {
        this.environment = env;
        this.notifiedRobots = new ArrayList<>();
    }

    public List<ToolRobot> checkEmptyNotification() {
        // Verify that no object (view) has been notified
        return notifiedRobots.isEmpty() ? new ArrayList<>() : notifiedRobots;
    }

    public boolean checkNotification(StringBuilder msg, ToolRobot obj) {
        // Verify the correct notification process when action is taken on Observable objects
        // Observable (robot) notifies dependent Observer objects (view, graphical representation)
        // Check if the correct object sent the notification and the number of notifications is correct
        if (notifiedRobots.contains(obj)) {
            msg.append("Error: Robot ").append(obj).append(" was notified multiple times.");
            return false;
        } else {
            notifiedRobots.clear();
            return true;
        }
    }

    @Override
    public void update(Observable o) {
        if (o instanceof ToolRobot) {
            notifiedRobots.add((ToolRobot) o);
        }
    }
}
