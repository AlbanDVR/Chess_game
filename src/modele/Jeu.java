package modele;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;

public class Jeu extends Observable implements Runnable {

    private Joueur[] liste = new Joueur[2];
    private int currentPlayer = 0;
    private Plateau plateau;
    public Coup nextC;

    public Jeu(){
        plateau = new Plateau();
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

    public Plateau getPlateau() {
        return plateau;
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
            this.plateau.getPositions()[nextC.getDep()[0]][nextC.getDep()[1]].getPiece().move(nextC, this);
            updateObservers();
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(this.plateau.getCase(i, j).getPiece() == null){
                    System.out.print(" x ");
                }
                else{
                    System.out.print(this.plateau.getCase(i, j).getPiece().getName());
                }
            }
            System.out.println();
        }
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setPositions(Coup c){
        Case dep = this.plateau.getCase(c.getDep()[0],  c.getDep()[1]);
        Case arr =  this.plateau.getCase(c.getArr()[0],  c.getArr()[1]);
        arr.setPiece(dep.getPiece());
        dep.setPiece(null);
    }

    public void updateObservers() {
        setChanged();
        notifyObservers();
    }

}
