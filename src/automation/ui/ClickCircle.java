package automation.ui;

/**
* Created by jetbrains on 10/02/16.
*/
import com.google.common.util.concurrent.Monitor;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.event.*;
import java.awt.geom.*;

public class ClickCircle extends JFrame implements ActionListener, MouseListener {
    int x, y;
    int r = 15;
    boolean painting = false;

    public ClickCircle() {
        addMouseListener(this);
        setSize(new Dimension(800, 600));
    }

    public static void main(String[] args) {
        //TODO code application logic here
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ClickCircle frame = new ClickCircle();
                frame.setVisible(true);
            }
        });
    }

    public void actionPerformed(ActionEvent ae) {

    }

    public void drawCircle(int x, int y) {
        Graphics2D g2d = (Graphics2D) this.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setStroke(new BasicStroke(1.5f));

            new Thread(new Runnable() {

                @Override
                public void run() {
                    int steps = 20;
                    for (int i = 1; i <= steps; i++) {
//                        if (i < steps/2) {
                            r = i + 8;
//                        } else {
//                            r = (int) (1.6 * (i - steps/2) + (8 + steps/2));
//                        }
                        repaintSuper(g2d);
//                        g2d.drawOval(x - r, y - r, 2 * r, 2 * r);
                        g2d.draw(new Ellipse2D.Float(x - r, y - r, 2 * r, 2 * r));
                        g2d.setColor(new Color(0.6f, 0.6f, 0.6f, (float) (1.0 - i / (1.0 * steps))));
                        try {
                            Thread.sleep(30);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        repaintSuper(g2d);
                    }
                }
            }).start();
    }


    public void mouseClicked(MouseEvent e) {
        x = e.getX();
        y = e.getY();
        repaint();
    }

    void repaintSuper(Graphics g){
        super.paint(g);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        drawCircle(x, y);
    }

    public void mouseExited(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {

    }

    public void mouseReleased(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }


}
