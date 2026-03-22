package modele;

import modele.pieces.King;
import modele.pieces.Bishop;
import modele.pieces.Knight;
import modele.pieces.Pawn;
import modele.pieces.Piece;
import modele.pieces.Queen;
import modele.pieces.Rook;
import reseau.MultiplayerClient;
import reseau.MultiplayerEventListener;
import reseau.WebSocketMultiplayerClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;

public class Jeu extends Observable implements Runnable {
    private static final String WEBSOCKET_SERVER_URL = "wss://chess.milobrt.fr/ws";

    private Thread threadDeJeu;

    public Joueur[] liste = new Joueur[2];
    private Joueur currentPlayer;
    private Plateau plateau;
    protected Coup nextC;
    private Coup pendingCoup;
    private ArrayList<Case> casesAccessibles = new ArrayList<>();

    private final ArrayList<String> moveNotations = new ArrayList<>();
    private final ArrayList<Piece> capturedPieces = new ArrayList<>();
    private final HashMap<String, Integer> positionHistory = new HashMap<>();
    private Coup lastPlayedMove;
    private long gameStartTime;
    private String playerName = "Joueur";
    private String opponentName = "-";
    private String multiplayerStatus = "Hors ligne";
    private String currentGameId = "-";
    private UiNotification pendingNotification;
    private String pendingPromotionChoice;
    private MultiplayerClient multiplayerClient;
    private volatile boolean applyingRemoteMove;
    private Joueur localPlayerColor = liste[1];

    public Jeu() {
        plateau = new Plateau();
        liste[0] = new Joueur(0, this);
        liste[1] = new Joueur(1, this);
        gameStartTime = System.currentTimeMillis();
        currentPlayer = liste[0];
        pendingCoup = new Coup(currentPlayer);
        generateBoardSignature();
    }

    @Override
    public void run() {
        threadDeJeu = Thread.currentThread();
        try {
            jouerPartie();
        }
        catch (InterruptedException e) {
            System.out.println("Restart en bonne et due forme");
        }
        finally {
            threadDeJeu = null;
        }
    }

    public void jouerPartie() throws InterruptedException {
        EtatPartie etat = EtatPartie.EN_COURS;

        while (etat == EtatPartie.EN_COURS || etat == EtatPartie.ECHEC) {
            // S'il est en echec alors pop up
            if (etat == EtatPartie.ECHEC) {
                pushNotification("Attention, votre Roi est en échec !", false, "Alerte");
            }

            Coup c = currentPlayer.getCoup();
            boolean isRemoteMove = applyingRemoteMove;
            while (!coupValide(c)) {
                c = currentPlayer.getCoup();
                isRemoteMove = applyingRemoteMove;
            }
            commitMove(c, !isRemoteMove);
            if (isRemoteMove) {
                applyingRemoteMove = false;
            }

            etat = obtenirEtatActuel(currentPlayer);
        }

        finGame(etat);
    }

    public EtatPartie obtenirEtatActuel(Joueur j) {
        if (isRepetitionPosition()) return EtatPartie.REPETITION_POSITION;
        if (mat(j)) return EtatPartie.MAT;
        if (isPat(j)) return EtatPartie.PAT;
        if (echec(j)) return EtatPartie.ECHEC;
        return EtatPartie.EN_COURS;
    }

    public void finGame(EtatPartie etat) {
        switch (etat) {
            case MAT -> pushNotification("ÉCHEC ET MAT ! Victoire de l'adversaire.", false, "Fin de match");
            case PAT -> pushNotification("MATCH NUL ! Position de PAT (plus de coups légaux).", false, "Fin de match");
            case REPETITION_POSITION ->
                    pushNotification("MATCH NUL ! Répétition de position 3 fois.", false, "Fin de match");
        }
        updateObservers();
    }

    private String generateBoardSignature() {
        StringBuilder sb = new StringBuilder();
        for (Case[] row : plateau.getPositions()) {
            for (Case c : row) {
                Piece piece = c.getPiece();
                if (piece == null) {
                    sb.append(".");
                } else {
                    sb.append(piece.getColor()).append(piece.getName().charAt(0));
                }
            }
        }
        return sb.toString();
    }

    private boolean isRepetitionPosition() {
        String signature = generateBoardSignature();
        int count = positionHistory.getOrDefault(signature, 0) + 1;
        positionHistory.put(signature, count);
        return count >= 3;
    }

    public void set(Coup c) {
        nextC = c;

        synchronized (this) {
            this.notifyAll();
        }
    }

    private void commitMove(Coup c, boolean broadcastMove) {
        moveNotations.add(buildMoveNotation(c));
        setPositions(c);
        lastPlayedMove = new Coup(currentPlayer, c);
        generateBoardSignature();

        if (broadcastMove && isMultiplayerConnected() && !applyingRemoteMove) {
            multiplayerClient.sendMove(c.getDep()[0], c.getDep()[1], c.getArr()[0], c.getArr()[1], c.getPromotionPiece());
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
            Coup playedMove = new Coup(currentPlayer, pendingCoup);
            if (isPromotionMove(playedMove)) {
                playedMove.setPromotionPiece(normalizePromotionChoice(pendingPromotionChoice));
            }
            pendingPromotionChoice = null;
            clearSelection();
            set(playedMove);
            return;
        }

        clearSelection();
        updateObservers();
    }

    public boolean echec(Joueur j) {
        Joueur adversaire = j.getId() == 0 ? liste[1] : liste[0];
        ArrayList<Case> cases = adversaire.chargerListCasesAccessibles();
        Case caseRoi = findKing(j);

        if (caseRoi == null) return false;

        int[] coordKing = plateau.getCoord(caseRoi);
        for (Case caseAtt : cases) {
            int[] coordAtt = plateau.getCoord(caseAtt);
            if (coordKing[0] == coordAtt[0] && coordKing[1] == coordAtt[1]) {
                return true;
            }
        }
        return false;
    }

    private Case findKing(Joueur j) {
        for (Case[] row : plateau.getPositions()) {
            for (Case c : row) {
                if (c.getPiece() != null && c.getPiece() instanceof King && c.getPiece().getColor() == j.getId()) {
                    return c;
                }
            }
        }
        return null;
    }

    public boolean coupValide(Coup c) {
        Plateau savePlateau = this.plateau;
        this.plateau = new Plateau(savePlateau);
        applyMove(c, false);
        boolean enEchec = echec(c.getJoueur());
        this.plateau = savePlateau;
        return !enEchec;
    }

    public boolean mat(Joueur j) {
        if (!echec(j)) {
            return false;
        }
        return !hasLegalMove(j);
    }

    public boolean isPat(Joueur j) {
        if (echec(j)) {
            return false;
        }
        return !hasLegalMove(j);
    }

    private boolean hasLegalMove(Joueur j) {
        for (Case[] ligne : plateau.getPositions()) {
            for (Case c : ligne) {
                if (c.getPiece() != null && c.getPiece().getColor() == j.getId()) {
                    ArrayList<Case> dest = c.getPiece().chargerCasesAccessible(this, this.plateau);
                    for (Case destination : dest) {
                        Coup coupCandidat = new Coup(j);
                        coupCandidat.setDep(plateau.getCoord(c)[0], plateau.getCoord(c)[1]);
                        coupCandidat.setArr(plateau.getCoord(destination)[0], plateau.getCoord(destination)[1]);
                        if (coupValide(coupCandidat)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public int getMoveCount() {
        return moveNotations.size();
    }

    public Coup getLastPlayedMove() {
        if (lastPlayedMove == null) {
            return null;
        }
        return new Coup(lastPlayedMove.getJoueur(), lastPlayedMove);
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
            public void onRemoteMove(int fromRow, int fromCol, int toRow, int toCol, String promotionPiece) {
                Joueur adversaire = (localPlayerColor.getId() == 0) ? liste[1] : liste[0];
                Coup remoteMove = new Coup(adversaire);
                remoteMove.setDep(fromRow, fromCol);
                remoteMove.setArr(toRow, toCol);
                remoteMove.setPromotionPiece(normalizePromotionChoice(promotionPiece));
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
        if (threadDeJeu != null) {
            threadDeJeu.interrupt();
        }
        plateau = new Plateau();
        moveNotations.clear();
        capturedPieces.clear();
        positionHistory.clear();
        lastPlayedMove = null;
        pendingCoup.resetCoup();
        pendingPromotionChoice = null;
        casesAccessibles.clear();
        currentPlayer = liste[0];
        localPlayerColor = liste[0];
        gameStartTime = System.currentTimeMillis();
        generateBoardSignature();

        new Thread(this).start();

        updateObservers();
    }


    private String buildMoveNotation(Coup c) {
        int depCol = c.getDep()[1];
        int arrCol = c.getArr()[1];
        Piece depPiece = plateau.getCase(c.getDep()[0], c.getDep()[1]).getPiece();

        if (depPiece instanceof King && Math.abs(arrCol - depCol) == 2) {
            return arrCol > depCol ? "O-O" : "O-O-O";
        }

        Piece movedPiece = plateau.getCase(c.getDep()[0], c.getDep()[1]).getPiece();
        if (movedPiece == null) {
            return "";
        }

        String notation = piecePrefix(movedPiece) + coordinatesToAlgebraic(c.getArr()[0], c.getArr()[1]);
        if (isEnPassantMove(c, movedPiece)) {
            notation += " e.p.";
        }
        if (movedPiece instanceof Pawn && (c.getArr()[0] == 0 || c.getArr()[0] == 7)) {
            notation += "=" + promotionToNotation(c.getPromotionPiece());
        }
        return notation;
    }

    private boolean isEnPassantMove(Coup move, Piece movedPiece) {
        if (!(movedPiece instanceof Pawn)) {
            return false;
        }

        int depRow = move.getDep()[0];
        int depCol = move.getDep()[1];
        int arrRow = move.getArr()[0];
        int arrCol = move.getArr()[1];
        if (Math.abs(arrCol - depCol) != 1 || Math.abs(arrRow - depRow) != 1) {
            return false;
        }

        Case arrival = plateau.getCase(arrRow, arrCol);
        if (arrival.getPiece() != null) {
            return false;
        }

        Case capturedSquare = plateau.getCase(depRow, arrCol);
        Piece capturedPiece = capturedSquare.getPiece();
        return capturedPiece instanceof Pawn && capturedPiece.getColor() != movedPiece.getColor();
    }

    private String promotionToNotation(String promotionPiece) {
        String normalized = normalizePromotionChoice(promotionPiece);
        return "Knight".equals(normalized) ? "N" : String.valueOf(normalized.charAt(0));
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
        pendingPromotionChoice = null;
        casesAccessibles.clear();
    }

    public boolean isPendingPromotionDestination(int row, int col) {
        if (!pendingCoup.ready) {
            return false;
        }

        Case dep = plateau.getCase(pendingCoup.getDep()[0], pendingCoup.getDep()[1]);
        if (!(dep.getPiece() instanceof Pawn)) {
            return false;
        }

        Case target = plateau.getCase(row, col);
        if (!casesAccessibles.contains(target)) {
            return false;
        }

        int targetRow = row;
        return targetRow == 0 || targetRow == 7;
    }

    public void setPendingPromotionChoice(String promotionPiece) {
        pendingPromotionChoice = normalizePromotionChoice(promotionPiece);
    }

    private boolean canLocalPlayerInteract() {
        return !isMultiplayerConnected() || currentPlayer.equals(localPlayerColor);
    }

    public Plateau getPlateau() {
        return plateau;
    }

    public void switchJoueur() {
        this.currentPlayer = (this.currentPlayer.getId() == 0) ? liste[1] : liste[0];
    }

    public Joueur getCurrentPlayer() {
        return currentPlayer;
    }

    public void setPositions(Coup c) {
        applyMove(c, true);
    }

    private void applyMove(Coup c, boolean trackCapture) {
        Case dep = this.plateau.getCase(c.getDep()[0], c.getDep()[1]);
        Case arr = this.plateau.getCase(c.getArr()[0], c.getArr()[1]);
        Piece movedPiece = dep.getPiece();
        if (movedPiece == null) {
            return;
        }

        Case capturedCase = movedPiece.resolveCapturedCase(this.plateau, c);
        Piece capturedPiece = (capturedCase == null) ? null : capturedCase.getPiece();
        if (trackCapture && capturedPiece != null) {
            capturedPieces.add(capturedPiece);
        }

        arr.setPiece(movedPiece);
        dep.setPiece(null);

        if (capturedCase != null && capturedCase != arr) {
            capturedCase.setPiece(null);
        }

        movedPiece.onMoveApplied(this, this.plateau, c);
        Piece postMovePiece = movedPiece.resolvePostMovePiece(this, this.plateau, c);
        if (postMovePiece != null) {
            postMovePiece.setMoved(true);
            arr.setPiece(postMovePiece);
        }
    }

    private boolean isPromotionMove(Coup move) {
        Case dep = plateau.getCase(move.getDep()[0], move.getDep()[1]);
        if (!(dep.getPiece() instanceof Pawn)) {
            return false;
        }
        int targetRow = move.getArr()[0];
        return targetRow == 0 || targetRow == 7;
    }

    public Piece createPromotionPiece(String promotionPiece, int color) {
        String normalized = normalizePromotionChoice(promotionPiece);
        return switch (normalized) {
            case "Rook" -> new Rook(color, plateau);
            case "Bishop" -> new Bishop(color, plateau);
            case "Knight" -> new Knight(color, plateau);
            default -> new Queen(color, plateau);
        };
    }

    private String normalizePromotionChoice(String promotionPiece) {
        if (promotionPiece == null) {
            return "Queen";
        }

        return switch (promotionPiece.trim()) {
            case "Rook", "Bishop", "Knight", "Queen" -> promotionPiece.trim();
            default -> "Queen";
        };
    }

    public boolean canCastle(int color, boolean kingSide) {
        int row = (color == 0) ? 7 : 0;
        int enemyColor = (color == 0) ? 1 : 0;

        Piece kingPiece = plateau.getCase(row, 4).getPiece();
        if (!(kingPiece instanceof King) || kingPiece.hasMoved()) {
            return false;
        }

        int rookCol = kingSide ? 7 : 0;
        Piece rookPiece = plateau.getCase(row, rookCol).getPiece();
        if (!(rookPiece instanceof Rook) || rookPiece.hasMoved()) {
            return false;
        }

        int startCol = Math.min(4, rookCol) + 1;
        int endCol = Math.max(4, rookCol) - 1;
        for (int col = startCol; col <= endCol; col++) {
            if (plateau.getCase(row, col).getPiece() != null) {
                return false;
            }
        }

        int middleCol = kingSide ? 5 : 3;
        int targetCol = kingSide ? 6 : 2;
        return !isSquareAttacked(row, 4, enemyColor) && !isSquareAttacked(row, middleCol, enemyColor) && !isSquareAttacked(row, targetCol, enemyColor);
    }

    private boolean isSquareAttacked(int row, int col, int attackerColor) {
        Case target = plateau.getCase(row, col);
        for (Case[] line : plateau.getPositions()) {
            for (Case c : line) {
                Piece piece = c.getPiece();
                if (piece == null || piece.getColor() != attackerColor) {
                    continue;
                }

                ArrayList<Case> attacks = piece.chargerCasesAttaquees(this, plateau);
                if (attacks.contains(target)) {
                    return true;
                }
            }
        }
        return false;
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


}
