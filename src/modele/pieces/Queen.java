package modele.pieces;

import modele.Case;
import modele.mouvement.Diag;
import modele.mouvement.HV;
import modele.Plateau;

/**
 * Classe représentant une reine
 */
public class Queen extends Piece {

    public Queen(int color) {
        super(color);
    }

    @Override
    public void chargerDCA(Case c, Plateau p) {
        this.setDCA(new HV(new Diag(c, p), c, p));
    }
}
