package modele;

public enum Direction {
    HAUT(0, -1),
    BAS(0, 1),
    GAUCHE(-1, 0),
    DROITE(1, 0),
    HD(1, -1),
    HG(-1, -1),
    BD(1, 1),
    BG(-1, 1);

    private int dx;
    private int dy;

    Direction (int _dx, int _dy){
        dx = _dx;
        dy = _dy;
    }

}
