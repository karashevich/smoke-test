package automation.ui;

import com.intellij.openapi.Disposable;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

/**
 * Created by jetbrains on 10/02/16.
 */
public class ClickRipple extends JComponent implements Disposable{

    private int x;
    private int y;
    private int r;
    private Color color;
    private Component glassPane;

    public ClickRipple(Component glassPane, int x, int y, int r, Color color) {
        super();
        this.x = x;
        this.y = y;
        this.color = color;
        this.r = r;
        this.glassPane = glassPane;
    }

    public void set(int x, int y, int r, Color color){
        this.x = x;
        this.y = y;
        this.r = r;
        this.color = color;
    }

    public void dispose(){
//        this.setVisible(false);
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) this.getGraphics();
        initSettings(g2d);
        g2d.setColor(color);
        g2d.draw(new Line2D.Float(0f,0f,getBounds().width, getBounds().height));
        g2d.draw(new Ellipse2D.Float(x - r, y - r, 2 * r, 2 * r));
    }

    private void initSettings(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setStroke(new BasicStroke(1.5f));
    }
}
