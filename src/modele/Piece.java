package modele;

abstract class Piece {
    public String name;
    public int color; // 0 : blanc | 1 : noir

    public int posX;
    public int posY;

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

    public abstract void deplacer(int x1, int y1, int x2, int y2);

}
