package modele.pieces;

import modele.Case;
import modele.mouvement.Diag;
import modele.mouvement.HV;
import modele.Plateau;

/**
 * Classe représentant un roi
 */
public class King extends Piece {

    public King(int color) {
        super(color);
    }

    @Override
    public void chargerDCA(Case c, Plateau p) {
        this.setDCA(new HV(new Diag(c, p, 1), c, p, 1));
    }
}
