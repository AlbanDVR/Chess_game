package reseau;
public interface MultiplayerClient {
    void connect(String serverUrl, MultiplayerEventListener listener);
    void sendSetName(String name);
    void sendCreateGame();
    void sendJoinGame(String gameId);
    void sendMove(int fromRow, int fromCol, int toRow, int toCol, String promotionPiece);
    boolean isConnected();
    void close();
}
