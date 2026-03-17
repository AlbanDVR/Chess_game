package modele;

/**
 * Classe représentant un fou
 */
public class Bishop extends Piece {

    public Bishop(int color) {
        super(color);
    }

    @Override
    public void chargerDCA(Case c, Plateau p) {
        this.setDCA(new Diag(c, p));
    }
}
