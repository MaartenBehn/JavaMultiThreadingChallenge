package com.stroby;

public class Node {
    private int x;
    private int y;
    private int[] paths;
    private Node[] neigbor;

    public Node(int x, int y, SequentialMaze maze){
        this.x = x;
        this.y = y;

        Point current = new Point(x, y);
        int index = 0;
        int[] paths = new int[4];
        Direction[] dirs = Direction.values();
        for (int i = 0; i < dirs.length; i++){
            Point neighbor = current.getNeighbor(dirs[i]);
            if (maze.hasPassage(current, neighbor)){
                paths[index] = i;
                index++;
            }
        }
        this.paths = new int[index];
        for (int i = 0; i < index; i++){
            this.paths[i] = paths[i];
        }
        return;
    }

    public void updateNeigbors(Node[][] nodes){
        neigbor = new Node[paths.length];
        Direction[] dirs = Direction.values();
        for (int i = 0; i < paths.length; i++){
            neigbor[i] = nodes[x + dirs[i].dx][y + dirs[i].dy];
        }
        return;
    }

    public boolean Equals(Node node){
        return node.x == x && node.y == y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
