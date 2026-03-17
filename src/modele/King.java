package modele;

/**
 * Classe représentant un roi
 */
public class King extends Piece {

    public King(int color) {
        super(color);
    }

    @Override
    public void chargerDCA(Case c, Plateau p) {
        this.setDCA(new HV(new Diag(c, p, 1, true, true), c, p, 1, true, true));
    }

    @Override
    public void move(Coup coup, Jeu jeu) {
        // to do
    }
}
