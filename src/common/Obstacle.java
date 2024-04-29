/*
 * Obstacle.java
 * @author Aleksandrov Vladimir xaleks03
 */
package common;
import tool.common.Position;
public class Obstacle {
    private Environment environment;
    private Position position;

    public Obstacle(Environment env, Position pos) {
        this.environment = env;
        this.position = pos;
    }

    public Position getPosition() {
        return position;
    }
}