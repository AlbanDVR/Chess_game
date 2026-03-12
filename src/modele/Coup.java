package modele;

public class Coup {

    public int[] arr = new int[2];
    public int[] dep = new int[2];

    public boolean ready = false;

    public Coup(){}

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

    public void resetCoup(){
        ready = false;
    }

}
