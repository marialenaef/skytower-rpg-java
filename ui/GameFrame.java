package skytower.ui;

import skytower.Game;
import skytower.model.Player;
import skytower.model.Resource;
import skytower.model.Aptitude;
import skytower.world.Section;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class GameFrame extends JFrame {
    private final JLabel statusLabel;
    private final JTextArea gameLog;
    private final Game game;
    private final GraphCanvas graphCanvas;
    private final JPanel sectionStripPanel;

    public GameFrame(Game game) {
        super("The Sky Tower");
        this.game = game;
        Player player = game.getPlayer();

        setLayout(new BorderLayout(4, 4));

        // 1. EAST: Character Sheet
        statusLabel = new JLabel();
        statusLabel.setVerticalAlignment(JLabel.TOP);
        updateStatus(player);
        add(new JScrollPane(statusLabel), BorderLayout.EAST);

        // 2. SOUTH: Event Log
        gameLog = new JTextArea(8, 40);
        gameLog.setEditable(false);
        gameLog.setBackground(Color.BLACK);
        gameLog.setForeground(Color.GREEN);
        add(new JScrollPane(gameLog), BorderLayout.SOUTH);

        // 3. NORTH: Section Strip
        sectionStripPanel = new JPanel(new GridLayout(1, 0, 2, 2));
        sectionStripPanel.setBackground(Color.DARK_GRAY);
        updateSectionStrip();
        add(sectionStripPanel, BorderLayout.NORTH);

        // 4. CENTER: Graph Canvas
        graphCanvas = new GraphCanvas(game);
        JScrollPane scrollPane = new JScrollPane(graphCanvas);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // Key Listener for player's input
        graphCanvas.setFocusable(true);
        graphCanvas.requestFocusInWindow();

        graphCanvas.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                graphCanvas.requestFocusInWindow();
            }
        });

        graphCanvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                char key = e.getKeyChar();
                appendLog("Action input: " + key);

                // 1. Perform action on the game controller
                game.handleInput(key);

                // 2. Game messages
                for (String msg : game.flushMessages()) {
                    appendLog(msg);
                }

                // 3. Combat Keys
                if (game.getMode() == Game.GameMode.IN_COMBAT) {

                    appendLog("Available Combat Keys -> [X]: Melee | [V]: Ranged | [C]: Spell | [Z]: Heal | [F]: Flee");
                }
                // 4. Loot Comparison Text
                else if (game.getMode() == Game.GameMode.CHOOSING_LOOT) {
                    String comparisonText = player.getInventory().getLootComparisonText(game.getPendingLoot());
                    appendLog(comparisonText);
                }

                // UI Update
                updateStatus(game.getPlayer());
                updateSectionStrip();
                graphCanvas.revalidate();
                graphCanvas.repaint();

            }
        });


        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                graphCanvas.requestFocusInWindow();
            }
        });

        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void appendLog(String message) {
        gameLog.append(message + "\n");
        gameLog.setCaretPosition(gameLog.getDocument().getLength());
    }

    private void updateSectionStrip() {
        sectionStripPanel.removeAll();
        List<Section> sections = game.getCurrentRoom().getSections();
        int currentIndex = game.getDisplaySectionIndex();

        for (int i = 0; i < sections.size(); i++) {
            Section sec = sections.get(i);

            JButton secBtn = new JButton((i + 1) + ". " + sec.getName()){

            @Override
            protected void paintComponent(Graphics g) { // light cost display
                super.paintComponent(g);

                int lightCost = sec.getLightCost();

                if (lightCost > 0) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    g2d.setColor(new Color(100, 200, 255));
                    g2d.setFont(new Font("Monospaced", Font.BOLD, 11));

                    int textX = 4;
                    int textY = getHeight() - 4;

                    g2d.drawString("-" + lightCost, textX, textY);
                }
            }
        };

            secBtn.setEnabled(false);

            // Coloring based on position (Visited, Active, Future)
            if (i < currentIndex) {
                secBtn.setBackground(new Color(50, 120, 50)); // Visited
                secBtn.setForeground(Color.WHITE);
            } else if (i == currentIndex) {
                secBtn.setBackground(new Color(200, 140, 0)); // Active (Amber)
                secBtn.setForeground(Color.BLACK);
            } else {
                secBtn.setBackground(Color.GRAY); // Future
                secBtn.setForeground(Color.BLACK);
            }
            secBtn.setFocusPainted(false);
            sectionStripPanel.add(secBtn);
        }
        sectionStripPanel.revalidate();
        sectionStripPanel.repaint();
    }

    // Player status update
    public void updateStatus(Player player) {
        StringBuilder sb = new StringBuilder("<html><body style='width: 180px; font-family: sans-serif; padding: 5px;'>");
        sb.append("<b>").append(player.getName()).append("</b> Lv ").append(player.getLevel())
                .append(" XP: ").append(player.getXP())
                .append("<br><hr>");

        for (Resource r : Resource.values()) {
            sb.append(r).append(": ").append(player.get(r))
                    .append("/").append(player.getMax(r)).append("<br>");
        }

        sb.append("<hr>");
        for (Aptitude apt : Aptitude.values()) {
            sb.append(apt).append(": ")
                    .append(String.format("%.2f", player.getAptitude(apt)))
                    .append("<br>");
        }

        sb.append("<hr>");
        var inventory = player.getInventory();

        // Weapon
        sb.append("<b>WEAPON:</b>");
        if (inventory.getEquippedWeapon() != null) {
            sb.append("<br>").append(inventory.getEquippedWeapon()).append("<br>");
        } else {
            sb.append("NONE<br>");
        }

        // Armor
        sb.append("<b>ARMOR:</b>");
        if (inventory.getEquippedArmor() != null) {
            sb.append("<br>").append(inventory.getEquippedArmor()).append("<br>");
        } else {
            sb.append("NONE<br>");
        }

        // Consumables
        sb.append("<b>CONSUMABLES:</b><br>");
        var consumables = inventory.getConsumables();

        // Slot 1
        sb.append("[1] ");
        if (consumables[0] != null) {
            sb.append(consumables[0]);
        } else {
            sb.append("EMPTY");
        }
        sb.append("<br>");

        // Slot 2
        sb.append("[2] ");
        if (consumables[1] != null) {
            sb.append(consumables[1]);
        } else {
            sb.append("EMPTY");
        }
        sb.append("<br>");

        sb.append("<hr>");

        // MODE
        sb.append("<b>MODE: </b>").append(game.getMode()).append("<br><br>");

        // CONTROL KEYS
        sb.append("<span style='font-size: 9px;'>");
        sb.append("[SPACE] -&gt; Advance through sections<br>");
        sb.append("[X] -&gt; Melee Attack<br>");
        sb.append("[V] -&gt; Ranged Attack<br>");
        sb.append("[C] -&gt; Magic Attack<br>");
        sb.append("[Z] -&gt; Heal<br>");
        sb.append("[F] -&gt; Flee<br>");
        sb.append("[R] -&gt; Rest<br>");
        sb.append("[E] -&gt; Check Loot<br>");
        sb.append("[1,2,..] -&gt; Choose next room<br>");
        sb.append("[1]/[2] for consumables");
        sb.append("</span>");

        sb.append("</body></html>");
        statusLabel.setText(sb.toString());
    }
}
