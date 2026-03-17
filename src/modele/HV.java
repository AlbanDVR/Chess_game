package modele;

import java.util.ArrayList;

public class HV extends DCA{
    private int portee;

    public HV(Case currentCase, Plateau plateau) {
        super(currentCase, plateau);
        this.portee = 8; // tout
    }

    public HV(Case currentCase, Plateau plateau, int portee) {
        super(currentCase, plateau);
        this.portee = portee;
    }

    @Override
    public ArrayList<Case> getMesCasesAccessibles(int currentPlayer, Case currentCase) {
        ArrayList<Case> caseAccessible = new ArrayList<>();

        Plateau plateau = getPlateau();

        // Direction DROITE
        caseAccessible.addAll(explorerDirection(currentCase, Direction.DROITE, currentPlayer, plateau, portee));

        // Direction GAUCHE
        caseAccessible.addAll(explorerDirection(currentCase, Direction.GAUCHE, currentPlayer, plateau, portee));

        // Direction HAUT
        caseAccessible.addAll(explorerDirection(currentCase, Direction.HAUT, currentPlayer, plateau, portee));

        // Direction BAS
        caseAccessible.addAll(explorerDirection(currentCase, Direction.BAS, currentPlayer, plateau, portee));

        return caseAccessible;
    }

    private ArrayList<Case> explorerDirection(Case startCase, Direction direction, int currentPlayer, Plateau plateau, int porteeMax) {
        ArrayList<Case> cases = new ArrayList<>();
        Case currentCase = startCase;

        int pas = 0;
        while (pas < porteeMax) {
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
            else if (nextCase.getPiece().getColor() != currentPlayer) {
                cases.add(nextCase);
                break;
            }
            // Si c'est une pièce du même joueur, on arrête sans l'ajouter
            else {
                break;
            }

            pas++;
        }

        return cases;
    }
}
