package modele;

import java.util.ArrayList;

public class HV extends DCA{
    private int portee;
    private boolean peutAvancer;
    private boolean peutCapturer;
    private boolean fullRange;

    public HV(Case currentCase, Plateau plateau) {
        super(currentCase, plateau);
        this.portee = 8; // tout
        this.peutAvancer = true;
        this.peutCapturer = true;
        this.fullRange = true;
    }

    public HV(DCA base, Case currentCase, Plateau plateau) {
        super(base, currentCase, plateau);
        this.portee = 8; // tout
        this.peutAvancer = true;
        this.peutCapturer = true;
        this.fullRange = true;
    }

    public HV(DCA base, Case currentCase, Plateau plateau, int portee) {
        super(base, currentCase, plateau);
        this.portee = portee;
        this.peutAvancer = true;
        this.peutCapturer = true;
        this.fullRange = true;
    }

    // Pion
    public HV(DCA base, Case currentCase, Plateau plateau, int portee, boolean peutAvancer, boolean peutCapturer, boolean fullRange) {
        super(base, currentCase, plateau);
        this.portee = portee;
        this.peutAvancer = peutAvancer;
        this.peutCapturer = peutCapturer;
        this.fullRange = fullRange;
    }

    @Override
    public ArrayList<Case> getMesCasesAccessibles(int currentPlayer, Case currentCase) {
        ArrayList<Case> caseAccessible = new ArrayList<>();

        Plateau plateau = getPlateau();

        if (fullRange) {
            // Direction Droite
            caseAccessible.addAll(explorerDirection(currentCase, Direction.DROITE, currentPlayer, plateau, portee, peutAvancer, peutCapturer));
            // Direction Gauche
            caseAccessible.addAll(explorerDirection(currentCase, Direction.GAUCHE, currentPlayer, plateau, portee, peutAvancer, peutCapturer));
            // Direction Haut
            caseAccessible.addAll(explorerDirection(currentCase, Direction.HAUT, currentPlayer, plateau, portee, peutAvancer, peutCapturer));
            // Direction Bas
            caseAccessible.addAll(explorerDirection(currentCase, Direction.BAS, currentPlayer, plateau, portee, peutAvancer, peutCapturer));
        }
        else { // Pov le pion
            Direction directionAvant = (currentCase.getPiece().getColor() == 0) ? Direction.HAUT : Direction.BAS; // en fonction du joueur
            caseAccessible.addAll(explorerDirection(currentCase, directionAvant, currentPlayer, plateau, portee, peutAvancer, peutCapturer));
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
