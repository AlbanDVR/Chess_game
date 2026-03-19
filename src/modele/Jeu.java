package modele;

import modele.pieces.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Jeu extends Observable implements Runnable {

    private final Joueur[] liste = new Joueur[2];
    private int currentPlayer = 0;
    private Plateau plateau;
    public Coup nextC;
    private final Coup pendingCoup = new Coup();
    private final ArrayList<Case> casesAccessibles = new ArrayList<>();
    private final ArrayList<String> moveNotations = new ArrayList<>();
    private final ArrayList<Piece> capturedPieces = new ArrayList<>();
    private Coup lastPlayedMove;
    private long gameStartTime;

    public Jeu(){
        plateau = new Plateau();
        liste[0] = new Joueur(this);
        liste[1] = new Joueur(this);
        gameStartTime = System.currentTimeMillis();
    }

    @Override
    public void run() {
        try {
            jouerPartie();
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void set(Coup c){
        nextC = c;

        synchronized (this){
            moveNotations.add(buildMoveNotation(c));
            setPositions(c);
            lastPlayedMove = new Coup(c);
            switchJoueur();
            updateObservers();
        }
    }

    public int getMoveCount() {
        return moveNotations.size();
    }

    public Coup getLastPlayedMove() {
        if (lastPlayedMove == null) {
            return null;
        }
        return new Coup(lastPlayedMove);
    }

    public List<String> getMoveNotations() {
        return new ArrayList<>(moveNotations);
    }

    public List<String> getMoveColumn(int color) {
        ArrayList<String> column = new ArrayList<>();
        int turnNumber = 1;
        for (int i = 0; i < moveNotations.size(); i += 2) {
            int index = (color == 0) ? i : i + 1;
            if (index < moveNotations.size()) {
                column.add(turnNumber + ". " + moveNotations.get(index));
            }
            turnNumber++;
        }
        return column;
    }

    public List<String> getCapturedColumn(int color) {
        ArrayList<String> column = new ArrayList<>();
        int number = 1;
        for (Piece piece : capturedPieces) {
            if (piece.getColor() == color) {
                column.add(number + ". " + piece.getName());
                number++;
            }
        }
        return column;
    }

    public List<Piece> getCapturedPieces() {
        return new ArrayList<>(capturedPieces);
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - gameStartTime;
    }

    public void restart() {
        plateau = new Plateau();
        moveNotations.clear();
        capturedPieces.clear();
        lastPlayedMove = null;
        pendingCoup.resetCoup();
        casesAccessibles.clear();
        currentPlayer = 0;
        gameStartTime = System.currentTimeMillis();
        updateObservers();
    }

    private String buildMoveNotation(Coup c) {
        Case dep = plateau.getCase(c.getDep()[0], c.getDep()[1]);
        Piece movedPiece = dep.getPiece();
        if (movedPiece == null) {
            return "";
        }

        return piecePrefix(movedPiece) + coordinatesToAlgebraic(c.getArr()[0], c.getArr()[1]);
    }

    private String piecePrefix(Piece piece) {
        String pieceName = piece.getName();
        if ("Pawn".equals(pieceName)) {
            return "";
        }
        if ("Knight".equals(pieceName)) {
            return "N";
        }
        return String.valueOf(Character.toUpperCase(pieceName.charAt(0)));
    }

    private String coordinatesToAlgebraic(int row, int col) {
        char file = (char) ('a' + col);
        int rank = 8 - row;
        return "" + file + rank;
    }

    public void handleClick(int row, int col) {
        if (!pendingCoup.ready) {
            selectDeparture(row, col);
            updateObservers();
            return;
        }

        tryPlayMove(row, col);
    }

    public List<Case> getCasesAccessibles() {
        return new ArrayList<>(casesAccessibles);
    }

    private void selectDeparture(int row, int col) {
        clearSelection();
        Case departure = plateau.getCase(row, col);
        Piece piece = departure.getPiece();

        if (piece == null || piece.getColor() != currentPlayer) {
            return;
        }

        pendingCoup.setDep(row, col);
        piece.chargerDCA(departure, plateau);
        if (piece.getDCA() != null) {
            casesAccessibles.addAll(piece.getDCA().getCasesAccessibles(this));
        }
    }

    private void tryPlayMove(int row, int col) {
        Case caseArrivee = plateau.getCase(row, col);

        if (casesAccessibles.contains(caseArrivee)) {
            pendingCoup.setArr(row, col);
            clearSelection();
            set(pendingCoup);
            return;
        }

        clearSelection();
        updateObservers();
    }

    private void clearSelection() {
        pendingCoup.resetCoup();
        casesAccessibles.clear();
    }

    public void jouerPartie() throws InterruptedException {
        while(!partieTerminee()){
            Joueur joueur = getJoueurSuivant();
            Coup c = joueur.getCoup();
            setPositions(c);
        }
    }

    public boolean partieTerminee(){
        return true;
    }

    public Plateau getPlateau() {
        return plateau;
    }

    public Joueur getJoueurSuivant(){
        if(currentPlayer == 0){
            this.currentPlayer = 1;
        }
        else {
            this.currentPlayer = 0;
        }
        return liste[currentPlayer];
    }

    public void switchJoueur(){
        this.currentPlayer = (this.currentPlayer == 0) ? 1 : 0;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setPositions(Coup c){
        Case dep = this.plateau.getCase(c.getDep()[0],  c.getDep()[1]);
        Case arr =  this.plateau.getCase(c.getArr()[0],  c.getArr()[1]);
        Piece capturedPiece = arr.getPiece();
        if (capturedPiece != null) {
            capturedPieces.add(capturedPiece);
        }
        arr.setPiece(dep.getPiece());
        dep.setPiece(null);
    }

    public void updateObservers() {
        setChanged();
        notifyObservers();
    }

}
