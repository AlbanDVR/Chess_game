package modele.mouvement;

import modele.Case;
import modele.Jeu;
import modele.Plateau;

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
        if(base == null){
            return getMesCasesAccessibles(jeu.getCurrentPlayer(), currentCase);
        }
        else{
            ArrayList<Case> baseCase = base.getCasesAccessibles(jeu,  currentCase);
            baseCase.addAll(getMesCasesAccessibles(jeu.getCurrentPlayer(), currentCase));
            return baseCase;
        }
    }

    public abstract ArrayList<Case> getMesCasesAccessibles(int currentPlayer, Case currentCase);

    public Plateau getPlateau() {
        return plateau;
    }
}
