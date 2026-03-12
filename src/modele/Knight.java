package modele;

/**
 * Classe représentant un chevalier
 */
public class Knight extends Piece {

    public Knight(int posX, int posY, int  color) {
        super(posX, posY,  color, "knight");
    }

    @Override
    public void deplacer(Coup coup, Jeu jeu) {
        // to do
    }
}
