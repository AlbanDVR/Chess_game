package modele.pieces;

import modele.Case;
import modele.Jeu;
import modele.Plateau;
import modele.mouvement.Diag;

import java.util.ArrayList;

/**
 * Classe représentant un fou
 */
public class Bishop extends Piece {

    public Bishop(int color, Plateau p) {
        super(color, new Diag(p));
    }

    @Override
    public Piece copy(Plateau copyPlateau) {
        return new Bishop(this.getColor(), copyPlateau);
    }

    @Override
    public ArrayList<Case> chargerCasesAccessible(Jeu jeu, Plateau p) {
        return dca.getCasesAccessibles(jeu, p.getCase(this));
    }
}
