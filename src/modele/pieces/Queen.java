package modele.pieces;

import modele.Case;
import modele.Jeu;
import modele.Plateau;
import modele.mouvement.Diag;
import modele.mouvement.HV;

import java.util.ArrayList;

/**
 * Classe représentant une reine
 */
public class Queen extends Piece {

    public Queen(int color, Plateau p) {
        super(color, new HV(new Diag(p), p));
    }

    @Override
    public ArrayList<Case> chargerCasesAccessible(Jeu jeu, Plateau p) {
        return dca.getCasesAccessibles(jeu, p.getCase(this));
    }
}
