package vue.controller;

import modele.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

public class VC extends JFrame implements Observer {
    private modele.Jeu jeu;
    private JPanel[][] cases = new JPanel[8][8];

    public static void main(String [] args){
        modele.Jeu j = new Jeu();
        VC vc = new VC(j);
        j.addObserver(vc);
        vc.setVisible(true);
        new Thread(j).start();
    }

    public VC(modele.Jeu jeu) {
        super("Jeu d'échecs");

        damier(jeu);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400,400);
        setResizable(false);
    }

    @Override
    public void update(Observable o, Object arg) {
        damier(this.jeu);
    }

    public void damier(Jeu jeu){
        this.jeu = jeu;

        setLayout(new GridLayout(8,8));
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                final int r = row;
                final int c = col;

                cases[row][col] = new JPanel();
                add(cases[row][col]);

                if ((row + col) % 2 == 0) {
                    cases[row][col].setBackground(new Color(118, 150, 86));
                }
                else {
                    cases[row][col].setBackground(new Color(238, 238, 210));
                }

                if (!jeu.getVal(row, col).isEmpty()) {
                    ImageIcon icon = new ImageIcon("assets/"+jeu.getVal(row, col)+".png");
                    Image img = icon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);

                    cases[row][col].add(new JLabel(new ImageIcon(img)));
                }

                cases[row][col].addMouseListener(new MouseAdapter() {
                    Coup coup = new Coup();

                    @Override
                    public void mouseClicked(MouseEvent e) {

                        if(!coup.ready){
                            coup.setPos2(r, c);
                        }
                        else{
                            coup.setPos1(r, c);
                            jeu.set(coup);
                            coup.resetCoup();
                        }
                    }
                });
            }
        }
    }


}