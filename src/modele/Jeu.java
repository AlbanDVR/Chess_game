package modele;

import modele.pieces.King;
import modele.pieces.Piece;
import reseau.MultiplayerClient;
import reseau.MultiplayerEventListener;
import reseau.WebSocketMultiplayerClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Jeu extends Observable implements Runnable {
    private static final String WEBSOCKET_SERVER_URL = "wss://chess.milobrt.fr/ws";

    public Joueur[] liste = new Joueur[2];
    private Joueur currentPlayer;
    private Plateau plateau;
    protected Coup nextC;
    private Coup pendingCoup;
    private ArrayList<Case> casesAccessibles = new ArrayList<>();

    private final ArrayList<String> moveNotations = new ArrayList<>();
    private final ArrayList<Piece> capturedPieces = new ArrayList<>();
    private Coup lastPlayedMove;
    private long gameStartTime;
    private String playerName = "Joueur";
    private String opponentName = "-";
    private String multiplayerStatus = "Hors ligne";
    private String currentGameId = "-";
    private UiNotification pendingNotification;
    private MultiplayerClient multiplayerClient;
    private volatile boolean applyingRemoteMove;
    private Joueur localPlayerColor = liste[1];

    public Jeu(){
        plateau = new Plateau();
        liste[0] = new Joueur(0, this);
        liste[1] = new Joueur(1, this);
        gameStartTime = System.currentTimeMillis();
        currentPlayer = liste[0];
        pendingCoup = new Coup(currentPlayer);
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

    public void jouerPartie() throws InterruptedException {
        while(!partieTerminee()){
            Coup c = currentPlayer.getCoup();
            boolean isRemoteMove = applyingRemoteMove;
            while(!coupValide(c)){
                c = currentPlayer.getCoup();
                isRemoteMove = applyingRemoteMove;
            }
            commitMove(c, !isRemoteMove);
            if (isRemoteMove) {
                applyingRemoteMove = false;
            }
        }
    }

    public boolean partieTerminee(){
        return false;
    }

    public void set(Coup c){
        nextC = c;

        synchronized (this){
            this.notifyAll();
        }
    }

    private void commitMove(Coup c, boolean broadcastMove) {
        moveNotations.add(buildMoveNotation(c));
        setPositions(c);
        lastPlayedMove = new Coup(currentPlayer, c);

        if (broadcastMove && isMultiplayerConnected() && !applyingRemoteMove) {
            multiplayerClient.sendMove(c.getDep()[0], c.getDep()[1], c.getArr()[0], c.getArr()[1]);
        }

        switchJoueur();
        updateObservers();
    }

    public void handleClick(int row, int col) {
        if (!canLocalPlayerInteract()) {
            multiplayerStatus = "En attente du tour adverse";
            updateObservers();
            return;
        }

        if (!pendingCoup.ready) {
            selectDeparture(row, col);
            updateObservers();
            return;
        }

        tryPlayMove(row, col);
    }

    private void tryPlayMove(int row, int col) {
        Case caseArrivee = plateau.getCase(row, col);

        if (casesAccessibles.contains(caseArrivee)) {
            pendingCoup.setArr(row, col);
            clearSelection();
            set(new Coup(currentPlayer, pendingCoup));
            return;
        }

        clearSelection();
        updateObservers();
    }

    public boolean echec(Joueur j){
        // On prend l'adversaire et on calcule tous ces coups possibles
        Joueur adversaire = j.getId() == 0 ? liste[1] : liste[0];
        ArrayList<Case> cases = adversaire.chargerListCasesAccessibles();

        // On cherche la case du roi du joueur
        Case caseRoi = null;
        for(Case[] row : plateau.getPositions()){
            for(Case c : row){
                if(c.getPiece() != null && c.getPiece() instanceof King && c.getPiece().getColor() == j.getId()){
                    caseRoi = c;
                    break;
                }
            }
        }

        // On regarde si la case du roi n'est pas dans les cases attaquables
        int[] coordKing = plateau.getCoord(caseRoi);
        for(Case caseAtt : cases) {
            int[] coordAtt = plateau.getCoord(caseAtt);
            if(coordKing[0] == coordAtt[0] && coordKing[1] == coordAtt[1]){
                return true;
            }
        }
        return false;
    }

    public boolean coupValide(Coup c){
        // On save le plateau
        Plateau savePlateau = this.plateau;

        // On met dans notre jeu un plateau test identique au vrai
        this.plateau = new Plateau(savePlateau);

        // effectue le test
        setPositions(c);

        // On vérifie s'il est en échec
        boolean enEchec = echec(c.getJoueur());

        // On retourne à notre vrai plateau comme si rien ne s'était passé
        this.plateau = savePlateau;

        pushNotification(enEchec ? "Coup invalide: met votre roi en echec !" : "Coup valide", enEchec, "Règles");
        updateObservers();

        return !enEchec;
    }

    public int getMoveCount() {
        return moveNotations.size();
    }

    public Coup getLastPlayedMove() {
        if (lastPlayedMove == null) {
            return null;
        }
        return new Coup(currentPlayer, lastPlayedMove   );
    }

    public List<String> getMoveNotations() {
        return new ArrayList<>(moveNotations);
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public String getMultiplayerStatus() {
        return multiplayerStatus;
    }

    public String getGameDisplayLabel() {
        if (isOnlineGameActive()) {
            return currentGameId;
        }
        return "Locale";
    }

    public boolean isOnlineGameActive() {
        return currentGameId != null && !currentGameId.trim().isEmpty() && !"-".equals(currentGameId);
    }

    public String getCurrentGameId() {
        return currentGameId;
    }

    public boolean isMultiplayerConnected() {
        return multiplayerClient != null && multiplayerClient.isConnected();
    }

    public void setPlayerName(String playerName) {
        if (playerName == null || playerName.trim().isEmpty()) {
            return;
        }
        this.playerName = playerName.trim();
        if (isMultiplayerConnected()) {
            multiplayerClient.sendSetName(this.playerName);
        }
        updateObservers();
    }

    public void createOnlineGame() {
        connectIfNeeded();
        if (!isMultiplayerConnected()) {
            return;
        }
        localPlayerColor = liste[0];
        multiplayerClient.sendCreateGame();
        multiplayerStatus = "Creation de partie...";
        pushNotification("Creation de partie en cours...", false);
        updateObservers();
    }

    public void joinOnlineGame(String gameId) {
        connectIfNeeded();
        if (!isMultiplayerConnected()) {
            return;
        }
        if (gameId == null || gameId.trim().isEmpty()) {
            pushNotification("ID de partie invalide.", true);
            updateObservers();
            return;
        }
        localPlayerColor = liste[1];
        currentGameId = gameId.trim();
        multiplayerClient.sendJoinGame(currentGameId);
        multiplayerStatus = "Connexion a " + currentGameId + "...";
        pushNotification("Connexion a " + currentGameId + "...", false);
        updateObservers();
    }

    public void disconnectMultiplayer() {
        if (multiplayerClient != null) {
            multiplayerClient.close();
            multiplayerClient = null;
        }
        localPlayerColor = liste[0];
        opponentName = "-";
        currentGameId = "-";
        multiplayerStatus = "Hors ligne";
        pushNotification("Mode multijoueur desactive.", false);
        updateObservers();
    }

    private void connectIfNeeded() {
        if (isMultiplayerConnected()) {
            return;
        }

        multiplayerClient = new WebSocketMultiplayerClient();
        multiplayerStatus = "Connexion...";
        updateObservers();
        multiplayerClient.connect(WEBSOCKET_SERVER_URL, new MultiplayerEventListener() {
            @Override
            public void onConnected() {
                multiplayerStatus = "Connecte";
                multiplayerClient.sendSetName(playerName);
                pushNotification("Connecte au serveur.", false);
                updateObservers();
            }

            @Override
            public void onGameCreated(String gameId) {
                currentGameId = gameId;
                multiplayerStatus = "Partie creee: " + gameId;
                pushNotification("Partie creee: " + gameId, false);
                updateObservers();
            }

            @Override
            public void onJoinedGame(String gameId) {
                currentGameId = gameId;
                multiplayerStatus = "Partie rejointe: " + gameId;
                pushNotification("Partie rejointe: " + gameId, false);
                updateObservers();
            }

            @Override
            public void onRemoteName(String name) {
                if (name != null && !name.trim().isEmpty()) {
                    opponentName = name.trim();
                    updateObservers();
                }
            }

            @Override
            public void onRemoteMove(int fromRow, int fromCol, int toRow, int toCol) {
                Joueur adversaire = (localPlayerColor.getId() == 0) ? liste[1] : liste[0];
                Coup remoteMove = new Coup(adversaire);
                remoteMove.setDep(fromRow, fromCol);
                remoteMove.setArr(toRow, toCol);
                applyingRemoteMove = true;
                set(remoteMove);
            }

            @Override
            public void onStatus(String status) {
                multiplayerStatus = status;
                pushNotification(status, true);
                updateObservers();
            }

            @Override
            public void onDisconnected(String reason) {
                multiplayerStatus = "Deconnecte" + (reason == null || reason.isEmpty() ? "" : (": " + reason));
                pushNotification(multiplayerStatus, true);
                updateObservers();
            }
        });
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
        currentPlayer = liste[0];
        localPlayerColor = liste[0];
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

    public List<Case> getCasesAccessibles() {
        return new ArrayList<>(casesAccessibles);
    }

    private void selectDeparture(int row, int col) {
        clearSelection();
        Case departure = plateau.getCase(row, col);
        Piece piece = departure.getPiece();

        if (piece == null || piece.getColor() != currentPlayer.getId()) {
            return;
        }

        pendingCoup.setDep(row, col);
        this.casesAccessibles = piece.chargerCasesAccessible(this, this.getPlateau());
    }

    private void clearSelection() {
        pendingCoup.resetCoup();
        casesAccessibles.clear();
    }

    private boolean canLocalPlayerInteract() {
        return !isMultiplayerConnected() || currentPlayer.equals(localPlayerColor);
    }

    public Plateau getPlateau() {
        return plateau;
    }

    public Joueur getJoueurSuivant(){
        this.currentPlayer = (this.currentPlayer.getId() == 0) ? liste[1] : liste[0];
        this.pendingCoup = new Coup(this.currentPlayer);
        return currentPlayer;
    }

    public void switchJoueur(){
        this.currentPlayer = (this.currentPlayer.getId() == 0) ? liste[1] : liste[0];
    }

    public Joueur getCurrentPlayer() {
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

    public synchronized UiNotification consumePendingNotification() {
        UiNotification notification = pendingNotification;
        pendingNotification = null;
        return notification;
    }

    private synchronized void pushNotification(String message, boolean error) {
        if (message == null || message.trim().isEmpty()) {
            return;
        }
        pendingNotification = new UiNotification(message.trim(), error, "Multijoueur");
    }

    private synchronized void pushNotification(String message, boolean error, String title) {
        if (message == null || message.trim().isEmpty()) {
            return;
        }
        pendingNotification = new UiNotification(message.trim(), error, title);
    }

    public record UiNotification(String message, boolean error, String title) {
    }

}
