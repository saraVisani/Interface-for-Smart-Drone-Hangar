package src.main.java.swing;

import src.main.java.swing.pannel.Console;

import javax.swing.JFrame;

import src.main.java.controller.api.MainController;

public class Home {

    public Home(final MainController controller){
        JFrame frame = new JFrame("Drone Hangar Control Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        Console mainPanel = new Console(controller);
        frame.add(mainPanel);

        frame.setVisible(true);
    }
}
