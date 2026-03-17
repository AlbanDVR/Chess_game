package vue.controller;

import modele.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class VC extends JFrame implements Observer {
    private Jeu jeu;
    private JPanel[][] cases = new JPanel[8][8];
    private Coup coup = new Coup();
    private ArrayList<Case> casesAccessibles = new ArrayList<>();

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

        setLayout(new GridLayout(8, 8));
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                final int r = row;
                final int c = col;

                cases[r][c] = new JPanel();
                add(cases[r][c]);

                cases[r][c].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (!coup.ready) {
                            coup.setDep(r, c);
                            cases[r][c].setBackground(Color.YELLOW);

                            // Récupére les cases accessibles
                            Piece piece = jeu.getPlateau().getCase(r, c).getPiece();

                            if (piece != null) {

                                if (piece instanceof Rook || piece instanceof Queen) {
                                    piece.setDCA(new HV(jeu.getPlateau().getCase(r, c), jeu.getPlateau()));
                                }

                                if (piece instanceof Pawn) {
                                    piece.setDCA(new HV(jeu.getPlateau().getCase(r, c), jeu.getPlateau(), 1));
                                }

                                if (piece.getDCA() != null) {
                                    casesAccessibles = piece.getDCA().getCasesAccessibles(jeu);
                                }                            }

                            // Notifier les observateurs pour redessiner
                            jeu.updateObservers();

                        } else {
                            coup.setArr(r, c);
                            jeu.set(coup);
                            coup.resetCoup();
                            casesAccessibles.clear();
                        }
                    }
                });
            }
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400,400);
        setResizable(false);
    }

    @Override
    public void update(Observable o, Object arg) {

        Case[][] positions = jeu.getPlateau().getPositions();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = positions[i][j].getPiece();
                cases[i][j].removeAll();
                cases[i][j].setBackground((i + j) % 2 == 0 ? new Color(118, 150, 86) : new Color(238, 238, 210));

                if (casesAccessibles.contains(positions[i][j])) {
                    cases[i][j].setBackground(new Color(255, 211, 192));
                }

                if (piece != null) {
                    ImageIcon icon = new ImageIcon("assets/" + (piece.getColor() == 0 ? "white" : "black") + "-" + piece.getName() + ".png");
                    Image img = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);

                    cases[i][j].add(new JLabel(new ImageIcon(img)));
                }
            }
        }
        revalidate();
        repaint();
    }
}