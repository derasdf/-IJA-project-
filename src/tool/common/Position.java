/*
 * Position.java
 * @author Aleksandrov Vladimir xaleks03
 * @author Volodymyr Burylov xburyl00
 */ 
package tool.common;
public class Position extends Object {
    private final int width;
    private final int height;

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
        int result = 17;
        result = 31 * result + width;
        result = 31 * result + height;
        return result;
    }

    @Override
    public String toString() {
        return "(" + width + ", " + height + ")";
    }
}

