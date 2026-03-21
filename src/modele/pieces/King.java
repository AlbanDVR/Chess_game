package modele.pieces;

import modele.Case;
import modele.Coup;
import modele.Jeu;
import modele.Plateau;
import modele.mouvement.MouvKing;

import java.util.ArrayList;

/**
 * Classe représentant un roi
 */
public class King extends Piece {

    public King(int color, Plateau p) {
        super(color, new MouvKing(p));
    }

    @Override
    public Piece copy(Plateau copyPlateau) {
        return copyMovedState(new King(this.getColor(), copyPlateau));
    }

    @Override
    public ArrayList<Case> chargerCasesAccessible(Jeu jeu, Plateau p) {
        Case currentCase = p.getCase(this);
        ArrayList<Case> moves = dca.getCasesAccessibles(jeu, currentCase);

        int[] coord = p.getCoord(currentCase);
        int row = coord[0];
        if (jeu.canCastle(getColor(), true)) {
            moves.add(p.getCase(row, 6));
        }
        if (jeu.canCastle(getColor(), false)) {
            moves.add(p.getCase(row, 2));
        }

        return moves;
    }

    @Override
    public ArrayList<Case> chargerCasesAttaquees(Jeu jeu, Plateau p) {
        return dca.getCasesAccessibles(jeu, p.getCase(this));
    }

    @Override
    public void onMoveApplied(Jeu jeu, Plateau plateau, Coup move) {
        super.onMoveApplied(jeu, plateau, move);

        int depCol = move.getDep()[1];
        int arrCol = move.getArr()[1];
        if (Math.abs(arrCol - depCol) != 2) {
            return;
        }

        int row = move.getArr()[0];
        boolean kingSide = arrCol > depCol;
        int rookFromCol = kingSide ? 7 : 0;
        int rookToCol = kingSide ? 5 : 3;

        Case rookFrom = plateau.getCase(row, rookFromCol);
        Case rookTo = plateau.getCase(row, rookToCol);
        Piece rookPiece = rookFrom.getPiece();
        if (rookPiece instanceof Rook) {
            Rook rook = (Rook) rookPiece;
            rookTo.setPiece(rook);
            rookFrom.setPiece(null);
            rook.setMoved(true);
        }
    }
}
