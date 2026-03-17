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
        this.setDCA(new HV(new Diag(c, p, true, true), c, p, true, true));
    }

    @Override
    public void move(Coup coup, Jeu jeu) {
        // to do
    }
}
