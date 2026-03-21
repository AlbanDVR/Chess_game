package modele.mouvement;

import modele.Case;
import modele.Joueur;
import modele.Plateau;

import java.util.ArrayList;

public class MouvPawn extends DCA{

    public MouvPawn(Plateau plateau) {
        super(plateau);
    }

    @Override
    public ArrayList<Case> getMesCasesAccessibles(Joueur currentPlayer, Case currentCase) {
        ArrayList<Case> caseAccessible = new ArrayList<>();

        Plateau plateau = getPlateau();

        Direction dirAvant = (currentCase.getPiece().getColor() == 0) ? Direction.HAUT : Direction.BAS;
        Direction dirDiagG = (currentCase.getPiece().getColor() == 0) ? Direction.HG : Direction.BG;
        Direction dirDiagD = (currentCase.getPiece().getColor() == 0) ? Direction.HD : Direction.BD;

        caseAccessible.addAll(explorerDirection(currentCase, dirAvant, currentPlayer, plateau));
        caseAccessible.addAll(explorerDirection(currentCase, dirDiagG, currentPlayer, plateau));
        caseAccessible.addAll(explorerDirection(currentCase, dirDiagD, currentPlayer, plateau));

        return caseAccessible;
    }

    private ArrayList<Case> explorerDirection(Case startCase, Direction direction, Joueur currentPlayer, Plateau plateau) {
        ArrayList<Case> cases = new ArrayList<>();
        Case currentCase = startCase;
        int porteeMax = 1;

        Direction dirAvant = (currentCase.getPiece().getColor() == 0) ? Direction.HAUT : Direction.BAS;

        int y = plateau.getCoord(startCase)[0];
        if ((startCase.getPiece().getColor() == 0 && y == 6) || (startCase.getPiece().getColor() == 1 && y == 1)) {
                porteeMax = 2;
        }

        int pas = 0;
        while (pas < porteeMax) {
            Case nextCase = plateau.getCaseAdj(currentCase, direction);

            // Si pas de case adjacente, on arrête
            if (nextCase == null) {
                break;
            }

            if (direction == dirAvant) {
                // Si la case est vide, on l'ajoute et on continue
                if (nextCase.getPiece() == null) {
                    cases.add(nextCase);
                    currentCase = nextCase;
                }
                else{
                    break;
                }
            }
            else {
                if (nextCase.getPiece() != null && nextCase.getPiece().getColor() != currentPlayer.getId()) {
                    cases.add(nextCase);
                }
                break;
            }

            pas++;
        }

        return cases;
    }
}
