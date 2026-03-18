package modele.pieces;

import modele.Case;
import modele.mouvement.Diag;
import modele.mouvement.HV;
import modele.Plateau;

/**
 * Classe représentant un pion
 */
public class Pawn extends Piece {

    public Pawn(int color) {
        super(color);
    }

    @Override
    public void chargerDCA(Case c, Plateau p) {
        // Calcul de la porter du spawn
        int y = p.getCoord(c)[1];
        int portee = 1;
        if ((this.getColor() == 0 && y == 6) || (this.getColor() == 1 && y == 1)) {
            portee = 2;
        }

        this.setDCA(new HV(new Diag(c, p, 1, false, true, false), c, p, portee, true, false, false));
    }
}
