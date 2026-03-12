package modele;

/**
 * Classe représentant un roi
 */
public class King extends Piece {

    public King(int posX, int posY, int color) {
        super(posX, posY, color, "king");
    }

    @Override
    public void deplacer(Coup coup, Jeu jeu) {
        // to do
    }
}
