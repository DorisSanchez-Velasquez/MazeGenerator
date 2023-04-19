//package com.company;
import java.util.*;
import java.util.Arrays;
import java.util.Collections;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

class mazeSolver extends JPanel {
    private final int width;
    private final int height;
    private int[][] maze;
    private HashMap<String, ArrayList<Integer>> logicMaze; 
    //ArrayList<Type> str = new ArrayList<Type>();
    private BufferedImage backgroundImage;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            int cellSize = 20;
            int mazeWidth = width * cellSize;
            int mazeHeight = height * cellSize;
            g.drawImage(backgroundImage, (mazeWidth - backgroundImage.getWidth()) / 2,
                    (mazeHeight - backgroundImage.getHeight()) / 2, this);
        }
        drawMaze(g);
    }




    // Add this new method to draw the maze using the Graphics object
    private void drawMaze(Graphics g) {
        // (use g to draw the maze based on the printMaze() method)
        int cellSize = 20; // Change this value to adjust the size of the maze cells
        g.setColor(Color.cyan); //Some color looks better than others

        int blockSize = cellSize / 2;
        int blockCenter = (cellSize - blockSize) / 2;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                int cellX = x * cellSize;
                int cellY = y * cellSize;

                ArrayList<Integer> cellLogic = new ArrayList<Integer> ();

                if ((maze[x][y] & DIRECTIONS.NORTH.cell) == 0) {
                    cellLogic.add(1);
                    g.drawLine(cellX, cellY, cellX + cellSize, cellY);
                }
                else{
                    cellLogic.add(0);
                }
                if ((maze[x][y] & DIRECTIONS.SOUTH.cell) == 0) {
                    cellLogic.add(1);
                    g.drawLine(cellX, cellY + cellSize, cellX + cellSize, cellY + cellSize);
                }
                else{
                    cellLogic.add(0);
                }
                if ((maze[x][y] & DIRECTIONS.EAST.cell) == 0) {
                    cellLogic.add(1);
                    g.drawLine(cellX + cellSize, cellY, cellX + cellSize, cellY + cellSize);
                }
                else{
                    cellLogic.add(0);
                }
                if ((maze[x][y] & DIRECTIONS.WEST.cell) == 0) {
                    cellLogic.add(1);
                    g.drawLine(cellX, cellY, cellX, cellY + cellSize);
                }
                else{
                    cellLogic.add(0);
                }
                
                String cell = x + "," + y;
                logicMaze.put(cell, cellLogic);
            }
        }

        // Draw start and end points
        g.setColor(Color.red);
        g.fillRect(blockCenter, blockCenter, blockSize, blockSize); // Draw start point

        
        g.setColor(Color.green);
        g.fillRect((width - 1) * cellSize + blockCenter, (height - 1) * cellSize +
                blockCenter, blockSize, blockSize); // Draw end point

        ArrayList<MazeCells> shortestPath = solveMazeBFS(g); //Store the shortest path
        printShortestPath(shortestPath, g, cellSize, blockCenter, blockSize); //Color the shortest path on the frame
    }





    private ArrayList<MazeCells> solveMazeBFS(Graphics g){
        //Store the directions: North, South, East, West
        int[][] Directions = {{0,-1}, {0,1},{1,0},{-1,0}};

        //Create a linked list that will store the new paths for each cell
        LinkedList<MazeCells> nextMazeCell = new LinkedList<>();

        //Add the first cell: 0,0 to the current path
        MazeCells startCell = new MazeCells(0,0);
        nextMazeCell.add(startCell);

        //Check if the current path is empty
        while(!nextMazeCell.isEmpty()){
            //Get the current maze cell
            MazeCells currentCell = nextMazeCell.remove();

            //Get the logic for the walls at the current cell
            String cellName = currentCell.getXValue() + "," + currentCell.getYValue();
            ArrayList<Integer> currentCellWalls = logicMaze.get(cellName);

            //If the cell is not within the maze dimensions and the cell has not been visited, break iteration
            if(!isTrue(currentCell.getXValue(), currentCell.getYValue()) || currentCell.isVisited())
            {
                continue;
            }

            //If the cell is the exit, then start backtracking the path
            if(currentCell.getXValue() == width-1 && currentCell.getYValue() == height-1)
            {
                //Return the path from the start cell to the end cell
                return backtrackPath(currentCell);
            }

            int wall = 0;
            for(int[] direction : Directions)
            {
                //If a wall exist N, S, E, W at the current cell, then don't move in that direction
                if(currentCellWalls.get(wall) != 1)
                {
                    MazeCells nextCell = new MazeCells(currentCell.getXValue() + direction[0], currentCell.getYValue() + direction[1], currentCell);
                    nextMazeCell.add(nextCell);
                    currentCell.setVisited(); 
                }
                wall++; //Increment the wall that is being checked
            }
        }

        //If end cell is never found, return an empty path.
        return new ArrayList<MazeCells>();
    }

    private ArrayList<MazeCells> backtrackPath(MazeCells currentCell)
    {
        ArrayList<MazeCells> path = new ArrayList<>(); //Stores the path from the currentCell
        MazeCells iteration = currentCell;

        while(iteration != null) //If the cell still has a parent, 
        {
            path.add(iteration); //Add the currentCell to the path
            iteration = iteration.getParentCell(); //Get the parent of the currentCell
        }

        return path; //Return the path form the start cell to the end cell
    }

    public void printShortestPath(ArrayList<MazeCells> shortestPath, Graphics g, int cellSize, int blockCenter, int blockSize)
    {
        //Iterate through every cell in the maze found in the shortest path and color it blue
        g.setColor(Color.blue);
        for(MazeCells cell: shortestPath)
        {
            if(!(cell.getXValue() == 0 && cell.getYValue() == 0) && !(cell.getXValue() == width-1 && cell.getYValue() == height-1) )
            {
                int cellX = cell.getXValue() * cellSize;
                int cellY = cell.getYValue() * cellSize;
                g.fillRect(cellX + blockCenter, cellY + blockCenter, blockSize, blockSize);
            }
        }
    }




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
        DIRECTIONS(int cell, int x, int y) {
            this.cell = cell;
            // refer to the change in x and y when moving in a specified direction
            this.x = x;
            this.y = y;
        }
    }





    private void playBackgroundMusic(String musicFilePath) {
        try {
            File musicFile = new File(musicFilePath);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(musicFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error loading background music");
            e.printStackTrace();
        }
    }





    // dimensions of the maze
    public mazeSolver(int width, int height) {
        this.width = width;
        this.height = height;
        maze = new int[this.width][this.height];
        logicMaze = new HashMap<String, ArrayList<Integer>>();
        generateMaze(0, 0);

        try {
            // Replace this URL with the URL or path of your image
            File imageFile = new File("C:\\Users\\soka\\Documents\\school\\Design and Algorithms\\pacbackground.jpg");
            backgroundImage = ImageIO.read(imageFile);
        } catch (IOException e) {
            System.err.println("Error loading background image");
            e.printStackTrace();
        }
        int cellSize = 20; // Change this value to adjust the size of the maze cells
        setPreferredSize(new Dimension(width * cellSize, height * cellSize));
        // Add this block of code to the mazeSolver constructor
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == 'r' || e.getKeyChar() == 'R') {
                    maze = new int[width][height];
                    generateMaze(0, 0);
                    repaint();
                }
            }
        });
    }






    private void generateMaze(int x_cord, int y_cord) {
        // creates a list of all the values in the directions
        DIRECTIONS[] neighbors = DIRECTIONS.values();
        // shuffles them because it's a randomized maze
        Collections.shuffle(Arrays.asList(neighbors));
        // loops through the different directions
        for (DIRECTIONS dir : neighbors) {
            // new coordinates when adding the direction to the current coordinates
            int new_xcoord = x_cord + dir.x;
            int new_ycoord = y_cord + dir.y;

            if (isTrue(new_xcoord, new_ycoord) && isNotVisited(new_xcoord, new_ycoord)) {
                // performing bitwise or operation between the value at the position and
                // direction
                maze[x_cord][y_cord] |= dir.cell;
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
        int empty = 0;
        return maze[x][y] == empty;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
        JFrame frame = new JFrame("Maze Generator");
        mazeSolver maze = new mazeSolver(6, 6);
        frame.add(maze);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        maze.playBackgroundMusic("C:\\Users\\soka\\Documents\\school\\Design and Algorithms\\backgroundmusic.wav");
        });
    }
}

class MazeCells{
    private MazeCells parentCell;
    private int xCoordinate;
    private int yCoordinate;
    private boolean visited;

    //MAZE CELLS CONSTRUCTOR
    public MazeCells(int x, int y)
    {
        this.visited = false;
        this.parentCell = null;
        this.xCoordinate = x;
        this.yCoordinate = y;
    }

    public MazeCells(int x, int y, MazeCells parentCell)
    {
        this.parentCell = parentCell;
        this.xCoordinate = x;
        this.yCoordinate = y;
    }

    public void setVisited()
    {
        this.visited = true;
    }

    public boolean isVisited()
    {
        return this.visited;
    }

    public MazeCells getParentCell()
    {
        return this.parentCell;
    }

    public int getXValue()
    {
        return this.xCoordinate;
    }

    public int getYValue()
    {
        return this.yCoordinate;
    }
}
