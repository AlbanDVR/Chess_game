package modele.pieces;

import modele.Case;
import modele.Jeu;
import modele.Plateau;
import modele.mouvement.HV;

import java.util.ArrayList;

/**
 * Classe représentant une tour
 */
public class Rook extends Piece {

    public Rook(int color, Plateau p) {
        super(color, new HV(p));
    }

    @Override
    public Piece copy(Plateau copyPlateau) {
        return copyMovedState(new Rook(this.getColor(), copyPlateau));
    }

    @Override
    public ArrayList<Case> chargerCasesAccessible(Jeu jeu, Plateau p) {
        return dca.getCasesAccessibles(jeu, p.getCase(this));
    }
}
