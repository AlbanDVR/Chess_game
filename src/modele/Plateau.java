package modele;

public class Plateau {
    private Case[][] positions;

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

    public Case getCaseAdj(Case c, Direction dir) {
        return null;
    }
}
