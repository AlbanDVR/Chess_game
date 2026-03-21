package modele.mouvement;

import modele.Case;
import modele.Joueur;
import modele.Plateau;

import java.util.ArrayList;

public class SautCheval extends DCA {

    public SautCheval(Plateau plateau) {
        super(plateau);
    }

    @Override
    public ArrayList<Case> getMesCasesAccessibles(Joueur currentPlayer, Case currentCase) {
        ArrayList<Case> caseAccessible = new ArrayList<>();

        Plateau plateau = getPlateau();

        Direction[] mouvCheval = {
                Direction.C_HAUT_GAUCHE, Direction.C_HAUT_DROITE,
                Direction.C_BAS_GAUCHE, Direction.C_BAS_DROITE,
                Direction.C_GAUCHE_HAUT, Direction.C_GAUCHE_BAS,
                Direction.C_DROITE_HAUT, Direction.C_DROITE_BAS
        };

        for (Direction dir : mouvCheval) {
            Case target = plateau.getCaseAdj(currentCase, dir);

            if (target != null) {
                if (target.getPiece() == null || target.getPiece().getColor() != currentPlayer.getId()) {
                    caseAccessible.add(target);
                }
            }
        }

        return caseAccessible;
    }
}