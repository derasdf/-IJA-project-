/*
 * Position.java
 * @author Aleksandrov Vladimir xaleks03
 */
package common;
import java.util.Objects;
public class Position {
    private int width;
    private int height;

    public Position(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return width == position.width && height == position.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height);
    }

    @Override
    public String toString() {
        return "Position{" +
                "row=" + width +
                ", col=" + height +
                '}';
    }
}