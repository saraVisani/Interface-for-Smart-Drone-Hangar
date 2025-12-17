package src.main.java.swing.pannel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RadarPanel extends JPanel implements ActionListener {
    private double radarAngle = 0;
    private Timer timer;

    public RadarPanel() {
        setPreferredSize(new Dimension(150, 150));
        timer = new Timer(50, this);
        timer.start();
        setBackground(Color.DARK_GRAY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth(), h = getHeight();
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // cerchio radar
        g2.setColor(Color.BLACK);
        g2.fillOval(0, 0, w, h);

        g2.setColor(Color.GREEN);
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(0, 0, w-1, h-1);

        // raggio animato
        int cx = w/2, cy = h/2;
        int lx = (int)(cx + Math.cos(Math.toRadians(radarAngle)) * w/2);
        int ly = (int)(cy + Math.sin(Math.toRadians(radarAngle)) * h/2);
        g2.drawLine(cx, cy, lx, ly);

        g2.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        radarAngle += 3;
        if (radarAngle >= 360) radarAngle = 0;
        repaint();
    }
}
