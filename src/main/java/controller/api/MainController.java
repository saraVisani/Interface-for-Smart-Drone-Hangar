package src.main.java.controller.api;

import src.main.java.util.Enum.PanelType;

public interface MainController {
    MessageController getControllerFor(PanelType type);
}
