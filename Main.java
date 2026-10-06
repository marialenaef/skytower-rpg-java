package skytower;

import skytower.model.Player;
import skytower.ui.GameFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        String name = (args.length > 0) ? args[0] : "Marilena Efst";
        double melee = (args.length > 1) ? Double.parseDouble(args[1]) : 5.0;
        double ranged = (args.length > 2) ? Double.parseDouble(args[2]) : 4.5;
        double defense = (args.length > 3) ? Double.parseDouble(args[3]) : 2.8;
        double magicAtk = (args.length > 4) ? Double.parseDouble(args[4]) : 4.2;
        double magicRst = (args.length > 5) ? Double.parseDouble(args[5]) : 2.5;

        Player player = new Player(name, melee, ranged, defense, magicAtk, magicRst);
        Game game = new Game(player);

        SwingUtilities.invokeLater(() -> {
            GameFrame frame = new GameFrame(game);
            frame.setVisible(true);
            frame.appendLog("Welcome to The Sky Tower, " + player.getName() + "!");
            frame.appendLog("Use [SPACE] to advance through sections | 1 or 2 for potions");
        });
    }
}