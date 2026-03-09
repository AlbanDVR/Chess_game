package modele;

public class Coup {

    public int[] pos1 = new int[2];
    public int[] pos2 = new int[2];

    public boolean ready = false;

    public Coup(){}

    public void setPos1(int i, int j){
        this.pos1[0] = i;
        this.pos1[1] = j;
    }

    public void setPos2(int i, int j){
        this.pos2[0] = i;
        this.pos2[1] = j;
    }

    public int[] getPos1(){
        return this.pos1;
    }

    public int[] getPos2(){
        return this.pos2;
    }

    public void resetCoup(){
        pos1 = new int[2];
        pos2 = new int[2];
        ready = false;
    }

}
