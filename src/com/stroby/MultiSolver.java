package com.stroby;

public class MultiSolver extends Thread {
    int[] rule;

    public MultiSolver(int[] rule) {
        this.rule = rule;
    }

    @Override
    public void run() {
        Main.finisched(Main.solveMazeIteraWithRule(rule));
    }
}
