package modele;

import modele.mouvement.Direction;
import modele.pieces.Bishop;
import modele.pieces.King;
import modele.pieces.Knight;
import modele.pieces.Pawn;
import modele.pieces.Piece;
import modele.pieces.Queen;
import modele.pieces.Rook;

public class Plateau {
    private final Case[][] positions;

    public Plateau() {
        positions = new Case[][]{{new Case(new Rook(1, this)), new Case(new Knight(1, this)), new Case(new Bishop(1, this)), new Case(new King(1, this)), new Case(new Queen(1, this)), new Case(new Bishop(1, this)), new Case(new Knight(1, this)), new Case(new Rook(1, this))},
                {new Case(new Pawn(1, this)), new Case(new Pawn(1, this)), new Case(new Pawn(1, this)), new Case(new Pawn(1, this)), new Case(new Pawn(1, this)), new Case(new Pawn(1, this)), new Case(new Pawn(1, this)), new Case(new Pawn(1, this))},
                {new Case(null), new Case(null), new Case(null), new Case(null), new Case(null), new Case(null), new Case(null), new Case(null)},
                {new Case(null), new Case(null), new Case(null), new Case(null), new Case(null), new Case(null), new Case(null), new Case(null)},
                {new Case(null), new Case(null), new Case(null), new Case(null), new Case(null), new Case(null), new Case(null), new Case(null)},
                {new Case(null), new Case(null), new Case(null), new Case(null), new Case(null), new Case(null), new Case(null), new Case(null)},
                {new Case(new Pawn(0, this)), new Case(new Pawn(0, this)), new Case(new Pawn(0, this)), new Case(new Pawn(0, this)), new Case(new Pawn(0, this)), new Case(new Pawn(0, this)), new Case(new Pawn(0, this)), new Case(new Pawn(0, this))},
                {new Case(new Rook(0, this)), new Case(new Knight(0, this)), new Case(new Bishop(0, this)), new Case(new King(0, this)), new Case(new Queen(0, this)), new Case(new Bishop(0, this)), new Case(new Knight(0, this)), new Case(new Rook(0, this))}};
    }

    // Constructeur copie
    public Plateau(Plateau data) {
        this.positions = new Case[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                // On récup la pièce d'origine
                Piece pieceOrigine = data.getPositions()[i][j].getPiece();

                // On copie la pièce d'origine
                Piece copyPiece = (pieceOrigine == null) ? null : pieceOrigine.copy(this);

                // Et on la fout dans le plateau copié
                this.positions[i][j] = new Case(copyPiece);
            }
        }
    }

    public Case[][] getPositions() {
        return positions;
    }

    public int[] getCoord(Case target){
        for (int i = 0; i < positions.length; i++) {
            for (int j = 0; j < positions[i].length; j++) {
                if (positions[i][j] == target) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    public Case getCase(int x, int y) {
        return this.positions[x][y];
    }

    public Case getCase(Piece piece) {
        for (Case[] row : positions) {
            for (Case cases : row) {
                if(cases.getPiece() != null && cases.getPiece().equals(piece)) {
                    return cases;
                }
            }
        }
        return null;
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
        int newI = foundI + dir.getDy();
        int newJ = foundJ + dir.getDx();

        // On vérifie qu'elle est dans le plateau, notamment pour les pièces proches du bord
        if (newI >= 0 && newI < 8 && newJ >= 0 && newJ < 8) {
            return positions[newI][newJ];
        }

        // Si on sort du plateau
        return null;
    }
}
