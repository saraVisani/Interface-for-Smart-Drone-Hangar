package src.main.java.controller.impl;

import src.main.java.controller.api.MessageController;
import src.main.java.util.Enum.PanelType;

public class OrdersControllerImpl implements MessageController{

    @Override
    public PanelType getPanelType() {
        return PanelType.ORDERS;
    }

}
