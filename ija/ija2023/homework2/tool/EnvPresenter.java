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
    ToolEnvironment env;
    public EnvPresenter(ToolEnvironment env)
    {
        this.env = env;
    }
    public void open() {

        FieldView field = new FieldView(env);
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 1000);
        frame.getContentPane().add(field, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);

    }

    public FieldView fieldAt(Position pos) {

        return null;
    }
}
