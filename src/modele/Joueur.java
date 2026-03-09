package modele;

public class Joueur {

    private Jeu jeu;

    public Joueur(Jeu jeu) {
        this.jeu = jeu;
    }

    public Coup getCoup() throws InterruptedException {

        synchronized (jeu){
            jeu.wait();
        }

        return jeu.nextC;
    }

}
