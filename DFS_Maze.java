import java.util.Arrays;
import java.util.Collections;

public class DFS_Maze {
  private final int width;
  private final int height;
  private final int[][] maze;
  public final int[][] logicalMaze; 
  private final int empty = 0;

  // all the different directions dfs can be performed
  private enum DIRECTIONS {
    NORTH(1, 0, -1),
    SOUTH(2, 0, 1),
    EAST(4, 1, 0),
    WEST(8, -1, 0);

    // stores the opposite direction of the forward references
    private DIRECTIONS opposite;
    static {
      NORTH.opposite = SOUTH;
      SOUTH.opposite = NORTH;
      WEST.opposite = EAST;
      EAST.opposite = WEST;
    }
    private final int cell, x, y;

    // bits and direction
    private DIRECTIONS(int cell, int x, int y) {
      this.cell = cell;
      // refer to the change in x and y when moving in a specified direction
      this.x = x;
      this.y = y;
    }
  }

  // dimensions of the maze
  public DFS_Maze(int width, int height) {
    this.width = width;
    this.height = height;
    maze = new int[this.width][this.height];
    logicalMaze = new int[17][33];
    generateMaze(0, 0);
  }

  // drawn maze
  public void printMaze() {
    for (int y = 0; y < height; y++) {
      StringBuilder northEdge = new StringBuilder();
      StringBuilder westEdge = new StringBuilder();
      // north and west edges for maze
      for (int x = 0; x < width; x++) {
        northEdge.append((maze[x][y] & 1) == 0 ? "+---" : "+   ");
        westEdge.append((maze[x][y] & 8) == 0 ? "+   " : "    ");
        //System.out.println(westEdge);
      }
      northEdge.append("+");
      westEdge.append("|");
      System.out.println(northEdge.toString());
      createLogicalMaze(y, northEdge.toString());
      System.out.println(westEdge.toString());
      createLogicalMaze(y, northEdge.toString());
    }
    // draw the bottom line for the maze
    StringBuilder bottomEdge = new StringBuilder();
    for (int x = 0; x < width; x++) {
      bottomEdge.append("+---");
    }
    bottomEdge.append("+");
    System.out.println(bottomEdge.toString());

    printLogicMaze(logicalMaze);
  }

  private void generateMaze(int x_coord, int y_coord) {
    // creates a list of all the values in the directions
    DIRECTIONS[] neighbors = DIRECTIONS.values();
    // shuffles them because it's a randomized maze
    Collections.shuffle(Arrays.asList(neighbors));
    // loops through the different directions
    for (DIRECTIONS dir : neighbors) {
      // new coordinates when adding the direction to the current coordinates
      int new_xcoord = x_coord + dir.x;
      int new_ycoord = y_coord + dir.y;

      if (isTrue(new_xcoord, new_ycoord) && isNotVisited(new_xcoord, new_ycoord)) {
        // performing bitwise or operation between the value at the position and
        // direction
        maze[x_coord][y_coord] |= dir.cell;
        maze[new_xcoord][new_ycoord] |= dir.opposite.cell;
        generateMaze(new_xcoord, new_ycoord);
      }
    }
  }

  // checks if coordinates are within the maze
  private boolean isTrue(int x, int y) {
    return (x >= 0 && y >= 0 && x < width && y < height);
  }

  // valid to move if there's no wall
  private boolean isNotVisited(int x, int y) {
    return maze[x][y] == empty;
  }

/*------------------------------------------------------------------------------------------------------------- */
// BFS MAZE SOLVER

public void createLogicalMaze(int height, String logic){
    for(int width = 0; width < logic.length(); width++){
        if(logic.charAt(width) == ' '){
            logicalMaze[height][width] = 0;
        }
        else{
            logicalMaze[height][width] = 1;
        }
    }
}

public void printLogicMaze(int[][] maze)
{
    for(int i = 0; i < maze.length; i++)
    {
      System.out.println();
      for(int j = 0; j < maze.length; j++)
      {
          System.out.print(maze[i][j] + " ");
      }
    }
}

/*
 * everytime, the strings builder object is created and filled; Send to a function that adds it to the logical array
 * That stores which ones are spaces and which ones are walls.
 * Create a hashmap that stores the patterns within the grid so when it is time to 
 */







  public static void main(String[] args) {
    // prints out maze with any dimensions
    DFS_Maze maze = new DFS_Maze(8, 8);
    maze.printMaze();

  }
}