package modele;

/**
 * Classe représentant un pion
 */
public class Pawn extends Piece {

    public Pawn(int color) {
        super(color);
    }

    @Override
    public void chargerDCA(Case c, Plateau p) {
        // Calcul de la porter du spawn
        int y = p.getCoord(c)[1];
        int portee = 1;
        if ((this.getColor() == 0 && y == 6) || (this.getColor() == 1 && y == 1)) {
            portee = 2;
        }

        this.setDCA(new HV(new Diag(c, p, 1, false, true), c, p, portee, true, false));
    }

    @Override
    public void move(Coup coup, Jeu jeu) {

        // Si la place est libre
//        if(jeu.isAvailable(coup.getArr()[0], coup.getArr()[1])) {
//            // Si le pion peut le faire
//            if(getPosX() == coup.getArr()[1]) {
//                // S'il est blanc
//                if(this.getColor() == 0){
//                    if(getPosY() == 6 && (getPosY()-coup.getArr()[0] <= 2)){
//                        this.setPosition(getPosX(), coup.getArr()[1]);
//                        jeu.setPositions(coup);
//                    }
//                    else if(getPosY()-coup.getArr()[0] <= 1){
//                        this.setPosition(getPosX(), coup.getArr()[1]);
//                        jeu.setPositions(coup);
//                    }
//                }
//                // S'il est noir
//                else {
//                    if(getPosY() == 1 && (getPosY()-coup.getArr()[0] <= -2)){
//                        this.setPosition(getPosX(), coup.getArr()[1]);
//                        jeu.setPositions(coup);
//                    }
//                    else if(getPosY()-coup.getArr()[0] <= -1){
//                        this.setPosition(getPosX(), coup.getArr()[1]);
//                        jeu.setPositions(coup);
//                    }
//                }
//            }
//        }
    }
}
