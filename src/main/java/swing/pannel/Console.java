package src.main.java.swing.pannel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;

import src.main.java.controller.api.MainController;
import src.main.java.controller.impl.MessageHandlerControllerImpl;
import src.main.java.util.Enum.PanelType;

public class Console extends JPanel {

    private MainController controller;

    public Console(MainController controller) {
        this.controller = controller;
        setBackground(new Color(60, 60, 60));
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5,5,5,5);

        // ---------- ROW 0 ----------
        // Commands (sinistra)
        CommandsPanel commands = new CommandsPanel(
            (MessageHandlerControllerImpl)this.controller.getControllerFor(PanelType.LOGS)
        );
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 3.0;   // più largo
        c.weighty = 2.0;   // stessa altezza di Logs
        add(commands, c);

        // Logs (destra)
        LogsPanel logs = new LogsPanel(
            (MessageHandlerControllerImpl)this.controller.getControllerFor(PanelType.LOGS)
        );
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1.0;   // più stretto
        c.weighty = 2.0;   // stessa altezza di Commands
        add(logs, c);

        // ---------- ROW 1 ----------
        // Filler a sinistra per mantenere radar allineato a destra
        JPanel filler = new JPanel();
        filler.setOpaque(false);
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 3.0;
        c.weighty = 1.0;
        add(filler, c);

        // Radar a destra, sotto i Logs
        RadarPanel radar = new RadarPanel();

        // Wrapper con FlowLayout allineato a destra
        JPanel radarWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        radarWrapper.setOpaque(false);
        radarWrapper.add(radar);

        // Mantieni radar quadrato e altezza max 1/3 finestra
        radarWrapper.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int availableWidth = logs.getWidth();
                int availableHeight = getHeight() / 3;
                int size = Math.min(availableWidth, availableHeight);
                radar.setPreferredSize(new Dimension(size, size));
                radar.revalidate();
            }
        });

        // Aggiungi wrapper al GridBagLayout
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        add(radarWrapper, c);
    }
}
