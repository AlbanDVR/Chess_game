package modele.mouvement;

public enum Direction {
    // Vertical
    HAUT(0, -1),
    BAS(0, 1),

    // Horizontal
    GAUCHE(-1, 0),
    DROITE(1, 0),

    // Diagonale
    HD(1, -1),
    HG(-1, -1),
    BD(1, 1),
    BG(-1, 1),

    // Le cheval c trop génial
    C_HAUT_GAUCHE(-1, -2),
    C_HAUT_DROITE(1, -2),
    C_BAS_GAUCHE(-1, 2),
    C_BAS_DROITE(1, 2),
    C_GAUCHE_HAUT(-2, -1),
    C_GAUCHE_BAS(-2, 1),
    C_DROITE_HAUT(2, -1),
    C_DROITE_BAS(2, 1);

    private final int dx;
    private final int dy;

    Direction(int _dx, int _dy) {
        dx = _dx;
        dy = _dy;
    }

    public int getDx(){
        return dx;
    }
    public int getDy(){
        return dy;
    }

}
