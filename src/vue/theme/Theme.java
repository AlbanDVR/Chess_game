package vue.theme;

import modele.pieces.Piece;

import java.awt.Color;
import java.util.Locale;

public class Theme {
    private final String displayName;
    private final Color lightSquare;
    private final Color darkSquare;
    private final Color highlightSquare;
    private final String assetFolder;
    private final String filePattern;

    public Theme(String displayName, Color lightSquare, Color darkSquare, Color highlightSquare,
                 String assetFolder, String filePattern) {
        this.displayName = displayName;
        this.lightSquare = lightSquare;
        this.darkSquare = darkSquare;
        this.highlightSquare = highlightSquare;
        this.assetFolder = assetFolder;
        this.filePattern = filePattern;
    }


    public String getDisplayName() {
        return displayName;
    }

    public Color getLightSquare() {
        return lightSquare;
    }

    public Color getDarkSquare() {
        return darkSquare;
    }

    public Color getHighlightSquare() {
        return highlightSquare;
    }

    public String getPieceIconPath(Piece piece) {
        String colorName = piece.getColor() == 0 ? "white" : "black";
        String pieceName = piece.getName().toLowerCase(Locale.ROOT);
        String fileName = String.format(filePattern, colorName, pieceName);
        return "assets/" + assetFolder + "/" + fileName;
    }
}

