package modele.mouvement;

import modele.Case;
import modele.Joueur;
import modele.Plateau;

import java.util.ArrayList;

public class MouvKing extends DCA{

    public MouvKing(Plateau plateau) {
        super(plateau);
    }

    @Override
    public ArrayList<Case> getMesCasesAccessibles(Joueur currentPlayer, Case currentCase) {
        ArrayList<Case> caseAccessible = new ArrayList<>();

        Plateau plateau = getPlateau();

        // Direction Droite
        caseAccessible.addAll(explorerDirection(currentCase, Direction.DROITE, currentPlayer, plateau));
        // Direction Gauche
        caseAccessible.addAll(explorerDirection(currentCase, Direction.GAUCHE, currentPlayer, plateau));
        // Direction Haut
        caseAccessible.addAll(explorerDirection(currentCase, Direction.HAUT, currentPlayer, plateau));
        // Direction Bas
        caseAccessible.addAll(explorerDirection(currentCase, Direction.BAS, currentPlayer, plateau));
        // Direction Haut Gauche
        caseAccessible.addAll(explorerDirection(currentCase, Direction.HG, currentPlayer, plateau));
        // Direction Haut Droite
        caseAccessible.addAll(explorerDirection(currentCase, Direction.HD, currentPlayer, plateau));
        // Direction Bas Gauche
        caseAccessible.addAll(explorerDirection(currentCase, Direction.BG, currentPlayer, plateau));
        // Direction Bas Droite
        caseAccessible.addAll(explorerDirection(currentCase, Direction.BD, currentPlayer, plateau));

        return caseAccessible;
    }

    private ArrayList<Case> explorerDirection(Case startCase, Direction direction, Joueur currentPlayer, Plateau plateau) {
        ArrayList<Case> cases = new ArrayList<>();

        Case nextCase = plateau.getCaseAdj(startCase, direction);

        if (nextCase != null) {
            // Si la case est vide, on l'ajoute
            if (nextCase.getPiece() == null) {
                cases.add(nextCase);
            }

            // Si c'est une pièce adverse, on peut la prendre
            else if (nextCase.getPiece().getColor() != currentPlayer.getId()) {
                cases.add(nextCase);
            }
        }

        return cases;
    }
}
