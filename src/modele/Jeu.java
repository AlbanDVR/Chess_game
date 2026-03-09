package modele;

import java.util.Observable;

public class Jeu extends Observable implements Runnable {

    private Joueur[] liste = new Joueur[2];
    private int currentPlayer = 0;

    private Piece[][] positions;

    public Coup nextC;

    public Jeu(){
        positions = new Piece[][] {
                {new Rook(0, 0, 1), new Knight(1, 0, 1), new Bishop(2, 0, 1), new King(3, 0, 1), new Queen(4, 0, 1), new Bishop(5, 0, 1), new Knight(6, 0, 1), new Rook(7, 0, 1)},
                {new Pawn(0, 1, 1), new Pawn(1, 1, 1), new Pawn(2, 1, 1), new Pawn(3, 1, 1), new Pawn(4, 1, 1), new Pawn(5, 1, 1), new Pawn(6, 1, 1), new Pawn(7, 1, 1)},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {new Pawn(0, 1, 0), new Pawn(1, 1, 0), new Pawn(2, 1, 0), new Pawn(3, 1, 0), new Pawn(4, 1, 0), new Pawn(5, 1, 0), new Pawn(6, 1, 0), new Pawn(7, 1, 0)},
                {new Rook(0, 0, 0), new Knight(1, 0, 0), new Bishop(2, 0, 0), new King(3, 0, 0), new Queen(4, 0, 0), new Bishop(5, 0, 0), new Knight(6, 0, 0), new Rook(7, 0, 0)},
        };

        liste[0] = new Joueur(this);
        liste[1] = new Joueur(this);
    }

    @Override
    public void run() {
        try {
            jouerPartie();
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void jouerPartie() throws InterruptedException {
        while(!partieTerminee()){
            Joueur joueur = getJoueurSuivant();
            Coup c = joueur.getCoup();
            appliquerCoup(c);
        }
    }

    public boolean partieTerminee(){
        return true;
    }

    public Joueur getJoueurSuivant(){
        if(currentPlayer == 0){
            this.currentPlayer = 1;
        }
        else {
            this.currentPlayer = 0;
        }
        return liste[currentPlayer];
    }

    public void appliquerCoup(Coup c){
        int[] pos1 = c.getPos1();
        int[] pos2 = c.getPos2();
        this.positions[pos1[0]][pos1[1]] = this.positions[pos2[0]][pos2[1]];
        this.positions[pos2[0]][pos2[1]] = null;
    }

    public void set(Coup c){
        nextC = c;

        synchronized (this){
            appliquerCoup(nextC);
            setChanged();
            notifyObservers();
        }
    }

    public void switchPosition(int i, int j){
    }

    public Piece getVal(int i, int j) {
        return positions[i][j];
    }
}
