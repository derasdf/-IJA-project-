/*
 * EnvPresenter.java
 * @author Aleksandrov Vladimir xaleks03
 * @author Volodymyr Burylov xburyl00
 */ 
package ija.ija2023.homework2.tool;

import ija.ija2023.homework2.common.Robot;
import ija.ija2023.homework2.tool.common.Observable;
import ija.ija2023.homework2.tool.common.Position;
import ija.ija2023.homework2.tool.common.ToolRobot;
import ija.ija2023.homework2.tool.view.FieldView;
import ija.ija2023.homework2.tool.common.ToolEnvironment;
import ija.ija2023.homework2.tool.view.RobotView;

import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class EnvPresenter {
    public static final int FIELD_SIZE = 1000;
    ToolEnvironment env;
    public EnvPresenter(ToolEnvironment env)
    {
        this.env = env;
    }
    public void open() {

        FieldView field = new FieldView(env);
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(FIELD_SIZE, FIELD_SIZE);
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(field);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);

    }

    public FieldView fieldAt(Position pos) {
        return null;
    }
}