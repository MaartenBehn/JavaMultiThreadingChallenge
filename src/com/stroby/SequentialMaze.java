/*
 * Generate a perfect maze  using the depth-first algorithm
 * (www.astrolog.org/labyrnth/algrithm.htm), display it unless too large
 * (as ASCII graphics and using Swing graphics), solve it (using depth first
 * search) and display the solution, again unless too large, as a list of 
 * 2D coordinates and using Swing graphics.
 * 
 * Author: Holger.Peine@fh.hannover.de
 * 
 * Source of maze representation (incl. enum Direction)  and ASCII output 
 * generation : http://rosettacode.org/wiki/Maze#Java
 * 
 */

package com.stroby;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Arrays;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;


final class Point {
  final int x, y;
  Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  int getX() { return x; }
  int getY() { return y; }

  final Point getNeighbor(Direction dir) {
    return new Point(x+dir.dx, y+dir.dy);
  }
  
  @Override
  public String toString() {
    return "("+x+", "+y+")";
  }

  @Override
  public boolean equals(Object other) {
    if (other == this)
      return true;
    if (other.getClass() != this.getClass())
      return false;
    Point p = (Point)other;
    return x == p.x && y == p.y;
  }
  
  @Override
  public int hashCode() {
    return 3001*x+y;  // 3001 is prime
  }
}

final class PointAndDirection {
  final private Point point;
  public Point getPoint() {
    return point;
  }
  final private Direction directionToBranchingPoint;
  public Direction getDirectionToBranchingPoint() {
    return directionToBranchingPoint;
  }
  PointAndDirection(Point p, Direction direction) {
    this.point = p;
    directionToBranchingPoint = direction;
  }
}

enum Direction {
  N(1, 0, -1), S(2, 0, 1), E(4, 1, 0), W(8, -1, 0);
  final int bit;
  final int dx;
  final int dy;
  Direction opposite;

  // use the static initializer to resolve forward references
  static {
    N.opposite = S;
    S.opposite = N;
    E.opposite = W;
    W.opposite = E;
  }

  private Direction(int bit, int dx, int dy) {
    this.bit = bit;
    this.dx = dx;
    this.dy = dy;
  }
  
  @Override
  public String toString() {
    switch(this) {
      case N: return "N";
      case S: return "S";
      case W: return "W";
      case E: return "E";
      default: return "?";
    }
  }
}

@SuppressWarnings("serial")
final public class SequentialMaze extends JPanel {

  private static final int  CELL_PX = 10;  // width and length of the maze cells  in pixels
  private static final int  HALF_WALL_PX = 2;  // thickness/2 of the maze walls  in pixels
  // mazes with more pixels than this (in one or both directions) will not be graphically displayed:
  private static final int MAX_PX_TO_DISPLAY = 1000;

  private final int width;  // total number of cells in x direction
  private final int height;  // total number of cells in y direction
  
 
  private final int[][] passages;
  /*    
   *  Each int represents a cell in the maze with the passages possible from this
   *  cell. Its four least significant bits are interpreted as one flag for each direction
   *  (see enum Direction for which bit means which direction) indicating whether 
   *  there is a passage from this cell in that direction (note that passages
   *  and walls are not cells, but represented indirectly by these flags).
   *  Initially all cells are 0, i.e. have no passage from them (i.e. surrounded
   *  by walls on all their four sides).
   */ 

  private final boolean[][] visited; 
  // for each cell in the maze: Has solve() visited it yet?
  
  private final Point start;
  private final Point end;
  
  private Point[] solution = null; // set to solution path once that has been computed

  public SequentialMaze(int width, int height, Point start, Point end) {
    this.width = width;
    this.height = height;
    this.start = start;
    this.end = end;
    passages = new int[width][height];  // initially all 0
    visited = new boolean[width][height]; // initially all false
    generate();
  }

/**
 * Generate a perfect maze (i.e. one without cycles) using the depth-first algorithm
 * (www.astrolog.org/labyrnth/algrithm.htm) 
*/
  

  private void generate() {
    ArrayDeque<Point> pointsToDo = new ArrayDeque<Point>();
    Point current;
    pointsToDo.push(start);
    while (!pointsToDo.isEmpty()) {
      current = pointsToDo.pop();
      int cx = current.getX();
      int cy = current.getY();
      Direction[] dirs = Direction.values();
      Collections.shuffle(Arrays.asList(dirs));
      // For all unvisited neighboring cells in random order: 
      // Make a passage from the current cell to that neighbor
      for (Direction dir : dirs) {
        // Pick random neighbor of current cell as new cell (nx, ny)
        int nx = cx + dir.dx;
        int ny = cy + dir.dy;
        Point neighbor = new Point(nx, ny);
  
        if (contains(neighbor) // If new cell (nx, ny) is still in the maze ...
            && passages[nx][ny] == 0) { // ... and has no passage yet, i.e. has not been visited
                                    // (remove this condition to create a cycle!)
          // Make a passage from current to neighbor
          passages[cx][cy] |= dir.bit;
          passages[nx][ny] |= dir.opposite.bit;

          // Remember to continue from this neighbor later on
          pointsToDo.push(neighbor);
        }
      }
    }
  }
  
  private boolean contains(Point p) {
    return 0 <= p.getX() && p.getX() < width && 
          0 <= p.getY() && p.getY() < height;
  }

  private boolean hasPassage(Point from, Point to) {
    if (!contains(from) ||  !contains(to)) {
      return false;
    }
    if (from.getNeighbor(Direction.N).equals(to))
      return (passages[from.getX()][from.getY()] & Direction.N.bit) != 0;
    if (from.getNeighbor(Direction.S).equals(to))
      return (passages[from.getX()][from.getY()] & Direction.S.bit) != 0;
    if (from.getNeighbor(Direction.E).equals(to))
      return (passages[from.getX()][from.getY()] & Direction.E.bit) != 0;
    if (from.getNeighbor(Direction.W).equals(to))
      return (passages[from.getX()][from.getY()] & Direction.W.bit) != 0;
    return false;  // To suppress warning about undefined return value
  }

  private boolean visitedBefore(Point p) {
    return visited[p.getX()][p.getY()];
  }

  private void visit(Point p) {
    // DEBUG System.out.println("Visiting " + p);
    visited[p.getX()][p.getY()] = true;
  }

  private boolean checkSolution(Point[] solution) {
    Point from = solution[0];
    if (!from.equals(start)) {
      System.out.println("checkSolution fails because the first cell is" + from + ", but not  " + start);
      return false;
    }

    for (int i = 1; i < solution.length; ++i) {
      Point to = solution[i];
      if (!hasPassage(from, to)) {
        System.out.println("checkSolution fails because there is no passage from " + from + " to " + to);
        return false;
      }
      from = to;
    }
    if (!from.equals(end)) {
      System.out.println("checkSolution fails because the last cell is" + from + ", but not  " + end);
      return false;
    }
    return true;
  }

  /**
   * @return Returns a path through the maze from start to end as an array, or null if no solution exists
   */
  public Point[] solve() {

    Point current = start;
    ArrayDeque<Point> pathSoFar = new ArrayDeque<Point>();  // Path from start just before current

    ArrayDeque<PointAndDirection> backtrackStack = new ArrayDeque<PointAndDirection>();
    // Used as a stack: Branches not yet taken; solver will backtrack to these branching points later
    // Is it faster to allocate backtrackStack with width*height elements right away?


    while (!current.equals(end)) {
      Point next = null;
      visit(current);

      // Use first random unvisited neighbor as next cell, push others on the backtrack stack: 
      Direction[] dirs = Direction.values();
      for (Direction directionToNeighbor: dirs) {
        Point neighbor = current.getNeighbor(directionToNeighbor);
        if (contains(neighbor) && hasPassage(current, neighbor) && !visitedBefore(neighbor)) {
          if (next == null) // 1st unvisited neighbor
            next = neighbor;
          else // 2nd or higher unvisited neighbor: Save neighbor as starting cell for a later backtracking
            backtrackStack.push(new PointAndDirection(neighbor, directionToNeighbor.opposite));
        }
      }
      // Advance to next cell, if any:
      if (next != null) {
        // DEBUG System.out.println("Advancing from " + current + " to " + next);
        pathSoFar.addLast(current);
        current = next;
      } else { 
        // current has no unvisited neighbor: Backtrack, if possible
        if (backtrackStack.isEmpty())
          return null; // No more backtracking avaible: No solution exists

        // Backtrack: Continue with cell saved at latest branching point:
        PointAndDirection pd = backtrackStack.pop();
        current = pd.getPoint();
        Point branchingPoint = current.getNeighbor(pd.getDirectionToBranchingPoint());
        // DEBUG System.out.println("Backtracking to " +  branchingPoint);
        // Remove the dead end from the top of pathSoFar, i.e. all cells after branchingPoint:
        while (!pathSoFar.peekLast().equals(branchingPoint)) {
          // DEBUG System.out.println("    Going back before " + pathSoFar.peekLast());
          pathSoFar.removeLast();
        }
      }
    }
    pathSoFar.addLast(current);
     // Point[0] is only for making the return value have type Point[] (and not Object[]):
    return pathSoFar.toArray(new Point[0]); 
  }
  
  @Override
  protected void paintComponent(Graphics graphics) {
    super.paintComponent(graphics);
    display(graphics);
  }
  
  public void print() {
    for (int i = 0; i < height; i++) {
      // draw the north edges
      for (int j = 0; j < width; j++) {
        System.out.print((passages[j][i] & 1) == 0 ? "+---" : "+   ");
      }
      System.out.println("+");
      // draw the west edges
      for (int j = 0; j < width; j++) {
        System.out.print((passages[j][i] & 8) == 0 ? "|   " : "    ");
      }
      // draw the far east edge
      System.out.println("|");
    }
    // draw the bottom line
    for (int j = 0; j < width; j++) {
      System.out.print("+---");
    }
    System.out.println("+");
  }
  
  public boolean smallEnoughToDisplay() {
    return width*CELL_PX <= MAX_PX_TO_DISPLAY && height*CELL_PX <= MAX_PX_TO_DISPLAY;
  }

  public void display(Graphics graphics) {
    // draw white background
    graphics.setColor(Color.WHITE);
    graphics.fillRect(0, 0, width*CELL_PX, height*CELL_PX);
    
    // draw solution path, if available
    if (solution  != null) {
      graphics.setColor(Color.YELLOW);
      for (Point p: solution)
/*        // fill only white area between the walls instead of whole cell:
        graphics.fillRect(p.getX()*CELL_PX+HALF_WALL_PX, p.getY()*CELL_PX+HALF_WALL_PX, 
                      CELL_PX-2*HALF_WALL_PX, CELL_PX-2*HALF_WALL_PX); 
*/      
        graphics.fillRect(p.getX()*CELL_PX, p.getY()*CELL_PX,   CELL_PX, CELL_PX); 
    }
    
    // draw start and end cell in special colors (covering start and end cell of the solution path)
    graphics.setColor(Color.RED);
    graphics.fillRect(start.getX()*CELL_PX, start.getY()*CELL_PX, CELL_PX, CELL_PX);
    graphics.setColor(Color.GREEN);
    graphics.fillRect(end.getX()*CELL_PX, end.getY()*CELL_PX, CELL_PX, CELL_PX);
    
    // draw black walls (covering part of the solution path)
    graphics.setColor(Color.BLACK);
    for(int x = 0; x < width; ++x) {
      for(int y = 0; y < height; ++y) {
        // draw north edge of each cell (together with south edge of cell below)
        if ((passages[x][y] & Direction.N.bit) == 0)
          // y-HALF_WALL_PX will be half out of maze  for x=0 row, 
          // but that does not hurt the picture thanks to automatic cropping
          graphics.fillRect(x*CELL_PX, y*CELL_PX-HALF_WALL_PX, CELL_PX, 2*HALF_WALL_PX);
        // draw west edge of each cell (together with east edge of cell to the right)
        if ((passages[x][y] & Direction.W.bit) == 0)
          // x-HALF_WALL_PX will be half out of maze  for y=0 column, 
          // but that does not hurt the picture thanks to automatic cropping
          graphics.fillRect(x*CELL_PX-HALF_WALL_PX, y*CELL_PX, 2*HALF_WALL_PX, CELL_PX);
      }
    }
    // draw east edge of maze
    graphics.fillRect(width*CELL_PX, 0, HALF_WALL_PX, height*CELL_PX);
    // draw south edge of maze
    graphics.fillRect(0, height*CELL_PX-HALF_WALL_PX, width*CELL_PX, HALF_WALL_PX);   
  }

  public void printSolution() {
    System.out.print("Solution: ");
    for (Point p: solution)
      System.out.print(p);
    System.out.println();
  }
  
  public void displaySolution(Point[] solution, JFrame frame) {
    this.solution = solution;
    repaint();
}

  public static void main(String[] args) {
    int width = args.length >= 1 ? (Integer.parseInt(args[0])) : 10;
    int height = args.length == 2 ? (Integer.parseInt(args[1])) : 10;
    JFrame frame = null;
    
    SequentialMaze maze = new SequentialMaze(width, height, new Point(width-1, 0), new Point(0, height-1));
    
    if (maze.smallEnoughToDisplay()) {
      frame = new JFrame("Sequential maze solver");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      // TODO: Fenster erscheint initial immer etwas kleiner als die 
      // angegebene Frame-Größe, deshalb width+5 und height+10:     
      frame.setSize((width+5)*CELL_PX, (height+10)*CELL_PX);
      frame.getContentPane().add(maze, BorderLayout.CENTER);
      frame.setVisible(true); // will draw the maze (without solution)
      maze.print();
    }
    long startTime = System.currentTimeMillis();    
    Point[] solution = maze.solve();
    long endTime = System.currentTimeMillis();
    System.out.println("Computed solution of length " + solution.length + " to maze of size " + 
        width + "x" + height + " in " + (endTime - startTime) + "ms.");
    
    if (maze.smallEnoughToDisplay()) {
      maze.displaySolution(solution, frame);
        maze.printSolution();
    }
    
    if (maze.checkSolution(solution))
        System.out.println("Solution correct :-)"); 
        else
          System.out.println("Solution incorrect :-(");
  }
}