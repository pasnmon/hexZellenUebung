package com.hexField;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class Cell extends JButton {

    private int row;
    private int col;
    private Polygon hexagon = new Polygon();

    public Cell(String arg,int row,int col,Color backColor) {
        super(arg);
        this.setBackground(backColor);
        this.row = row;
        this.col = col;
    }
    public Cell(String arg,Color backColor) {
        super(arg);
        this.setBackground(backColor);
    }

    public Cell(String arg,int row,int col){
        this(arg,row,col,Color.white);
    }

    @Override
    public boolean contains(Point p)
    {
        return hexagon.contains(p);
    }

    @Override
    public boolean contains(int x, int y)
    {
        return hexagon.contains(x, y);
    }

    @Override
    public void setSize(Dimension d)
    {
        super.setSize(d);
        calculateCoords();
    }

    @Override
    public void setSize(int w, int h)
    {
        super.setSize(w, h);
        calculateCoords();
    }

    @Override
    public void setBounds(int x, int y, int width, int height)
    {
        super.setBounds(x, y, width, height);
        calculateCoords();
    }

    @Override
    public void setBounds(Rectangle r)
    {
        super.setBounds(r);
        calculateCoords();
    }

    @Override
    protected void processMouseEvent(MouseEvent e)
    {
        if ( contains(e.getPoint()) )
            super.processMouseEvent(e);
    }

    private void calculateCoords()  //creates a polygon in form of an hexagon
    {
        Polygon hex = new Polygon();
        int w = getWidth() - 1;
        int h = getHeight() - 1;
        int ratio = (int) (w * .25);

        hex.addPoint(ratio, 0);
        hex.addPoint(w-ratio, 0);
        hex.addPoint(w, h/2);
        hex.addPoint(w-ratio, h);
        hex.addPoint(ratio, h);
        hex.addPoint(0, h/2);

        hexagon =  hex;
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        g.setColor(getBackground());

        g.fillPolygon(hexagon);

        g.setColor(getForeground());
        g.drawPolygon(hexagon);

        FontMetrics fm = getFontMetrics(getFont());
        Rectangle viewR = getBounds();
        Rectangle iconR = new Rectangle();
        Rectangle textR = new Rectangle();


        SwingUtilities.layoutCompoundLabel(this, fm, getText(), null,
                SwingUtilities.CENTER, SwingUtilities.CENTER, SwingUtilities.BOTTOM, SwingUtilities.CENTER,
                viewR, iconR, textR, 0);

        Point loc = getLocation();
        g.drawString(getText(), textR.x-loc.x, textR.y-loc.y+fm.getAscent());
    }

    @Override
    protected void paintBorder(Graphics g)
    {
        // do not paint a border
    }

    @Override
    public String toString() {      //for testing w/o gui
        return String.format("Farbe: %-50s",this.getBackground());
    }


    public boolean isFilled(){
        return (this.getBackground() == Color.white ? false : true);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}