package modele.pieces;

import modele.Case;
import modele.Jeu;
import modele.Plateau;
import modele.mouvement.MouvKing;

import java.util.ArrayList;

/**
 * Classe représentant un roi
 */
public class King extends Piece {

    public King(int color, Plateau p) {
        super(color, new MouvKing(p));
    }

    @Override
    public ArrayList<Case> chargerCasesAccessible(Jeu jeu, Plateau p) {
        return dca.getCasesAccessibles(jeu, p.getCase(this));
    }
}
