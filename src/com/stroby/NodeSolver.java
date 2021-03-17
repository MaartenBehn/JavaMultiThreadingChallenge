package com.stroby;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class NodeSolver {

    public static void run(){

        Node[][] nodes = NodeSolver.convertMaze( Main.maze);
        NodeSolver.start = nodes[ Main.width-1][0];
        NodeSolver.end = nodes[0][ Main.height-1];

        System.out.println("Node Single Thread");
        Main.StartTimer();
        Node[] path = NodeSolver.solveMazeIntera();
        Main.TimeCheck();
        Point[] mySolution = NodeSolver.convertSolution(path);
        Main.maze.checkSolution(mySolution);

        if ( Main.width *  Main.height <= 100 * 100){
            System.out.println("Node Single Thread Recursiv");
            Main.StartTimer();
            Node[] path2 = NodeSolver.solveMazeRecursiv(NodeSolver.start, new Node[ Main.width *  Main.height], 0);
            Main.TimeCheck();
            Point[] mySolution2 = NodeSolver.convertSolution(path2);
            Main.maze.checkSolution(mySolution2);
        }

        System.out.println("Node Multi 3 Thread");
        Main.StartTimer();
        NodeSolver.solveMazeMulti(3, new int[][]{
                new int[]{0},
                new int[]{1},
                new int[]{2},
        });

        System.out.println("Nodes done");
    }

    public static Node[][] convertMaze(SequentialMaze maze){

        Node[][] nodes = new Node[Main.width][Main.height];

        for (int i = 0; i < Main.width; i++){
            for (int j = 0; j < Main.height; j++){
                nodes[i][j] = new Node(i, j, maze);
            }
        }

        for (int i = 0; i < Main.width; i++){
            for (int j = 0; j < Main.height; j++){
                nodes[i][j].updateNeigbors(nodes);
            }
        }
        return nodes;
    }

    public static Node start;
    public static Node end;
    public static Node[] solveMazeRecursiv(Node current, Node[] path, int pathIndex){
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
    public static Node[] solveMazeIntera(){
        Node[] path = new Node[Main.width * Main.height];
        path[0] = start;
        int[] stack = new int[Main.width * Main.height];

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
        Node[] path = new Node[Main.width * Main.height];
        path[0] = start;
        int[] stack = new int[Main.width * Main.height];

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
    public static void solveMazeMulti(int threads, int[][] rules){
        ExecutorService es = Executors.newCachedThreadPool();
        for(int i=0; i<threads; i++)
            es.execute(new MultiNodeSolver(rules[i]));
        es.shutdown();
        try {
            boolean finished = es.awaitTermination(10, TimeUnit.MINUTES);
            if (!finished){
                System.out.println("Time out!");
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void finisched(Node[] path){
        Main.TimeCheck();
        Point[] mySolution = convertSolution(path);
        Main.maze.checkSolution(mySolution);
    }

    public static Point[] convertSolution(Node[] solution){
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

class MultiNodeSolver extends Thread {
    int[] rule;

    public MultiNodeSolver(int[] rule) {
        this.rule = rule;
    }

    @Override
    public void run() {
        NodeSolver.finisched(NodeSolver.solveMazeIteraWithRule(rule));
    }
}
