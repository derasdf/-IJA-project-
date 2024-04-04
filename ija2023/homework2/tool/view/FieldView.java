/*
 * FieldView.java
 * @author Aleksandrov Vladimir xaleks03
 * @author Volodymyr Burylov xburyl00
 */ 
package ija.ija2023.homework2.tool.view;

import ija.ija2023.homework2.common.Obstacle;
import ija.ija2023.homework2.common.Robot;
import ija.ija2023.homework2.tool.common.ToolEnvironment;
import ija.ija2023.homework2.tool.common.ToolRobot;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class FieldView extends JPanel {
    ToolEnvironment env;
    public FieldView(ToolEnvironment env){
        this.env = env;
        this.setLayout(null);
        this.setPreferredSize(new Dimension(1000, 1000));
    }
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;
        for (Obstacle obstacle : env.myObstacleslist()) {
            g.fillRect(1000/env.cols() * (obstacle.getPosition().getCol()-1) , 1000 / env.rows() * (obstacle.getPosition().getRow()-1), 1000 / env.cols(), 1000 / env.rows());
        }
        for (ToolRobot robot : env.robots()) {
            if (robot instanceof Robot) {
                RobotView robotView = new RobotView((Robot)robot, env);
                robotView.setBounds(1000 / env.cols() * (robot.getPosition().getCol()-1), 1000 / env.rows() * (robot.getPosition().getRow()-1), robotView.getPreferredSize().width, robotView.getPreferredSize().height);
                this.add(robotView);
            }
            }


    }

}
