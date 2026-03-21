package modele;

public class Coup {
    private Joueur joueur;

    private int[] arr = new int[2];
    private int[] dep = new int[2];

    public boolean ready = false;

    public Coup(Joueur joueur){
        this.joueur = joueur;
    }

    public Coup(Joueur joueur, Coup other) {
        this.arr[0] = other.arr[0];
        this.arr[1] = other.arr[1];
        this.dep[0] = other.dep[0];
        this.dep[1] = other.dep[1];
        this.ready = other.ready;
        this.joueur = joueur;
    }

    public void setArr(int i, int j){
        this.arr[0] = i;
        this.arr[1] = j;
    }

    public void setDep(int i, int j){
        this.dep[0] = i;
        this.dep[1] = j;
        this.ready = true;
    }

    public int[] getArr(){
        return this.arr;
    }

    public int[] getDep(){
        return this.dep;
    }

    public Joueur getJoueur(){
        return this.joueur;
    }

    public void resetCoup(){
        ready = false;
    }

}
