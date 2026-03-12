package modele;

public abstract class Piece {
    private String name;
    private int color; // 0 : blanc | 1 : noir

    private int posX;
    private int posY;

    public Piece(int posX, int posY,  int color, String name) {
        this.posX = posX;
        this.posY = posY;
        this.color = color;
        this.name = name;
    }

    public void setPosition(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public String  getName() {
        return name;
    }
    public int getColor() {
        return color;
    }

    public abstract void deplacer(Coup coup, Jeu jeu);

}
