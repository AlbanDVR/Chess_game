package modele;

import java.util.ArrayList;

public class Joueur {

    private int id;
    private final Jeu jeu;
    public ArrayList<Case> ListCasesAccessibles = new ArrayList<>();

    public Joueur(int id, Jeu jeu) {
        this.id = id;
        this.jeu = jeu;
    }

    public ArrayList<Case> chargerListCasesAccessibles() {
        ListCasesAccessibles.clear();

        for(Case[] row : jeu.getPlateau().getPositions()){
            for(Case c : row){
                if(c.getPiece() != null && c.getPiece().getColor() == id) {
                    ListCasesAccessibles.addAll(c.getPiece().chargerCasesAttaquees(jeu, jeu.getPlateau()));
                }
            }
        }
        return ListCasesAccessibles;
    }

    public Coup getCoup() throws InterruptedException {

        synchronized (jeu){
            jeu.wait();
        }

        return jeu.nextC;
    }

    public int getId(){
        return id;
    }

}
