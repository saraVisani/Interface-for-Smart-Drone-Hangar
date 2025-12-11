package src.main.java.controller.impl;

import src.main.java.controller.api.MessageController;
import src.main.java.util.Enum.PanelType;

public class MessageHandlerControllerImpl implements MessageController{

    @Override
    public PanelType getPanelType() {
        return PanelType.LOGS;
    }

}
