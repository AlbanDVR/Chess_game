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
            setPositions(c);
            updateObservers();
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
