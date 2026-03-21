package vue.controller;

import javax.swing.ImageIcon;
import java.awt.Point;

public class MoveAnimation {
    public final int toRow;
    public final int toCol;
    public final Point from;
    public final Point to;
    public final ImageIcon icon;
    public final long startMs;

    public MoveAnimation(int toRow, int toCol, Point from, Point to, ImageIcon icon) {
        this.toRow = toRow;
        this.toCol = toCol;
        this.from = from;
        this.to = to;
        this.icon = icon;
        this.startMs = System.currentTimeMillis();
    }
}

