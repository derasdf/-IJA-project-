/*
 * Obstacle.java
 * @author Aleksandrov Vladimir xaleks03
 */
package common;
import tool.common.Position;
public class Obstacle {
    private Environment environment;
    private Position position;
    private int size;
    public Obstacle(Environment env, Position pos, int size) {
        this.environment = env;
        this.position = pos;
        this.size = size;
    }

    public Position getPosition() {
        return position;
    }

    public int getSize() {
        return size;
    }
}