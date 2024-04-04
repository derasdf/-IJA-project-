/*
 * Room.java
 * @author Aleksandrov Vladimir xaleks03
 */
package ija.ija2023.homework1.room;

import ija.ija2023.homework1.common.Environment;
import ija.ija2023.homework1.common.Position;
import ija.ija2023.homework1.common.Robot;
import ija.ija2023.homework1.common.Obstacle;


public class Room implements Environment {
    private int rows;
    private int cols;
    private boolean[][] obstacles;
    private boolean[][] robots;

    private Room(int rows, int cols) {
        if (rows <= 0 || cols <= 0) {
        throw new IllegalArgumentException("Rozmery mistnosti musi byt vetsi nez 0");
    }
        this.rows = rows;
        this.cols = cols;
        this.obstacles = new boolean[rows][cols];
        this.robots = new boolean[rows][cols];
    }

    public static Room create(int rows, int cols) {
        return new Room(rows, cols);
    }

    @Override
    public boolean addRobot(Robot robot) {
        Position pos = robot.getPosition();
        if (!containsPosition(pos)) {
            return false; // Position is out of bounds
        }
        if (obstacleAt(pos) || robotAt(pos)) {
            return false; // There is an obstacle or another robot at the position
        }
        int row = pos.getRow();
        int col = pos.getCol();

        robots[row][col] = true;
        return true;
    }

    @Override
    public boolean createObstacleAt(int row, int col) {
        if (!containsPosition(new Position(row, col))) {
            return false; // Position is out of bounds
        }
        Obstacle obstacle = new Obstacle(this, new Position(row, col));
        obstacles[row][col] = true;
        return true;
    }

    @Override
    public boolean obstacleAt(int row, int col) {
        return obstacles[row][col];
    }

    @Override
    public boolean obstacleAt(Position p) {
        return obstacles[p.getRow()][p.getCol()];
    }

    @Override
    public boolean robotAt(Position p) {
        return robots[p.getRow()][p.getCol()];
    }

    @Override
    public boolean containsPosition(Position pos) {
        int row = pos.getRow();
        int col = pos.getCol();
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }
}
