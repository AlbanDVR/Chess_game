package modele.pieces;

import modele.Case;
import modele.Jeu;
import modele.Plateau;
import modele.mouvement.MouvPawn;

import java.util.ArrayList;

/**
 * Classe représentant un pion
 */
public class Pawn extends Piece {

    public Pawn(int color, Plateau p) {
        super(color, new MouvPawn(p));
    }

    @Override
    public Piece copy(Plateau copyPlateau) {
        return new Pawn(this.getColor(), copyPlateau);
    }

    @Override
    public ArrayList<Case> chargerCasesAccessible(Jeu jeu, Plateau p) {
        return dca.getCasesAccessibles(jeu, p.getCase(this));
    }
}
