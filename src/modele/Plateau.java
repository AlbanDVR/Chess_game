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
                {new Case(null), new Case(new Pawn(0)), new Case(new Pawn(0)), new Case(new Pawn(0)), new Case(new Pawn(0)), new Case(new Pawn(0)), new Case(new Pawn(0)), new Case(new Pawn(0))},
                {new Case(new Rook(0)), new Case(new Knight(0)), new Case(new Bishop(0)), new Case(new King(0)), new Case(new Queen(0)), new Case(new Bishop(0)), new Case(new Knight(0)), new Case(new Rook(0))}};
    }

    public Case[][] getPositions() {
        return positions;
    }

    public int[] getCoord(Case target){
        for (int i = 0; i < positions.length; i++) {
            for (int j = 0; j < positions[i].length; j++) {
                if (positions[i][j] == target) {
                    return new int[]{j, i};
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
            if (foundI != -1) break;
        }

        // Si la case n'est pas trouvée
        if (foundI == -1) return null;

        // On calcul la case adjacente
        int newI = foundI + dir.getDx();
        int newJ = foundJ + dir.getDy();

        // On vérifie qu'elle est dans le plateau, notamment pour les pièces proches du bord
        if (newI >= 0 && newI < 8 && newJ >= 0 && newJ < 8) {
            return positions[newI][newJ];
        }

        // Si on sort du plateau
        return null;
    }
}
