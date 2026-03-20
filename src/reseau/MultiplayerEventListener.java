package reseau;

public interface MultiplayerEventListener {
    void onConnected();

    void onGameCreated(String gameId);

    void onJoinedGame(String gameId);

    void onRemoteName(String name);

    void onRemoteMove(int fromRow, int fromCol, int toRow, int toCol);

    void onStatus(String status);

    void onDisconnected(String reason);
}

