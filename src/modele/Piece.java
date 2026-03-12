package modele;

public abstract class Piece {
    private final String name;
    private final int color; // 0 : blanc | 1 : noir

    public Piece(int color) {
        this.color = color;
        this.name = String.valueOf(this);
    }

    public String getName() {
        return name;
    }
    public int getColor() {
        return color;
    }

    public abstract void move(Coup coup, Jeu jeu);

}
