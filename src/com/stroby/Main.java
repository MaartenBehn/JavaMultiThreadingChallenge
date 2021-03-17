package com.stroby;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Main {

    public static final int width = 1000;
    public static final int height = 1000;
    public static SequentialMaze maze;

    public static void main(String[] args) {

        maze = new SequentialMaze(width, height, new Point(width-1, 0), new Point(0, height-1));

        System.out.println("Original Version");
        StartTimer();
        Point[] solution = maze.solve();
        TimeCheck();
        System.out.println(maze.checkSolution(solution));

        NodeSolver.run();

        showSolution(maze, solution);
    }

    private static long startTimer;
    public static void StartTimer(){
        startTimer = System.currentTimeMillis();
    }
    public static void TimeCheck(){
        long differnce = System.currentTimeMillis() - startTimer;
        System.out.println(differnce + " ms");
    }

    private static void showSolution(SequentialMaze maze, Point[] solution){
        if (!maze.smallEnoughToDisplay()) { return;}

        JFrame frame = new JFrame("Sequential maze solver");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // angegebene Frame-Größe, deshalb width+5 und height+10:
        frame.setSize((width+5)* SequentialMaze.CELL_PX, (height+10)* SequentialMaze.CELL_PX);
        frame.getContentPane().add(maze, BorderLayout.CENTER);
        frame.setVisible(true); // will draw the maze (without solution)
        maze.displaySolution(solution, frame);
    }
}
