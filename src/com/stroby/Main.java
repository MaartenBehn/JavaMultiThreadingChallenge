package com.stroby;

import javax.swing.*;
import java.awt.*;

public class Main {

    private static final int width = 100;
    private static final int height = 100;

    public static void main(String[] args) {

        long start = System.currentTimeMillis(); // Zeitmessung start

        long stop = System.currentTimeMillis();// Zeitmessung stop
        long vergangeneZeit = stop - start;// Zeit berechnen

        SequentialMaze maze = new SequentialMaze(width, height, new Point(width-1, 0), new Point(0, height-1));
        Point[] solution = maze.solve();
        showSolution(maze, solution);
    }

    private static void showSolution(SequentialMaze maze, Point[] solution){
        if (!maze.smallEnoughToDisplay()) { return;}

        JFrame frame = new JFrame("Sequential maze solver");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // TODO: Fenster erscheint initial immer etwas kleiner als die
        // angegebene Frame-Größe, deshalb width+5 und height+10:
        frame.setSize((width+5)*10, (height+10)*10);
        frame.getContentPane().add(maze, BorderLayout.CENTER);
        frame.setVisible(true); // will draw the maze (without solution)
        maze.displaySolution(solution, frame);
    }

    private static Point[] solveMaze(SequentialMaze maze){



        return null;
    }
}
