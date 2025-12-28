package it.unibo.controller.impl;

import it.unibo.controller.api.MessageController;
import it.unibo.util.Enum.PanelType;

public class MessageHandlerControllerImpl implements MessageController{

    private final SerialChannel serialChannel;

    public MessageHandlerControllerImpl(SerialChannel serialChannel) {
        this.serialChannel = serialChannel;
    }

    @Override
    public PanelType getPanelType() {
        return PanelType.LOGS;
    }

    void update(){
        try {
            serialChannel.update();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
