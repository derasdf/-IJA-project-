/*
 * Position.java
 * @author Aleksandrov Vladimir xaleks03
 * @author Volodymyr Burylov xburyl00
 */ 
package tool.common;
public class Position extends Object {
    private double width;
    private double height;

    public Position(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
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
    public String toString() {
        return "(" + width + ", " + height + ")";
    }
}

