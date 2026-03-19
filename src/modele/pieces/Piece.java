package modele.pieces;

import modele.Case;
import modele.Jeu;
import modele.mouvement.DCA;
import modele.Plateau;

import java.util.ArrayList;

public abstract class Piece {
    private final String name;
    private final int color; // 0 : blanc | 1 : noir
    protected DCA dca;

    public Piece(int color, DCA dca) {
        this.color = color;
        this.name = this.getClass().getSimpleName();
        this.dca = dca;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public abstract ArrayList<Case> chargerCasesAccessible(Jeu jeu, Plateau p);
}
