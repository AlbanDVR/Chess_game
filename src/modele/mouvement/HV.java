package modele.mouvement;

import modele.Case;
import modele.Joueur;
import modele.Plateau;

import java.util.ArrayList;

public class HV extends DCA{

    public HV(Plateau plateau) {
        super(plateau);
    }

    public HV(DCA base, Plateau plateau) {
        super(base, plateau);
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

        return caseAccessible;
    }

    private ArrayList<Case> explorerDirection(Case startCase, Direction direction, Joueur currentPlayer, Plateau plateau) {
        ArrayList<Case> cases = new ArrayList<>();
        Case currentCase = startCase;

        while (true) {
            Case nextCase = plateau.getCaseAdj(currentCase, direction);

            // Si pas de case adjacente, on arrête
            if (nextCase == null) {
                break;
            }

            // Si la case est vide, on l'ajoute et on continue
            if (nextCase.getPiece() == null) {
                cases.add(nextCase);
                currentCase = nextCase;
            }

            // Si c'est une pièce de l'adversaire, on l'ajoute et on arrête
            else if (nextCase.getPiece().getColor() != currentPlayer.getId()) {
                cases.add(nextCase);
                break;
            }

            // Si c'est une pièce du même joueur, on arrête sans l'ajouter
            else {
                break;
            }

        }

        return cases;
    }
}
