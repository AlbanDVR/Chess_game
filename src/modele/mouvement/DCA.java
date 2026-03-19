package modele.mouvement;

import modele.Case;
import modele.Jeu;
import modele.Plateau;

import java.util.ArrayList;

public abstract class DCA {

    private final DCA base;
    private final Case currentCase;
    private final Plateau plateau;


    public DCA(Case currentCase, Plateau plateau) {
        this.base = null;
        this.currentCase = currentCase;
        this.plateau = plateau;
    }

    public DCA(DCA base, Case currentCase, Plateau plateau) {
        this.base = base;
        this.currentCase = currentCase;
        this.plateau = plateau;
    }


    public ArrayList<Case> getCasesAccessibles(Jeu jeu){
        if(base == null){
            return getMesCasesAccessibles(jeu.getCurrentPlayer(), currentCase);
        }
        else{
            ArrayList<Case> baseCase = base.getCasesAccessibles(jeu);
            baseCase.addAll(getMesCasesAccessibles(jeu.getCurrentPlayer(), currentCase));
            return baseCase;
        }
    }

    public abstract ArrayList<Case> getMesCasesAccessibles(int currentPlayer, Case currentCase);

    public Plateau getPlateau() {
        return plateau;
    }
}
