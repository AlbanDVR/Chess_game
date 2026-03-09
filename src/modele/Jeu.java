package modele;

import java.util.Observable;

public class Jeu extends Observable implements Runnable {

    private Joueur[] liste = new Joueur[2];
    private int currentPlayer = 0;

    private String[][] positions;

    public Coup nextC;

    public Jeu(){
        positions = new String[][] {
                {"black-rook", "black-knight", "black-bishop", "black-king", "black-queen", "black-bishop", "black-knight", "black-rook"},
                {"black-pawn", "black-pawn", "black-pawn", "black-pawn", "black-pawn", "black-pawn", "black-pawn", "black-pawn"},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"white-pawn", "white-pawn", "white-pawn", "white-pawn", "white-pawn", "white-pawn", "white-pawn", "white-pawn"},
                {"white-rook", "white-knight", "white-bishop", "white-king", "white-queen", "white-bishop", "white-knight", "white-rook"},
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
        if(currentPlayer == 1){
            this.currentPlayer = 0;
        }
        return liste[currentPlayer];
    }

    public void appliquerCoup(Coup c){

    }

    public void set(Coup c){
        nextC = c;

        synchronized (this){
            setChanged();
            notifyObservers();
        }
    }

    public void switchPosition(int i, int j){
    }

    public String getVal(int i, int j) {
        return positions[i][j];
    }
}
