package modele;

import java.util.ArrayList;

public abstract class DCA {

    private DCA base;
    private Case currentCase;


    public DCA(Case currentCase) {
        this.base = null;
        this.currentCase = currentCase;
    }

    public DCA(DCA base, Case currentCase){
        this.base = base;
        this.currentCase = currentCase;
    }

    public ArrayList<Case> getCasesAccessibles(Jeu jeu){
        if(base == null){
            return getMesCasesAccessibles(jeu, jeu.getCurrentPlayer(), currentCase);
        }
        else{
            return base.getCasesAccessibles(jeu).addAll(getMesCasesAccessibles(jeu, jeu.getCurrentPlayer(), currentCase));
        }
    }

    public abstract ArrayList<Case> getMesCasesAccessibles(Jeu jeu, int currentPlayer, Case currentCase);
}
