package modele.mouvement;

import modele.Case;
import modele.Jeu;
import modele.Joueur;
import modele.Plateau;
import modele.pieces.Piece;

import java.util.ArrayList;

public abstract class DCA {

    private DCA base;
    private Plateau plateau;


    public DCA(Plateau plateau) {
        this.base = null;
        this.plateau = plateau;
    }

    public DCA(DCA base, Plateau plateau) {
        this.base = base;
        this.plateau = plateau;
    }


    public ArrayList<Case> getCasesAccessibles(Jeu jeu, Case currentCase){
        Joueur proprio = (currentCase.getPiece().getColor() == 0) ? jeu.liste[0] : jeu.liste[1];

        if(base == null){
            return getMesCasesAccessibles(proprio, currentCase);
        } else {
            ArrayList<Case> baseCase = base.getCasesAccessibles(jeu, currentCase);
            baseCase.addAll(getMesCasesAccessibles(proprio, currentCase));
            return baseCase;
        }
    }

    public abstract ArrayList<Case> getMesCasesAccessibles(Joueur currentPlayer, Case currentCase);

    public Plateau getPlateau() {
        return plateau;
    }
}
