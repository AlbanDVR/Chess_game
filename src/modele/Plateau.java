package modele;

public class Plateau {
    private final Case[][] positions;

    Plateau() {
        positions = new Case[][]{{new Case(new Rook(1)), new Case(new Knight(1)), new Case(new Bishop(1)), new Case(new King(1)), new Case(new Queen(1)), new Case(new Bishop(1)), new Case(new Knight(1)), new Case(new Rook(1))},
                {new Case(new Pawn(1)), new Case(new Pawn(1)), new Case(new Pawn(1)), new Case(new Pawn(1)), new Case(new Pawn(1)), new Case(new Pawn(1)), new Case(new Pawn(1)), new Case(new Pawn(1))},
                {new Case(null), new Case(null), new Case(null), new Case(null), new Case(null), new Case(null), new Case(null), new Case(null)},
                {new Case(null), new Case(null), new Case(null), new Case(null), new Case(null), new Case(null), new Case(null), new Case(null)},
                {new Case(null), new Case(null), new Case(null), new Case(null), new Case(null), new Case(null), new Case(null), new Case(null)},
                {new Case(null), new Case(null), new Case(null), new Case(null), new Case(null), new Case(null), new Case(null), new Case(null)},
                {new Case(new Pawn(0)), new Case(new Pawn(0)), new Case(new Pawn(0)), new Case(new Pawn(0)), new Case(new Pawn(0)), new Case(new Pawn(0)), new Case(new Pawn(0)), new Case(new Pawn(0))},
                {new Case(new Rook(0)), new Case(new Knight(0)), new Case(new Bishop(0)), new Case(new King(0)), new Case(new Queen(0)), new Case(new Bishop(0)), new Case(new Knight(0)), new Case(new Rook(0))}};
    }

    public Case[][] getPositions() {
        return positions;
    }

    public Case getCase(Piece piece) {
        for (Case[] array : positions) {
            for (Case c : array) {
                if (c.getPiece().equals(piece)) {
                    return c;
                }
            }
        }
        return null;
    }

    public Case getCase(int x, int y) {
        return this.positions[x][y];
    }

    public Case getCaseAdj(Case c, Direction dir) {
        // On cherche la case de départ
        int foundI = -1, foundJ = -1;
        for (int i = 0; i < positions.length; i++) {
            for (int j = 0; j < positions[i].length; j++) {
                if (positions[i][j] == c) {
                    foundI = i;
                    foundJ = j;
                    break;
                }
            }
        }

        return positions[foundI + dir.getDx()][foundJ + dir.getDy()];
    }
}
