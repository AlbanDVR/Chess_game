package modele.mouvement;

import modele.Case;
import modele.Joueur;
import modele.Plateau;

import java.util.ArrayList;

public class Diag extends DCA{

    public Diag(Plateau plateau) {
        super(plateau);
    }

    @Override
    public ArrayList<Case> getMesCasesAccessibles(Joueur currentPlayer, Case currentCase) {
        ArrayList<Case> caseAccessible = new ArrayList<>();

        Plateau plateau = getPlateau();

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
