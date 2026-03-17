package modele;

/**
 * Classe représentant une reine
 */
public class Queen extends Piece {

    public Queen(int color) {
        super(color);
    }

    @Override
    public void chargerDCA(Case c, Plateau p) {
        this.setDCA(new HV(new Diag(c, p), c, p));
    }
}
