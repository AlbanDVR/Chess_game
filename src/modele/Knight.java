package modele;

/**
 * Classe représentant un chevalier
 */
public class Knight extends Piece {

    public Knight(int  color) {
        super(color);
    }

    @Override
    public void chargerDCA(Case c, Plateau p) {
        this.setDCA(new SautCheval(c, p));
    }

    @Override
    public void move(Coup coup, Jeu jeu) {
        // to do
    }
}
