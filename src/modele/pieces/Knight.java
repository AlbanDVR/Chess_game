package modele.pieces;

import modele.Case;
import modele.Plateau;
import modele.mouvement.SautCheval;

/**
 * Classe représentant un chevalier
 */
public class Knight extends Piece {

    public Knight(int  color) {
        super(color);
    }

    @Override
    public void chargerDCA(Case c, Plateau p) {
        this.setDCA(new SautCheval(c, p));
    }
}
