package com.hexField;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class CellField {

    private Cell[][] cells;
    private int width, height;

    public CellField(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new Cell[height][width];
        initCells();
        colorRandomCells();     //colors random cells black
        checkForNeighbors();
    }

    private void initCells(){
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                cells[i][j] = new Cell("",i,j);
            }
        }
    }

    private void colorRandomCells(){    //colors random cells black
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                int rnd = ThreadLocalRandom.current().nextInt(1, 3 + 1);
                if (rnd == 3){
                    cells[i][j].setBackground(Color.black);
                }
            }
        }
    }



    public Cell[][] getCells() {
        return cells;
    }

    private void checkForNeighbors(){   //checks for neighbors and colors the groups
        this.cells = new CheckCells(this.cells,this.height,this.width).getCells();
    }

    public void printCells(){   //  was for testing w/o gui
        for (Cell[] x:cells) {
            for (Cell y :x) {
                System.out.print(""+y+" , ");
            }
            System.out.println();
        }
    }

    public void editField(int row ,int col,Color color){    //changes the background color of a cell
        cells[row][col].setBackground(color);
        checkForNeighbors();
    }
}


//CCL -> connected-component labeling
class CheckCells{

    private int component[];
    private int w,h;
    private Cell[][] input;
    private HashMap<Integer,Cell> colors = new HashMap<>();

    public CheckCells(Cell[][] cells,int w, int h) {
        this.component = new int[w*h];
        this.input = cells;
        this.w = w;
        this.h = h;
        main();
        addCount();
    }


    private void doUnion(int a, int b)
    {
        // get the root component of a and b, and set the one's parent to the other
        while (component[a] != a)
            a = component[a];
        while (component[b] != b)
            b = component[b];
        component[b] = a;
    }

    private void unionCoords(int x, int y, int x2, int y2)  //checks if the two cells exist and if they are filled
    {
            if (y2 < h && x2 < w && y2 >= 0 && x2 >= 0 && input[x][y].isFilled() && input[x2][y2].isFilled())
                doUnion(x*h + y, x2*h + y2);    //if both are filled
    }

    private void main()
    {
        // set up component
        for (int i = 0; i < w*h; i++)
            component[i] = i;


        for (int x = 0; x < w; x++)  //goes through all coords and checks for neighbors
            for (int y = 0; y < h; y++)
            {
                if (x%2 == 0){
                    unionCoords(x, y, x-1, y);
                    unionCoords(x, y, x+1, y);
                }else {
                    unionCoords(x, y, x-1, y+1);
                    unionCoords(x, y, x+1, y+1);
                }
                unionCoords(x, y, x+2, y);
            }

            addAllColors();         //sets the colors for the groups
            changeAllColors();      //changes all cells to the color of their group

    }

    private void addAllColors(){
        for (int x = 0; x < w; x++)
        {
            for (int y = 0; y < h; y++)
            {
                if (!input[x][y].isFilled()) continue;
                int c = x*h + y;
                while (component[c] != c) c = component[c];
                input[x][y].setBackground(getColor(c,input[x][y],input[x][y].getText()));
            }
        }
    }

    private void changeAllColors(){
        addAllColors();
    }

    private Color getColor(int c,Cell cell,String text){

        int count = (text != "") ? Integer.parseInt(text): 0;
        int antiCount = colors.containsKey(c) && colors.get(c).getText() != "" ? Integer.parseInt(colors.get(c).getText()) : 0;
        Color tempColor = getUniqueColor();
        boolean changeColor = false;

        //if colors !contains the group(c) or count of the cell group is bigger then the group that already exists then set the group to the
        //current cell.
        if (!colors.containsKey(c) || (count > antiCount)){
            //if the cell has a unique color -> dont change it / if not -> change it.
            changeColor = (ceckForUniqueColor(cell));
            colors.put(c,cell);
        }

        //if group doesnt exist or changeColor or color of the group is balck -> change the color of the group to tempColor
        if (!colors.containsKey(c) || changeColor || colors.get(c).getBackground() == Color.black){
            Cell c1 = colors.get(c);
            c1.setBackground(tempColor);
            colors.put(c,c1);
        }
        return colors.get(c).getBackground();
    }

    private Color getUniqueColor(){     //generates a unique color and returns the color
        Color tempColor;
        Cell tempCell;
        do {
            int c1 = ThreadLocalRandom.current().nextInt(0, 255 + 1);
            int c2 = ThreadLocalRandom.current().nextInt(0, 255 + 1);
            int c3 = ThreadLocalRandom.current().nextInt(0, 255 + 1);
            tempColor = new Color(c1, c2, c3);
            tempCell = new Cell("",tempColor);
        }while (!ceckForUniqueColor(tempCell) && tempColor == new Color(0,0,0));
        return tempColor;
    }

    private boolean ceckForUniqueColor(Cell cell){      //checks if color is unique (false -> is unique / true -> not unique)
        for (Map.Entry<Integer,Cell> x:colors.entrySet()) {
            if (x.getValue().getBackground() == cell.getBackground()){
                return true;
            }
        }
        return false;
    }

    public Cell[][] getCells(){
        return this.input;
    }

    private void addCount(){    //adds the amount of group members to each component

        HashMap<Color,Integer> hm1= new HashMap<>();                                    //Hashmap Color + amount the color is used

        for (Cell[] cells: input) {                                                   //Increases the int which is mapped to the color
            for (Cell cell: cells) {
                if (hm1.containsKey(cell.getBackground())){
                    hm1.put(cell.getBackground(),hm1.get(cell.getBackground())+1);
                }else {
                    hm1.put(cell.getBackground(),1);
                }
            }
        }
        for (Cell[] cells: input) {                                                 //Sets the int as Text for each field
            for (Cell cell: cells) {

                //sets the text-color to the contrast color of the background color
                cell.setForeground(getContrastColor(cell.getBackground()));
                if (cell.getBackground() != Color.white){
                    cell.setText(""+hm1.get(cell.getBackground()));
                }else {
                    cell.setText("");
                }
            }
        }
    }

    private static Color getContrastColor(Color color) {    // gets the contrast color
        double y = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000;
        return y >= 128 ? Color.black : Color.white;
    }
}
