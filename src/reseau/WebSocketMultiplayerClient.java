package reseau;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletionStage;

public class WebSocketMultiplayerClient implements MultiplayerClient, WebSocket.Listener {
    private WebSocket webSocket;
    private MultiplayerEventListener listener;
    private final StringBuilder incomingBuffer = new StringBuilder();

    @Override
    public void connect(String serverUrl, MultiplayerEventListener listener) {
        this.listener = listener;
        try {
            HttpClient.newHttpClient()
                    .newWebSocketBuilder()
                    .buildAsync(URI.create(serverUrl), this)
                    .join();
        } catch (Exception e) {
            if (this.listener != null) {
                this.listener.onStatus("Erreur connexion: " + e.getMessage());
            }
        }
    }

    @Override
    public void sendSetName(String name) {
        sendText("{\"type\":\"set_name\",\"name\":\"" + escapeJson(name) + "\"}");
    }

    @Override
    public void sendCreateGame() {
        sendText("{\"type\":\"create_game\"}");
    }

    @Override
    public void sendJoinGame(String gameId) {
        sendText("{\"type\":\"join_game\",\"gameId\":\"" + escapeJson(gameId) + "\"}");
    }

    @Override
    public void sendMove(int fromRow, int fromCol, int toRow, int toCol, String promotionPiece) {
        StringBuilder payload = new StringBuilder("{\"type\":\"move\",\"fromRow\":")
                .append(fromRow)
                .append(",\"fromCol\":").append(fromCol)
                .append(",\"toRow\":").append(toRow)
                .append(",\"toCol\":").append(toCol);

        if (promotionPiece != null && !promotionPiece.trim().isEmpty()) {
            payload.append(",\"promotion\":\"")
                    .append(escapeJson(promotionPiece.trim()))
                    .append("\"");
        }

        payload.append("}");
        sendText(payload.toString());
    }

    @Override
    public boolean isConnected() {
        return webSocket != null && !webSocket.isOutputClosed() && !webSocket.isInputClosed();
    }

    @Override
    public void close() {
        if (webSocket != null) {
            try {
                webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "bye").join();
            } catch (Exception ignored) {
            }
            webSocket = null;
        }
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        this.webSocket = webSocket;
        if (listener != null) {
            listener.onConnected();
        }
        WebSocket.Listener.super.onOpen(webSocket);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        incomingBuffer.append(data);
        if (last) {
            String message = incomingBuffer.toString();
            incomingBuffer.setLength(0);
            handleMessage(message);
        }
        return WebSocket.Listener.super.onText(webSocket, data, last);
    }

    @Override
    public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
        return WebSocket.Listener.super.onBinary(webSocket, data, last);
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        if (listener != null) {
            listener.onDisconnected(reason);
        }
        this.webSocket = null;
        return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        if (listener != null) {
            listener.onStatus("Erreur websocket: " + error.getMessage());
        }
        WebSocket.Listener.super.onError(webSocket, error);
    }

    private void sendText(String payload) {
        if (webSocket != null) {
            webSocket.sendText(payload, true);
        }
    }

    private void handleMessage(String message) {
        if (listener == null || message == null) {
            return;
        }

        String type = extractStringField(message, "type");
        if ("created".equals(type)) {
            listener.onGameCreated(extractStringField(message, "gameId"));
            return;
        }
        if ("joined".equals(type)) {
            listener.onJoinedGame(extractStringField(message, "gameId"));
            return;
        }
        if ("name".equals(type)) {
            listener.onRemoteName(extractStringField(message, "name"));
            return;
        }
        if ("move".equals(type)) {
            Integer fromRow = extractIntField(message, "fromRow");
            Integer fromCol = extractIntField(message, "fromCol");
            Integer toRow = extractIntField(message, "toRow");
            Integer toCol = extractIntField(message, "toCol");
            if (fromRow != null && fromCol != null && toRow != null && toCol != null) {
                String promotion = extractStringField(message, "promotion");
                listener.onRemoteMove(fromRow, fromCol, toRow, toCol, promotion);
            }
            return;
        }

        if ("status".equals(type) || "statuts".equals(type)) {
            String statusMessage = extractStringField(message, "message");
            if (statusMessage.isEmpty()) {
                statusMessage = extractStringField(message, "status");
            }
            if (!statusMessage.isEmpty()) {
                listener.onStatus(statusMessage);
            }
        }
    }

    private String extractStringField(String json, String field) {
        String key = "\"" + field + "\":\"";
        int start = json.indexOf(key);
        if (start < 0) {
            return "";
        }
        int valueStart = start + key.length();
        int valueEnd = json.indexOf('"', valueStart);
        if (valueEnd < 0) {
            return "";
        }
        return json.substring(valueStart, valueEnd);
    }

    private Integer extractIntField(String json, String field) {
        String key = "\"" + field + "\":";
        int start = json.indexOf(key);
        if (start < 0) {
            return null;
        }
        int valueStart = start + key.length();
        int valueEnd = valueStart;
        while (valueEnd < json.length()) {
            char ch = json.charAt(valueEnd);
            if ((ch < '0' || ch > '9') && ch != '-') {
                break;
            }
            valueEnd++;
        }
        if (valueStart == valueEnd) {
            return null;
        }
        try {
            return Integer.parseInt(json.substring(valueStart, valueEnd));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

