/*
 * Collectable.java
 * @author Aleksandrov Vladimir xaleks03
 */
package common;

import tool.common.Position;

public class Collectable extends Obstacle {

    public Collectable(Environment env, Position pos, int size) {
        super(env, pos, size);
    }

    public static Collectable create(Environment env, Position pos, int size) {
        if (!env.containsPosition(pos, size) || env.collectableAt(pos, size, null) || env.robotAt(pos, size, null)) {
            return null;
        }
        Collectable collectable = new Collectable(env, pos, size);
        if (!env.createCollectableAt(collectable)) {
            return null;
        }
        return collectable;
    }
}