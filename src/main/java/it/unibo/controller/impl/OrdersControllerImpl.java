package it.unibo.controller.impl;

import it.unibo.controller.api.MessageController;
import it.unibo.util.Enum.PanelType;

public class OrdersControllerImpl implements MessageController{

    private final SerialChannel serialChannel;

    public OrdersControllerImpl(SerialChannel serialChannel) {
        this.serialChannel = serialChannel;
    }

    @Override
    public PanelType getPanelType() {
        return PanelType.ORDERS;
    }

}
