package src.main.java;

import src.main.java.controller.api.MainController;
import src.main.java.controller.impl.MainControllerImpl;
import src.main.java.swing.Home;
public class Main {

    public static void main(String[] args) {
        MainController controller = new MainControllerImpl();
        new Home(controller);
    }
}
