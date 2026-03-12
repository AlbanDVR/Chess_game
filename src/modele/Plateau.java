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
                {new Case(null), new Case(null), new Case(null), new Case(null), new Case(null), new Case(null), new Case(null), new Case(null)},
                {new Case(new Rook(0)), new Case(new Knight(0)), new Case(new Bishop(0)), new Case(new King(0)), new Case(new Queen(0)), new Case(new Bishop(0)), new Case(new Knight(0)), new Case(new Rook(0))}};

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = positions[i][j].getPiece();
                if (piece != null) {
                    if (piece.getName().equals("Rook") || piece.getName().equals("Queen")) {
                        piece.setDCA(new HV(positions[i][j], this));
                    }
                }
            }
        }
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
        int foundI = -1, foundJ = -1;
        for (int i = 0; i < positions.length; i++) {
            for (int j = 0; j < positions[i].length; j++) {
                if (positions[i][j] == c) {  // ✅ Utiliser == pour la comparaison de références
                    foundI = i;
                    foundJ = j;
                    break;
                }
            }
            if (foundI != -1) break;
        }

        if (foundI == -1) return null;  // Case non trouvée

        int newI = foundI + dir.getDx();
        int newJ = foundJ + dir.getDy();

        // ✅ Vérifier les bornes
        if (newI >= 0 && newI < 8 && newJ >= 0 && newJ < 8) {
            return positions[newI][newJ];
        }
        return null;
    }
}
