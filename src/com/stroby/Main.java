package com.stroby;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Main {

    private static final int width = 200;
    private static final int height = 150;

    public static void main(String[] args) {

        SequentialMaze maze = new SequentialMaze(width, height, new Point(width-1, 0), new Point(0, height-1));

        StartTimer();
        Point[] solution = maze.solve();
        TimeCheck();
        System.out.println(maze.checkSolution(solution));

        Node[][] nodes = convertMaze(maze);

        StartTimer();
        Node[] path = solveMazeRecursiv(nodes[width-1][0], nodes[0][height-1], new Node[width * height], 0);
        TimeCheck();

        Point[] mySolution = convertSolution(path);
        System.out.println(maze.checkSolution(mySolution));

        showSolution(maze, mySolution);
    }

    private static long start;
    private static void StartTimer(){
        start = System.currentTimeMillis();
    }
    private static void TimeCheck(){
        long differnce = System.currentTimeMillis() - start;
        System.out.println(differnce + " ms");
    }

    private static Node[][] convertMaze(SequentialMaze maze){

        Node[][] nodes = new Node[width][height];

        for (int i = 0; i < width; i++){
            for (int j = 0; j < height; j++){
                nodes[i][j] = new Node(i, j, maze);
            }
        }

        for (int i = 0; i < width; i++){
            for (int j = 0; j < height; j++){
                nodes[i][j].updateNeigbors(nodes);
            }
        }
        return nodes;
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

    private static Node[] solveMazeRecursiv(Node current, Node end, Node[] path, int pathIndex){
        path[pathIndex] = current;
        if (current.equals(end)){
            return path;
        }

        Node[] neigbors = current.getNeigbors();
        for (int i = 0; i < neigbors.length; i++){
            if (pathIndex > 1 && path[pathIndex - 1].equals(neigbors[i])) {continue;}
            Node[] solution = solveMazeRecursiv(neigbors[i], end, path, pathIndex + 1);
            if (solution != null){
                return solution;
            }
        }
        path[pathIndex] = null;
        return null;
    }

    private static Node[] solveMazeIntera(Node start, Node end){
        Node[] path = new Node[width * height];

        int pathIndex = 0;
        Node current = start;
        boolean flound = false;
        while (path[pathIndex].equals(end)){
            Node[] neigbors = current.getNeigbors();

            for (int i = 0; i < neigbors.length; i++){

            }
        }

        return path;
    }

    private static Point[] convertSolution(Node[] solution){
        ArrayList<Point> list = new ArrayList<>();
        for (int i = 0; i < solution.length; i++){
            if (solution[i] == null) {break;}
            list.add(solution[i].getPoint());
        }
        Point[] points = new Point[list.size()];
        for (int i = 0; i < list.size(); i++){
            points[i] = list.get(i);
        }
        return points;
    }
}
