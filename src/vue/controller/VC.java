package vue.controller;

import modele.Case;
import modele.Coup;
import modele.Jeu;
import modele.Joueur;
import modele.pieces.Piece;
import vue.theme.Theme;
import vue.theme.Themes;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.Timer;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;


public class VC extends JFrame implements Observer {
    private static final int BOARD_SIZE = 8;
    private static final int ANIMATION_DURATION_MS = 220;

    private final Jeu jeu;
    private final JPanel[][] cases = new JPanel[BOARD_SIZE][BOARD_SIZE];
    private Theme currentTheme = Themes.defaultTheme();
    private final Map<String, ImageIcon> scaledIconCache = new HashMap<>();
    private final MoveOverlay moveOverlay = new MoveOverlay();

    private JLabel currentPlayerLabel;
    private JLabel elapsedTimeLabel;
    private JLabel gameLabel;
    private JLabel opponentLabel;
    private JList<String> moveWhitesList;
    private JList<String> moveBlacksList;
    private JList<String> capturedWhitesList;
    private JList<String> capturedBlacksList;

    private Timer moveAnimationTimer;
    private MoveAnimation moveAnimation;
    private int lastRenderedMoveCount;

    public static void main(String[] args) {
        Jeu j = new Jeu();
        VC vc = new VC(j);
        j.addObserver(vc);
        vc.setVisible(true);
        vc.update(j, null);
        j.run();
    }

    public VC(Jeu jeu) {
        super("Jeu d'échecs");
        this.jeu = jeu;

        setJMenuBar(createMenuBar());
        setLayout(new BorderLayout(10, 10));
        setGlassPane(moveOverlay);
        moveOverlay.setVisible(false);

        add(createLeftPanel(), BorderLayout.WEST);

        JPanel boardPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                final int r = row;
                final int c = col;

                cases[r][c] = new JPanel();
                boardPanel.add(cases[r][c]);

                cases[r][c].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (jeu.isPendingPromotionDestination(r, c)) {
                            String choice = askPromotionChoice();
                            if (choice == null) {
                                return;
                            }
                            jeu.setPendingPromotionChoice(choice);
                        }
                        jeu.handleClick(r, c);
                    }
                });
            }
        }
        add(boardPanel, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setResizable(false);

        Timer elapsedTimeTimer = new Timer(1000, e -> updateElapsedTimeLabel());
        elapsedTimeTimer.start();

        lastRenderedMoveCount = jeu.getMoveCount();
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        leftPanel.setPreferredSize(new Dimension(200, 500));

        currentPlayerLabel = new JLabel("Joueur: Blanc");
        elapsedTimeLabel = new JLabel("Temps: 0s");
        gameLabel = new JLabel("Partie: Locale");
        opponentLabel = new JLabel();
        opponentLabel.setVisible(false);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.add(currentPlayerLabel);
        infoPanel.add(elapsedTimeLabel);
        infoPanel.add(gameLabel);
        infoPanel.add(opponentLabel);
        infoPanel.add(Box.createVerticalStrut(10));

        infoPanel.add(new JLabel("Coups:"));

        JPanel movesPanel = new JPanel(new GridLayout(1, 2, 5, 0));

        JPanel whiteMovesPanel = new JPanel(new BorderLayout(0, 4));
        whiteMovesPanel.add(new JLabel("Blancs"), BorderLayout.NORTH);
        moveWhitesList = new JList<>();
        moveWhitesList.setPrototypeCellValue("10. Qh5");
        whiteMovesPanel.add(new JScrollPane(moveWhitesList), BorderLayout.CENTER);

        JPanel blackMovesPanel = new JPanel(new BorderLayout(0, 4));
        blackMovesPanel.add(new JLabel("Noirs"), BorderLayout.NORTH);
        moveBlacksList = new JList<>();
        moveBlacksList.setPrototypeCellValue("10. Qh5");
        blackMovesPanel.add(new JScrollPane(moveBlacksList), BorderLayout.CENTER);

        movesPanel.add(whiteMovesPanel);
        movesPanel.add(blackMovesPanel);
        infoPanel.add(movesPanel);

        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(new JLabel("Pièces prises:"));

        JPanel capturedPanel = new JPanel(new GridLayout(1, 2, 5, 0));

        JPanel whiteCapturedPanel = new JPanel(new BorderLayout(0, 4));
        whiteCapturedPanel.add(new JLabel("Blancs"), BorderLayout.NORTH);
        capturedWhitesList = new JList<>();
        capturedWhitesList.setPrototypeCellValue("10. Queen");
        whiteCapturedPanel.add(new JScrollPane(capturedWhitesList), BorderLayout.CENTER);

        JPanel blackCapturedPanel = new JPanel(new BorderLayout(0, 4));
        blackCapturedPanel.add(new JLabel("Noirs"), BorderLayout.NORTH);
        capturedBlacksList = new JList<>();
        capturedBlacksList.setPrototypeCellValue("10. Queen");
        blackCapturedPanel.add(new JScrollPane(capturedBlacksList), BorderLayout.CENTER);

        capturedPanel.add(whiteCapturedPanel);
        capturedPanel.add(blackCapturedPanel);
        infoPanel.add(capturedPanel);

        leftPanel.add(infoPanel, BorderLayout.CENTER);
        return leftPanel;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu gameMenu = new JMenu("Partie");
        JMenuItem restartItem = new JMenuItem("Recommencer");
        restartItem.addActionListener(e -> jeu.restart());
        gameMenu.add(restartItem);
        JMenuItem castleTestItem = new JMenuItem("Charger test roque");
        castleTestItem.addActionListener(e -> jeu.loadCastleTestBoard());
        gameMenu.add(castleTestItem);
        gameMenu.addSeparator();
        JMenuItem quitItem = new JMenuItem("Quitter");
        quitItem.addActionListener(e -> dispose());
        gameMenu.add(quitItem);

        JMenu themeMenu = new JMenu("Thème");
        for (Theme theme : Themes.all()) {
            JMenuItem themeItem = new JMenuItem(theme.getDisplayName());
            themeItem.addActionListener(e -> applyTheme(theme));
            themeMenu.add(themeItem);
        }

        JMenu multiplayerMenu = new JMenu("Multijoueur");
        JMenuItem changeNameItem = new JMenuItem("Changer mon nom");
        changeNameItem.addActionListener(e -> onChangeName());
        JMenuItem createGameItem = new JMenuItem("Creer une partie");
        createGameItem.addActionListener(e -> onCreateGame());
        JMenuItem joinGameItem = new JMenuItem("Rejoindre une partie");
        joinGameItem.addActionListener(e -> onJoinGame());
        JMenuItem disconnectItem = new JMenuItem("Se deconnecter");
        disconnectItem.addActionListener(e -> jeu.disconnectMultiplayer());
        multiplayerMenu.add(changeNameItem);
        multiplayerMenu.add(createGameItem);
        multiplayerMenu.add(joinGameItem);
        multiplayerMenu.addSeparator();
        multiplayerMenu.add(disconnectItem);


        JMenu helpMenu = new JMenu("Aide");
        JMenuItem aboutItem = new JMenuItem("A propos");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "Projet encadré 3A FISA Info S6\nAlban DOUVRE et Milo BRETET",
                "A propos",
                JOptionPane.INFORMATION_MESSAGE
        ));
        helpMenu.add(aboutItem);

        menuBar.add(gameMenu);
        menuBar.add(themeMenu);
        menuBar.add(multiplayerMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    private void applyTheme(Theme theme) {
        this.currentTheme = theme;
        scaledIconCache.clear();
        moveOverlay.repaint();
        jeu.updateObservers();
    }

    private int resolvePieceSize(int row, int col) {
        int w = cases[row][col].getWidth();
        int h = cases[row][col].getHeight();
        int side = Math.min(w, h);
        if (side <= 0) {
            return 40;
        }
        return Math.max(24, side - 8);
    }

    private ImageIcon getScaledPieceIcon(Piece piece, int targetSize) {
        String path = currentTheme.getPieceIconPath(piece);
        String cacheKey = path + "@" + targetSize;
        ImageIcon cached = scaledIconCache.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        ImageIcon sourceIcon = new ImageIcon(path);
        Image sourceImage = sourceIcon.getImage();
        BufferedImage scaled = new BufferedImage(targetSize, targetSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = scaled.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(sourceImage, 0, 0, targetSize, targetSize, null);
        g2.dispose();

        ImageIcon result = new ImageIcon(scaled);
        scaledIconCache.put(cacheKey, result);
        return result;
    }

    private void syncMoveAnimation() {
        int moveCount = jeu.getMoveCount();

        if (moveCount < lastRenderedMoveCount) {
            stopMoveAnimation();
            lastRenderedMoveCount = moveCount;
            return;
        }

        if (moveCount > lastRenderedMoveCount) {
            Coup lastMove = jeu.getLastPlayedMove();
            if (lastMove != null) {
                startMoveAnimation(lastMove);
            }
            lastRenderedMoveCount = moveCount;
        }
    }

    private void startMoveAnimation(Coup move) {
        stopMoveAnimation();

        int fromRow = move.getDep()[0];
        int fromCol = move.getDep()[1];
        int toRow = move.getArr()[0];
        int toCol = move.getArr()[1];
        Piece movedPiece = jeu.getPlateau().getCase(toRow, toCol).getPiece();
        if (movedPiece == null) {
            return;
        }

        int size = resolvePieceSize(toRow, toCol);
        ImageIcon icon = getScaledPieceIcon(movedPiece, size);
        Point from = getPieceTopLeftOnOverlay(fromRow, fromCol, size);
        Point to = getPieceTopLeftOnOverlay(toRow, toCol, size);

        moveAnimation = new MoveAnimation(toRow, toCol, from, to, icon);
        moveOverlay.update(icon, from.x, from.y);
        moveOverlay.setVisible(true);

        moveAnimationTimer = new Timer(16, e -> advanceMoveAnimation());
        moveAnimationTimer.start();
    }

    private void advanceMoveAnimation() {
        if (moveAnimation == null) {
            return;
        }

        long elapsed = System.currentTimeMillis() - moveAnimation.startMs;
        float progress = Math.min(1.0f, elapsed / (float) ANIMATION_DURATION_MS);
        float eased = (float) (1 - Math.pow(1 - progress, 3));

        int x = moveAnimation.from.x + Math.round((moveAnimation.to.x - moveAnimation.from.x) * eased);
        int y = moveAnimation.from.y + Math.round((moveAnimation.to.y - moveAnimation.from.y) * eased);
        moveOverlay.update(moveAnimation.icon, x, y);
        renderBoard(true);

        if (progress >= 1.0f) {
            stopMoveAnimation();
            renderBoard(false);
        }
    }

    private Point getPieceTopLeftOnOverlay(int row, int col, int size) {
        Point topLeft = SwingUtilities.convertPoint(cases[row][col], 0, 0, moveOverlay);
        int cellWidth = cases[row][col].getWidth();
        int cellHeight = cases[row][col].getHeight();
        int x = topLeft.x + (cellWidth - size) / 2;
        int y = topLeft.y + (cellHeight - size) / 2;
        return new Point(x, y);
    }

    private void stopMoveAnimation() {
        if (moveAnimationTimer != null) {
            moveAnimationTimer.stop();
            moveAnimationTimer = null;
        }
        moveAnimation = null;
        moveOverlay.clear();
    }

    private void renderBoard(boolean hideAnimatedDestination) {
        Case[][] positions = jeu.getPlateau().getPositions();
        List<Case> casesAccessibles = jeu.getCasesAccessibles();

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                Piece piece = positions[i][j].getPiece();
                cases[i][j].removeAll();
                cases[i][j].setBackground((i + j) % 2 == 0
                        ? currentTheme.getLightSquare()
                        : currentTheme.getDarkSquare());

                if (casesAccessibles.contains(positions[i][j])) {
                    cases[i][j].setBackground(currentTheme.getHighlightSquare());
                }

                boolean shouldHidePiece = hideAnimatedDestination
                        && moveAnimation != null
                        && i == moveAnimation.toRow
                        && j == moveAnimation.toCol;

                if (piece != null && !shouldHidePiece) {
                    int pieceSize = resolvePieceSize(i, j);
                    cases[i][j].add(new JLabel(getScaledPieceIcon(piece, pieceSize)));
                }
            }
        }

        revalidate();
        repaint();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> update(o, arg));
            return;
        }

        updateInfoPanel();
        showPendingNotification();
        syncMoveAnimation();
        renderBoard(moveAnimation != null);
    }

    private void updateInfoPanel() {
        Joueur currentPlayer = jeu.getCurrentPlayer();
        currentPlayerLabel.setText("Joueur: " + (currentPlayer.getId() == 0 ? "Blanc" : "Noir"));
        gameLabel.setText("Partie: " + jeu.getGameDisplayLabel());

        boolean onlineGame = jeu.isOnlineGameActive();
        opponentLabel.setVisible(onlineGame);
        if (onlineGame) {
            opponentLabel.setText("Adversaire: " + jeu.getOpponentName());
        }

        updateElapsedTimeLabel();

        moveWhitesList.setListData(jeu.getMoveColumn(0).toArray(new String[0]));
        moveBlacksList.setListData(jeu.getMoveColumn(1).toArray(new String[0]));
        capturedWhitesList.setListData(jeu.getCapturedColumn(0).toArray(new String[0]));
        capturedBlacksList.setListData(jeu.getCapturedColumn(1).toArray(new String[0]));
    }

    private void showPendingNotification() {
        Jeu.UiNotification notification = jeu.consumePendingNotification();
        if (notification == null) {
            return;
        }

        if (notification.message().contains("MAT")) {
            Object[] options = {"Recommencer", "Quitter"};
            int n = JOptionPane.showOptionDialog(this,
                    notification.message(),
                    "Fin de partie",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);

            if (n == JOptionPane.YES_OPTION) {
                jeu.restart();
            } else if (n == JOptionPane.NO_OPTION) {
                dispose();
            }
        }
        else if (notification.error()) {
            JOptionPane.showMessageDialog(this, notification.message(), notification.title(), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateElapsedTimeLabel() {
        long elapsedMs = jeu.getElapsedTime();
        long seconds = elapsedMs / 1000;
        long minutes = seconds / 60;
        long secs = seconds % 60;
        elapsedTimeLabel.setText(String.format("Temps: %02d:%02d", minutes, secs));
    }

    private void onChangeName() {
        String newName = JOptionPane.showInputDialog(this, "Votre nom:", jeu.getPlayerName());
        if (newName != null) {
            jeu.setPlayerName(newName);
        }
    }

    private void onCreateGame() {
        jeu.createOnlineGame();
    }

    private void onJoinGame() {
        String gameId = JOptionPane.showInputDialog(this, "ID de la partie:", jeu.getCurrentGameId());
        if (gameId != null) {
            jeu.joinOnlineGame(gameId);
        }
    }

    private String askPromotionChoice() {
        Object[] options = {"Queen", "Rook", "Bishop", "Knight"};
        Object selected = JOptionPane.showInputDialog(
                this,
                "Choisissez la pièce de promotion :",
                "Promotion",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        return (selected == null) ? null : selected.toString();
    }

    private static class MoveAnimation {
        private final int toRow;
        private final int toCol;
        private final Point from;
        private final Point to;
        private final ImageIcon icon;
        private final long startMs;

        private MoveAnimation(int toRow, int toCol, Point from, Point to, ImageIcon icon) {
            this.toRow = toRow;
            this.toCol = toCol;
            this.from = from;
            this.to = to;
            this.icon = icon;
            this.startMs = System.currentTimeMillis();
        }
    }

    private static class MoveOverlay extends JComponent {
        private ImageIcon icon;
        private int x;
        private int y;

        private MoveOverlay() {
            setOpaque(false);
        }

        private void update(ImageIcon icon, int x, int y) {
            this.icon = icon;
            this.x = x;
            this.y = y;
            repaint();
        }

        private void clear() {
            icon = null;
            setVisible(false);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (icon != null) {
                g.drawImage(icon.getImage(), x, y, null);
            }
        }
    }

}