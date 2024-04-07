package ija.ija2023.homework2.tool.view;

import ija.ija2023.homework2.common.Obstacle;
import ija.ija2023.homework2.common.Robot;
import ija.ija2023.homework2.tool.common.Observable;
import ija.ija2023.homework2.tool.common.ToolEnvironment;
import ija.ija2023.homework2.tool.common.ToolRobot;
import ija.ija2023.homework2.tool.EnvPresenter;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class FieldView extends JPanel {
    ToolEnvironment env;
    private List<RobotView> rViews = new ArrayList<>();
    private final int FIELD_SIZE = EnvPresenter.FIELD_SIZE; 
    public FieldView(ToolEnvironment env) {
        this.env = env;
        this.setLayout(null);
        this.setPreferredSize(new Dimension(FIELD_SIZE, FIELD_SIZE));
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        // Scale obstacles
        for (Obstacle obstacle : env.myObstacleslist()) {
            int scaledWidth = FIELD_SIZE / env.cols();
            int scaledHeight = FIELD_SIZE / env.rows();
            int scaledX = scaledWidth * (obstacle.getPosition().getCol() - 1);
            int scaledY = scaledHeight * (obstacle.getPosition().getRow() - 1);
            g.fillRect(scaledX, scaledY, scaledWidth, scaledHeight);
        }

        // Scale robots
        for (ToolRobot robot : env.robots()) {
            if (robot instanceof Robot) {
                boolean found = false;
                for (RobotView rV : rViews) {
                    if (rV.getRobot() == robot) {
                        found = true;
                        int scaledX = FIELD_SIZE / env.cols() * (robot.getPosition().getCol() - 1);
                        int scaledY = FIELD_SIZE / env.rows() * (robot.getPosition().getRow() - 1);
                        rV.setLocation(scaledX, scaledY);
                        rV.repaint();
                        break;
                    }
                }
                if (!found) {
                    RobotView robotView = new RobotView((Robot) robot, env);
                    rViews.add(robotView);
                    int scaledX = FIELD_SIZE / env.cols() * (robot.getPosition().getCol() - 1);
                    int scaledY = FIELD_SIZE / env.rows() * (robot.getPosition().getRow() - 1);
                    robotView.setBounds(scaledX, scaledY, robotView.getPreferredSize().width, robotView.getPreferredSize().height);
                    this.add(robotView);
                    robot.addObserver(robotView);
                }
            }
        }
    }
}
