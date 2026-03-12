package modele;

import java.util.ArrayList;

public class HV extends DCA{

    public HV(Case currentCase, Plateau plateau) {
        super(currentCase, plateau);
    }

    public HV(DCA base, Case currentCase, Plateau plateau) {
        super(base, currentCase, plateau);
    }

    @Override
    public ArrayList<Case> getMesCasesAccessibles(int currentPlayer, Case currentCase) {
        ArrayList<Case> caseAccessible = new ArrayList<>();

        Plateau plateau = getPlateau();

        while(plateau.getCaseAdj(currentCase, Direction.DROITE) != null || plateau.getCaseAdj(currentCase, Direction.DROITE).getPiece().getColor() == currentPlayer) {

            caseAccessible.add(plateau.getCaseAdj(currentCase, Direction.DROITE));

            currentCase = plateau.getCaseAdj(currentCase, Direction.DROITE);
        }


        return caseAccessible;
    }
}
