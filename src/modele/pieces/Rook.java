package modele.pieces;

import modele.Case;
import modele.mouvement.HV;
import modele.Plateau;

/**
 * Classe représentant une tour
 */
public class Rook extends Piece {

    public Rook(int color) {
        super(color);
    }

    @Override
    public void chargerDCA(Case c, Plateau p) {
        this.setDCA(new HV(c, p));
    }
}
