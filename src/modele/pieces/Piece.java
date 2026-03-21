package modele.pieces;

import modele.Case;
import modele.Coup;
import modele.Jeu;
import modele.mouvement.DCA;
import modele.Plateau;

import java.util.ArrayList;

public abstract class Piece {
    private final String name;
    private final int color; // 0 : blanc | 1 : noir
    private boolean moved;
    protected DCA dca;

    public Piece(int color, DCA dca) {
        this.color = color;
        this.name = this.getClass().getSimpleName();
        this.dca = dca;
    }

    public abstract Piece copy(Plateau copyPlateau);

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public boolean hasMoved() {
        return moved;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    public abstract ArrayList<Case> chargerCasesAccessible(Jeu jeu, Plateau p);

    public ArrayList<Case> chargerCasesAttaquees(Jeu jeu, Plateau p) {
        return chargerCasesAccessible(jeu, p);
    }

    /**
     * Default capture behavior: capture the piece on arrival square.
     */
    public Case resolveCapturedCase(Plateau plateau, Coup move) {
        return plateau.getCase(move.getArr()[0], move.getArr()[1]);
    }

    public void onMoveApplied(Jeu jeu, Plateau plateau, Coup move) {
        setMoved(true);
    }

    public Piece resolvePostMovePiece(Jeu jeu, Plateau plateau, Coup move) {
        return this;
    }

    protected <T extends Piece> T copyMovedState(T copy) {
        copy.setMoved(this.moved);
        return copy;
    }
}
