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

                g.setColor(Color.BLUE);
                g.fillRect(cellX + blockCenter, cellY +
                        blockCenter, blockSize, blockSize); // Draw end point

                if ((maze[x][y] & DIRECTIONS.NORTH.cell) == 0) {
                    cellLogic.add(1);
                    g.drawLine(cellX, cellY, cellX + cellSize, cellY);
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

        //TO LOOP THROUGH THE MATRIX: THIS CODE IS FOR ITERATING AND FILLING WITH COLOR
        // g.setColor(Color.BLUE);
        // g.fillRect(1 * cellSize + blockCenter, 0 * cellSize +
        //         blockCenter, blockSize, blockSize); // Draw end point

        System.out.println(logicMaze);
        solveMazeBFS(g);
    }





    private void solveMazeBFS(Graphics g){
        int cellSize = 20; // Change this value to adjust the size of the maze cells

        int blockSize = cellSize / 2;
        int blockCenter = (cellSize - blockSize) / 2;
        g.setColor(Color.BLUE);

        // g.fillRect(cellX + blockCenter, cellY +
        // blockCenter, blockSize, blockSize); // Draw end point

        // int cellX = x * cellSize;
        // int cellY = y * cellSize;
        // String cellName = y + "," + x;
        // ArrayList<Integer> cellWalls = logicMaze.get(cellName);

        //Store the north, south, east, and west info of cells
        MazeCells N, S, E, W;

        //Creating queue to perform BFS
        Queue<Integer> mazeCells = new LinkedList<>();
        MazeCells beginCell = new MazeCells(0, 0);

        beginCell.markVisited(); //Set the starting cell as visited
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
        mazeSolver maze = new mazeSolver(4, 4);
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
    private boolean visited;
    private MazeCells parentCell;
    private int xCoordinate;
    private int yCoordinate;

    //MAZE CELLS CONSTRUCTOR
    public MazeCells(int x, int y)
    {
        visited = false;
        parentCell = null;
        this.xCoordinate = x;
        this.yCoordinate = y;
    }

    public void markVisited()
    {
        visited = true;
    }

    public boolean isVisited()
    {
        return this.visited;
    }

    public MazeCells getParentCell()
    {
        return this.parentCell;
    }

    public void changeParentCell(MazeCells child)
    {
        child.parentCell = this;
    }

    public int xValue()
    {
        return this.xCoordinate;
    }

    public int yValue()
    {
        return this.yCoordinate;
    }
}
