/*
 * RobotView.java
 * @author Aleksandrov Vladimir xaleks03
 * @author Volodymyr Burylov xburyl00
 */
package ija.ija2023.homework2.tool.view;

import ija.ija2023.homework2.tool.common.Observable;
import ija.ija2023.homework2.common.Environment;
import ija.ija2023.homework2.common.Robot;
import ija.ija2023.homework2.room.ControlledRobot;
import ija.ija2023.homework2.room.Room;
//---

//--- Importy z baliku dodaneho nastroje
import ija.ija2023.homework2.tool.EnvTester;
import ija.ija2023.homework2.tool.common.Position;
import ija.ija2023.homework2.tool.common.ToolEnvironment;
import ija.ija2023.homework2.tool.common.ToolRobot;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class RobotView extends JComponent implements ComponentView, Observable.Observer {

    int angle;
    int row;
    int col;
    int width;
    int height;
    int allrow;
    int allcol;
    Robot rob;
    public RobotView(Robot r, ToolEnvironment env)
    {
        this.angle = r.angle();
        this.row = r.getPosition().getRow();
        this.col = r.getPosition().getCol();
        this.allrow = env.cols();
        this.allcol = env.rows();
        this.width = 1000 / env.cols() ;
        this.height = 1000 / env.rows();
        this.setPreferredSize(new Dimension(this.width, this.height));
        this.rob = r;
    }
    public Robot getRobot()
    {
        return this.rob;
    }
    @java.lang.Override
    public void update(Observable o) {
        if (o instanceof Robot)
        {
            Robot robot = (Robot) o;
            this.angle = robot.angle();
            this.row = robot.getPosition().getRow();
            this.col = robot.getPosition().getCol();
            repaint();
        }
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.RED);
        g.fillOval( this.width/4, this.height/4, this.width/2 , this.height/2);
        g.setColor(Color.BLACK);
        if (angle == 0) {
            g.fillOval(this.width / 2 - 7 , this.height/4 - 7, 15, 15);
        } else if (angle == 45) {
            g.fillOval(this.width/4*3 - 15 , this.height/4 + 7, 15, 15);
        } else if (angle == 90) {
            g.fillOval(this.width - 7, this.height/2 - 7, 15, 15);
        } else if (angle == 135) {
            g.fillOval(this.width - 7, this.height- 7, 15, 15);
        } else if (angle == 180) {
            g.fillOval(this.width/2 - 7, this.height- 7, 15, 15);
        } else if (angle == 225) {
            g.fillOval(this.width/4 - 7, this.height- 7, 15, 15);
        } else if (angle == 270) {
            g.fillOval(this.width/4 - 7 , this.height/2 - 7, 15, 15);
        } else if (angle == 315) {
            g.fillOval(this.width/4- 7 , this.height/4- 7, 15, 15);
        }


    }

}