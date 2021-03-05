package com.stroby;

public class Node {
    private Point point;
    private int[] paths;
    private Node[] neigbor;

    public Node(int x, int y, SequentialMaze maze){
        this.point = new Point(x, y);

        int index = 0;
        int[] paths = new int[4];
        Direction[] dirs = Direction.values();
        for (int i = 0; i < dirs.length; i++){
            Point neighbor = point.getNeighbor(dirs[i]);
            if (maze.hasPassage(point, neighbor)){
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
            neigbor[i] = nodes[point.x + dirs[paths[i]].dx][point.y + dirs[paths[i]].dy];
        }
    }

    public boolean equals(Node node){
        return point.equals(node.point);
    }

    public Point getPoint() {
        return point;
    }

    public Node[] getNeigbors() {
        return neigbor;
    }
}
