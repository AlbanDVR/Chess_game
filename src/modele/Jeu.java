package modele;

import javax.swing.*;
import java.awt.*;
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
                {new Pawn(0, 6, 0), new Pawn(1, 6, 0), new Pawn(2, 6, 0), new Pawn(3, 6, 0), new Pawn(4, 6, 0), new Pawn(5, 6, 0), new Pawn(6, 6, 0), new Pawn(7, 6, 0)},
                {new Rook(0, 7, 0), new Knight(1, 7, 0), new Bishop(2, 7, 0), new King(3, 7, 0), new Queen(4, 7, 0), new Bishop(5, 7, 0), new Knight(6, 7, 0), new Rook(7, 7, 0)},
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
            setPositions(c);
        }
    }

    public boolean partieTerminee(){
        return true;
    }

    public Piece getVal(int i, int j) {
        return positions[i][j];
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

    public void set(Coup c){
        nextC = c;

        synchronized (this){
            if(!isAvailable(nextC.getDep()[0], nextC.getDep()[1])){
                positions[nextC.getDep()[0]][nextC.getDep()[1]].deplacer(nextC, this);
            }
            setChanged();
            notifyObservers();
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(positions[i][j] == null){
                    System.out.print(" x ");
                }
                else{
                    System.out.print(positions[i][j].getName());
                }
            }
            System.out.println();
        }
    }

    public void setPositions(Coup c){
        Piece piece = this.positions[c.getDep()[0]][c.getDep()[1]];
        this.positions[c.getDep()[0]][c.getDep()[1]] = null;
        this.positions[c.getArr()[0]][c.getArr()[1]] = piece;
    }

    public boolean isAvailable(int x, int y){
        return positions[x][y] == null;
    }
}
