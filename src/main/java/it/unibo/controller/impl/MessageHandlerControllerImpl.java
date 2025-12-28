package it.unibo.controller.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.SwingUtilities;

import it.unibo.controller.api.InputControllers;
import it.unibo.controller.api.MessageController;
import it.unibo.swing.panel.logic.ScreenPanel;
import it.unibo.util.Enum.OrderType;
import it.unibo.util.Enum.PanelType;

public class MessageHandlerControllerImpl implements MessageController, InputControllers {

    private final SerialChannel serialChannel;
    private ScreenPanel screenPanel;

    private OrderType currentOrder = null;
    private boolean orderInProgress = false; // TakeOff / Landing
    private boolean logInProgress = false;   // LOG

    private int lineCounter = 0; // Contatore generale comandi
    private int logCounter = 0;  // Contatore LOG
    private int globalCounter = -1;

    private final List<String> labels = List.of(
            "Stato Hangar",
            "Stato Drone",
            "Temperatura Hangar",
            "Take off",
            "Landing"
    );

    public MessageHandlerControllerImpl(SerialChannel serialChannel) {
        this.serialChannel = serialChannel;
    }

    @Override
    public PanelType getPanelType() {
        return PanelType.LOGS;
    }

    public void setScreenPanel(ScreenPanel screenPanel) {
        this.screenPanel = screenPanel;
    }

    public void update() {
        try {
            serialChannel.update();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (serialChannel.hasMsg()) {
            String msg = serialChannel.readMsg();
            parseMessage(msg);
        }
    }

    private void parseMessage(String msg) {
        if (msg == null || msg.isBlank()) return;
        msg = msg.trim();

        String statusPart = null;
        String logPart = null;

        int starIndex = msg.indexOf('*');
        if (starIndex == -1) {
            statusPart = msg;
        } else if (starIndex == 0) {
            logPart = msg.substring(1);
        } else {
            statusPart = msg.substring(0, starIndex);
            logPart = msg.substring(starIndex + 1);
        }

        List<String> statusFields = splitByDash(statusPart);
        List<String> logFields = splitByDash(logPart);

        System.out.println("Status fields: " + statusFields);
        System.out.println("Log fields: " + logFields);

        if (screenPanel != null) {
            if (!statusFields.isEmpty()) {
                updateStatus(statusFields);
                buildLog(statusFields, logFields);
                // Se era TakeOff o Landing, l'ordine è completato
                if (currentOrder == OrderType.TAKE_OFF || currentOrder == OrderType.LANDING) {
                    orderInProgress = false;
                    currentOrder = null;
                }
            }else if (!logFields.isEmpty()) {
                updateLogs(logFields);
                logInProgress = false;
                currentOrder = null;
            }
        }
    }

    private List<String> splitByDash(String part) {
        if (part == null || part.isBlank()) return List.of();
        return Arrays.stream(part.split("-"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    private void buildLog(List<String> statusFields, List<String> logFields) {
        if (logFields == null) logFields = List.of();

        List<String> preparedLog = new ArrayList<>();
        preparedLog.add(statusFields.get(0)); // system
        preparedLog.add(statusFields.get(1)); // drone
        preparedLog.add(logFields.size() > 1 ? logFields.get(1) : "N/A"); // temp

        if ("LANDING".equals(statusFields.get(1))) {
            preparedLog.add("N/A"); // no TakeOff
            preparedLog.add(statusFields.size() > 2 ? statusFields.get(2) : "N/A"); // landing avg
            preparedLog.add(logFields.size() > 0 ? logFields.get(0) : "N/A"); // landing sing
        } else {
            preparedLog.add(logFields.size() > 2 ? logFields.get(2) : "N/A"); // takeOff avg
            preparedLog.add(logFields.size() > 3 ? logFields.get(3) : "N/A"); // takeOff sing
            preparedLog.add("N/A"); // no landing
        }

        updateLogs(preparedLog);
    }

    private void updateStatus(List<String> statusFields) {
        String prefix = "System Order(" + this.lineCounter + ") -> ";
        safeAppendLine(prefix + labels.get(0) + " " + statusFields.get(0) + "\n");
        safeAppendLine(prefix + labels.get(1) + " " + statusFields.get(1) + "\n");

        if ("LANDING".equals(statusFields.get(1)) && statusFields.size() > 2) {
            safeAppendLine(prefix + labels.get(4) + " distance " + statusFields.get(2) + "\n");
        }
    }

    private void updateLogs(List<String> logFields) {
        if (logFields == null || logFields.isEmpty()) return;

        String prefix = "Log -> ";
        List<String> preparedLog = new ArrayList<>();

        for (int i = 0; i < 3 && i < logFields.size(); i++) {
            preparedLog.add(prefix + labels.get(i) + " " + logFields.get(i) + "\n");
        }

        // TakeOff
        if (logFields.size() > 3) {
            preparedLog.add("N/A".equals(logFields.get(3)) ?
                    prefix + labels.get(3) + " N/A\n" :
                    prefix + labels.get(3) + " Distanza Media Decollo " + logFields.get(3) + " Distanza Singola " + (logFields.size() > 4 ? logFields.get(4) : "N/A") + "\n");
        }

        // Landing
        if (logFields.size() > 5) {
            preparedLog.add(prefix + labels.get(4) + " Distanza Media Atterraggio " + logFields.get(4) + " Distanza Singola " + logFields.get(5) + "\n");
        } else if (logFields.size() > 4 && "N/A".equals(logFields.get(4))) {
            preparedLog.add(prefix + labels.get(4) + " N/A\n");
        }
        SwingUtilities.invokeLater(() ->
            screenPanel.getLogsPanel().getScreenArea().setLines(preparedLog)
        );
    }

    @Override
    public void updateLaunchOrder(OrderType order) {
        switch (order) {
            case LOGS:
                if (!logInProgress) {
                    currentOrder = order;
                    logInProgress = true;
                    logCounter = ++globalCounter;
                    safeAppendLine("System -> Sending order LOGS line " + logCounter + "\n");
                } else {
                    safeAppendLine("System -> LOG order denied: still waiting previous LOG\n");
                }
                break;
            case TAKE_OFF:
            case LANDING:
                if (!orderInProgress) {
                    currentOrder = order;
                    orderInProgress = true;
                    lineCounter = ++globalCounter;
                    safeAppendLine("System -> Sending order " + order + " line " + lineCounter + "\n");
                } else {
                    safeAppendLine("System -> " + order + " denied: previous order still in progress\n");
                }
                break;
        }
    }

    private void safeAppendLine(String text) {
        SwingUtilities.invokeLater(() -> {
            if (screenPanel != null) {
                screenPanel.getCommandsPanel().getScreenArea().appendLine(text);
            }
        });
    }

}
