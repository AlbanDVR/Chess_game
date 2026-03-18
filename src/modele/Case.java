package modele;

import modele.pieces.Piece;

public class Case {
    private Piece piece;

    public Case(Piece piece) {
        this.piece = piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public Piece getPiece() {
        return piece;
    }
}
