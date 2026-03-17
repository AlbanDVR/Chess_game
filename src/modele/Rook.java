package modele;

/**
 * Classe représentant une tour
 */
public class Rook extends Piece {

    public Rook(int color) {
        super(color);
    }

    @Override
    public void chargerDCA(Case c, Plateau p) {
        this.setDCA(new HV(c, p, true, true));
    }

    @Override
    public void move(Coup coup, Jeu jeu) {
        //to do
    }
}
