package modele.pieces;

import modele.Case;
import modele.Jeu;
import modele.Plateau;
import modele.mouvement.SautCheval;

import java.util.ArrayList;

/**
 * Classe représentant un chevalier
 */
public class Knight extends Piece {

    public Knight(int color, Plateau p) {
        super(color, new SautCheval(p));
    }

    @Override
    public Piece copy(Plateau copyPlateau) {
        return new Knight(this.getColor(), copyPlateau);
    }

    @Override
    public ArrayList<Case> chargerCasesAccessible(Jeu jeu, Plateau p) {
        return dca.getCasesAccessibles(jeu, p.getCase(this));
    }
}
