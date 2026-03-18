package modele.mouvement;

import modele.Case;
import modele.Plateau;

import java.util.ArrayList;

public class Diag extends DCA{
    private int portee;
    private boolean peutAvancer;
    private boolean peutCapturer;
    private boolean fullRange;

    public Diag(Case currentCase, Plateau plateau) {
        super(currentCase, plateau);
        this.portee = 8; // tout
        this.peutAvancer = true;
        this.peutCapturer = true;
        this.fullRange = true;
    }

    public Diag(Case currentCase, Plateau plateau, int portee) {
        super(currentCase, plateau);
        this.portee = portee;
        this.peutAvancer = true;
        this.peutCapturer = true;
        this.fullRange = true;
    }

    // pion
    public Diag(Case currentCase, Plateau plateau, int portee, boolean peutAvancer, boolean peutCapturer, boolean fullRange) {
        super(currentCase, plateau);
        this.portee = portee;
        this.peutAvancer = peutAvancer;
        this.peutCapturer = peutCapturer;
        this.fullRange = fullRange;
    }

    @Override
    public ArrayList<Case> getMesCasesAccessibles(int currentPlayer, Case currentCase) {
        ArrayList<Case> caseAccessible = new ArrayList<>();

        Plateau plateau = getPlateau();

        if(fullRange) {
            // Direction Haut Gauche
            caseAccessible.addAll(explorerDirection(currentCase, Direction.HG, currentPlayer, plateau, portee, peutAvancer, peutCapturer));
            // Direction Haut Droite
            caseAccessible.addAll(explorerDirection(currentCase, Direction.HD, currentPlayer, plateau, portee, peutAvancer, peutCapturer));
            // Direction Bas Gauche
            caseAccessible.addAll(explorerDirection(currentCase, Direction.BG, currentPlayer, plateau, portee, peutAvancer, peutCapturer));
            // Direction Bas Droite
            caseAccessible.addAll(explorerDirection(currentCase, Direction.BD, currentPlayer, plateau, portee, peutAvancer, peutCapturer));
        }
        else {
            if(currentPlayer == 0){
                Direction dir1 = Direction.HG;
                Direction dir2 = Direction.HD;
                caseAccessible.addAll(explorerDirection(currentCase, dir1, currentPlayer, plateau, portee, peutAvancer, peutCapturer));
                caseAccessible.addAll(explorerDirection(currentCase, dir2, currentPlayer, plateau, portee, peutAvancer, peutCapturer));
            }
            else {
                Direction dir1 = Direction.BG;
                Direction dir2 = Direction.BD;
                caseAccessible.addAll(explorerDirection(currentCase, dir1, currentPlayer, plateau, portee, peutAvancer, peutCapturer));
                caseAccessible.addAll(explorerDirection(currentCase, dir2, currentPlayer, plateau, portee, peutAvancer, peutCapturer));
            }
        }
        return caseAccessible;
    }

    private ArrayList<Case> explorerDirection(Case startCase, Direction direction, int currentPlayer, Plateau plateau, int porteeMax, boolean peutAvancer, boolean peutCapturer) {
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
                if(peutAvancer) {
                    cases.add(nextCase);
                }
                currentCase = nextCase;
            }

            // Si c'est une pièce de l'adversaire, on l'ajoute et on arrête
            else if (nextCase.getPiece().getColor() != currentPlayer && peutCapturer) {
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
