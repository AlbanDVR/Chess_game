package modele;

/**
 * Classe représentant un pion
 */
public class Pawn extends Piece {

    public Pawn(int posX, int posY, int color) {
        super(posX, posY, color, "pawn");
    }

    @Override
    public void deplacer(Coup coup, Jeu jeu) {
        // Si la place est libre
        if(jeu.isAvailable(coup.getArr()[0], coup.getArr()[1])) {
            // Si le pion peut le faire
            if(getPosX() == coup.getArr()[1]) {
                // S'il est blanc
                if(this.getColor() == 0){
                    if(getPosY() == 6 && (getPosY()-coup.getArr()[0] <= 2)){
                        this.setPosition(getPosX(), coup.getArr()[1]);
                        jeu.setPositions(coup);
                    }
                    else if(getPosY()-coup.getArr()[0] <= 1){
                        this.setPosition(getPosX(), coup.getArr()[1]);
                        jeu.setPositions(coup);
                    }
                }
                // S'il est noir
                else {
                    if(getPosY() == 1 && (getPosY()-coup.getArr()[0] <= -2)){
                        this.setPosition(getPosX(), coup.getArr()[1]);
                        jeu.setPositions(coup);
                    }
                    else if(getPosY()-coup.getArr()[0] <= -1){
                        this.setPosition(getPosX(), coup.getArr()[1]);
                        jeu.setPositions(coup);
                    }
                }
            }
        }
    }
}
