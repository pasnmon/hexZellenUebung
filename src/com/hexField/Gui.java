package com.hexField;

import javax.swing.*;
import java.awt.*;

public class Gui extends JFrame {

    public Gui(int w, int h){

        this.setTitle("Hex Cells CCL");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel p1 = new JPanel();
        p1.setLayout(new HexLayout(h,w));
        CellField cf1 = new CellField(w,h);

        for (Cell[] cArr : cf1.getCells()) {
            for (Cell c: cArr) {
                c.addActionListener(e -> {
                        Cell selected = (Cell) e.getSource();
                        Color color = (selected.getBackground() == Color.white) ? Color.black : Color.white;
                        cf1.editField(selected.getRow(),selected.getCol(),color);

                });
                p1.add(c);
            }
        }

        this.add(p1);
        this.setSize(this.getPreferredSize());
        this.setResizable(true);
        this.setLocation(50,50);
        this.setVisible(true);
    }

}
