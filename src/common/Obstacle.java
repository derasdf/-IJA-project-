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
    public static Obstacle create(Environment env, Position pos, int size) {
        System.out.println("Obstacle.create " + env.containsPosition(pos, size) + " " + env.obstacleAt(pos, size, null) + " " + env.robotAt(pos, size, null));
        if (!env.containsPosition(pos, size) || env.obstacleAt(pos, size, null) || env.robotAt(pos, size, null)) {
            return null;
        }
        Obstacle obstacle = new Obstacle(env, pos, size);
        if (!env.createObstacleAt(obstacle)) {
            return null;
        }
        return obstacle;
    }

    public Position getPosition() {
        return position;
    }

    public int getSize() {
        return size;
    }
    public void setPosition(Position pos) {
        this.position = pos;
    }
    public void setSize(int size) {
        this.size = size;
    }
}