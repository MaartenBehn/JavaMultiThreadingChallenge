package com.stroby;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Main {

    private static final int width = 1000;
    private static final int height = 1000;
    private static SequentialMaze maze;

    public static void main(String[] args) {

        maze = new SequentialMaze(width, height, new Point(width-1, 0), new Point(0, height-1));

        System.out.println("Original Version");
        StartTimer();
        Point[] solution = maze.solve();
        TimeCheck();
        System.out.println(maze.checkSolution(solution));

        Node[][] nodes = convertMaze(maze);
        start = nodes[width-1][0];
        end = nodes[0][height-1];

        System.out.println("Single Thread");
        StartTimer();
        Node[] path = solveMazeIntera();
        TimeCheck();
        Point[] mySolution = convertSolution(path);
        System.out.println(maze.checkSolution(mySolution));

        showSolution(maze, mySolution);

        switch (3){
            case 3:
                System.out.println("Multi 3 Thread");
                StartTimer();
                solveMazeMulti(3, new int[][]{
                        new int[]{0},
                        new int[]{1},
                        new int[]{2},
                });
                break;
            case 5:
                System.out.println("Multi 5 Thread");
                StartTimer();
                solveMazeMulti(5, new int[][]{
                        new int[]{0},
                        new int[]{1},
                        new int[]{2},
                        new int[]{0,1},
                        new int[]{1,0},
                });
                break;
        }
    }

    private static long startTimer;
    private static void StartTimer(){
        startTimer = System.currentTimeMillis();
    }
    private static void TimeCheck(){
        long differnce = System.currentTimeMillis() - startTimer;
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

    private static Node start;
    private static Node end;
    private static Node[] solveMazeRecursiv(Node current, Node[] path, int pathIndex){
        path[pathIndex] = current;
        if (current.equals(end)){
            return path;
        }

        Node[] neigbors = current.getNeigbors();
        for (int i = 0; i < neigbors.length; i++){
            if (pathIndex > 1 && path[pathIndex - 1].equals(neigbors[i])) {continue;}
            Node[] solution = solveMazeRecursiv(neigbors[i], path, pathIndex + 1);
            if (solution != null){
                return solution;
            }
        }
        path[pathIndex] = null;
        return null;
    }
    private static Node[] solveMazeIntera(){
        Node[] path = new Node[width * height];
        path[0] = start;
        int[] stack = new int[width * height];

        int index = 0;
        while (!path[index].equals(end)){

            Node[] neigbors = path[index].getNeigbors();
            if (stack[index] >= neigbors.length){
                stack[index] = 0;
                path[index] = null;
                index--;

            }else {
                if (!(index != 0 && neigbors[stack[index]].equals(path[index-1]))){
                    path[index + 1] = neigbors[stack[index]];
                    stack[index]++;
                    index++;
                }else {
                    stack[index]++;
                }
            }

        }

        return path;
    }
    public static Node[] solveMazeIteraWithRule(int[] rule){
        Node[] path = new Node[width * height];
        path[0] = start;
        int[] stack = new int[width * height];

        int index = 0;
        while (!path[index].equals(end)){
            Node[] neigbors = path[index].getNeigbors();

            int neigborLenght = neigbors.length;
            int ruleValue = rule[index % rule.length];
            int stackValue = stack[index] + ruleValue;

            if(stackValue >= neigborLenght + ruleValue){
                stack[index] = 0;
                path[index] = null;
                index--;
            } else  {
                while (stackValue >= neigborLenght){
                    stackValue -= neigborLenght;
                }
                stack[index]++;

                if (!(index != 0 && neigbors[stackValue].equals(path[index - 1]))){
                    path[index + 1] = neigbors[stackValue];
                    index++;
                }
            }
        }

        return path;
    }
    private static void solveMazeMulti(int threads, int[][] rules){
        for (int i = 0; i < threads; i++){
            MultiSolver solver = new MultiSolver(rules[i]);
            solver.start();
        }
    }
    public static void finisched(Node[] path){
        TimeCheck();
        Point[] mySolution = convertSolution(path);
        maze.checkSolution(mySolution);
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
