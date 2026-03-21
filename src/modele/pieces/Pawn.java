package modele.pieces;

import modele.Case;
import modele.Coup;
import modele.Jeu;
import modele.Plateau;
import modele.mouvement.MouvPawn;

import java.util.ArrayList;

/**
 * Classe représentant un pion
 */
public class Pawn extends Piece {

    public Pawn(int color, Plateau p) {
        super(color, new MouvPawn(p));
    }

    @Override
    public Piece copy(Plateau copyPlateau) {
        return copyMovedState(new Pawn(this.getColor(), copyPlateau));
    }

    @Override
    public ArrayList<Case> chargerCasesAccessible(Jeu jeu, Plateau p) {
        Case currentCase = p.getCase(this);
        ArrayList<Case> moves = dca.getCasesAccessibles(jeu, currentCase);
        addEnPassantCases(jeu, p, currentCase, moves);
        return moves;
    }

    @Override
    public Case resolveCapturedCase(Plateau plateau, Coup move) {
        Case arrival = plateau.getCase(move.getArr()[0], move.getArr()[1]);
        if (arrival.getPiece() != null) {
            return arrival;
        }

        int depRow = move.getDep()[0];
        int depCol = move.getDep()[1];
        int arrCol = move.getArr()[1];

        if (Math.abs(arrCol - depCol) != 1) {
            return arrival;
        }

        return plateau.getCase(depRow, arrCol);
    }

    @Override
    public ArrayList<Case> chargerCasesAttaquees(Jeu jeu, Plateau p) {
        ArrayList<Case> attacks = new ArrayList<>();
        Case currentCase = p.getCase(this);
        if (currentCase == null) {
            return attacks;
        }

        int[] coord = p.getCoord(currentCase);
        int row = coord[0];
        int col = coord[1];
        int direction = (getColor() == 0) ? -1 : 1;
        int targetRow = row + direction;
        if (targetRow < 0 || targetRow > 7) {
            return attacks;
        }

        if (col - 1 >= 0) {
            attacks.add(p.getCase(targetRow, col - 1));
        }
        if (col + 1 <= 7) {
            attacks.add(p.getCase(targetRow, col + 1));
        }
        return attacks;
    }

    @Override
    public Piece resolvePostMovePiece(Jeu jeu, Plateau plateau, Coup move) {
        int targetRow = move.getArr()[0];
        if ((getColor() == 0 && targetRow != 0) || (getColor() == 1 && targetRow != 7)) {
            return this;
        }

        return jeu.createPromotionPiece(move.getPromotionPiece(), getColor());
    }

    private void addEnPassantCases(Jeu jeu, Plateau plateau, Case currentCase, ArrayList<Case> moves) {
        Coup lastMove = jeu.getLastPlayedMove();
        if (lastMove == null) {
            return;
        }

        int[] coord = plateau.getCoord(currentCase);
        int row = coord[0];
        int col = coord[1];

        int expectedRow = (getColor() == 0) ? 3 : 4;
        if (row != expectedRow) {
            return;
        }

        int direction = (getColor() == 0) ? -1 : 1;
        tryAddEnPassantTarget(plateau, row, col - 1, direction, lastMove, moves);
        tryAddEnPassantTarget(plateau, row, col + 1, direction, lastMove, moves);
    }

    private void tryAddEnPassantTarget(Plateau plateau, int row, int adjacentCol, int direction, Coup lastMove, ArrayList<Case> moves) {
        if (adjacentCol < 0 || adjacentCol > 7) {
            return;
        }

        Case adjacent = plateau.getCase(row, adjacentCol);
        Piece adjacentPiece = adjacent.getPiece();
        if (!(adjacentPiece instanceof Pawn) || adjacentPiece.getColor() == getColor()) {
            return;
        }

        if (!isLastMoveMatchingAdjacentPawn(lastMove, row, adjacentCol)) {
            return;
        }

        int targetRow = row + direction;
        if (targetRow < 0 || targetRow > 7) {
            return;
        }

        Case enPassantTarget = plateau.getCase(targetRow, adjacentCol);
        if (enPassantTarget.getPiece() == null) {
            moves.add(enPassantTarget);
        }
    }

    private boolean isLastMoveMatchingAdjacentPawn(Coup lastMove, int row, int col) {
        int fromRow = lastMove.getDep()[0];
        int fromCol = lastMove.getDep()[1];
        int toRow = lastMove.getArr()[0];
        int toCol = lastMove.getArr()[1];

        return fromCol == toCol
                && Math.abs(toRow - fromRow) == 2
                && toRow == row
                && toCol == col;
    }
}
