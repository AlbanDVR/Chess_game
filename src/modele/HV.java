package modele;

import java.util.ArrayList;

public class HV extends DCA{


    public HV(Case currentCase) {
        super(currentCase);
    }

    public HV(DCA base, Case currentCase) {
        super(base, currentCase);
    }

    @Override
    public ArrayList<Case> getMesCasesAccessibles(Jeu jeu, int currentPlayer, Case currentCase) {
        Case[][] plateau = jeu.getPositions();

        ArrayList<Case> caseAccessible = new ArrayList<>();


        return caseAccessible;
    }
}
