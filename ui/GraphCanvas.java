package skytower.ui;

import skytower.world.Room;
import skytower.Game;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GraphCanvas extends JPanel {
    private final Game game;

    public GraphCanvas(Game game) {
        this.game = game;
        setBackground(Color.BLACK);
    }

    @Override
    public Dimension getPreferredSize() {
        int maxLayer = 0;
        int maxRoomsInOneLayer = 0;

        if (game != null && game.getAllRooms() != null) {
            // Find the highest layer
            maxLayer = game.getAllRooms().stream()
                    .mapToInt(Room::getLayer)
                    .max().orElse(0);

            // Number of rooms in the widest floor
            Map<Integer, Long> roomsPerLayer = game.getAllRooms().stream()
                    .collect(Collectors.groupingBy(Room::getLayer, Collectors.counting()));

            maxRoomsInOneLayer = roomsPerLayer.values().stream()
                    .mapToInt(Long::intValue)
                    .max().orElse(0);
        }

        // 90 pixels height for each layer + 120 pixels
        int requiredHeight = (maxLayer * 90) + 120;

        // 160 pixels width for each room + 100 pixels
        int requiredWidth = (maxRoomsInOneLayer * 160) + 100;

        // Size provided by the window (the parent panel)
        int currentWidth = getParent() != null ? getParent().getWidth() : 800;
        int defaultHeight = getParent() != null ? getParent().getHeight() : 600;

        //We return the biggest one
        return new Dimension(Math.max(currentWidth, requiredWidth), Math.max(defaultHeight, requiredHeight));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (game == null || game.getAllRooms() == null) return;

        List<Room> rooms = game.getAllRooms();
        int width = getWidth();
        int height = getHeight();

        // Rooms start from the bottom (layer 0)
        int startY = height - 60;
        int spacingY = 90;

        // 1. Calculation of coordinates
        Map<Room, Point> roomPositions = new HashMap<>();
        Map<Integer, List<Room>> roomsByLayer = rooms.stream()
                .collect(Collectors.groupingBy(Room::getLayer));

        for (Map.Entry<Integer, List<Room>> entry : roomsByLayer.entrySet()) {
            int layer = entry.getKey();
            List<Room> layerRooms = entry.getValue();

            int y = startY - (layer * spacingY);

            int totalRoomsInLayer = layerRooms.size();
            int spacingX = width / (totalRoomsInLayer + 1);

            for (int i = 0; i < totalRoomsInLayer; i++) {
                Room room = layerRooms.get(i);
                int x = spacingX * (i + 1) - 60;
                roomPositions.put(room, new Point(x + 60, y + 22));
            }
        }

        // 2. Drawing lines
        g2d.setColor(new Color(70, 70, 70));
        g2d.setStroke(new BasicStroke(2.0f));

        for (Room room : rooms) {
            Point p1 = roomPositions.get(room);
            if (p1 == null) continue;

            for (Room neighbor : room.getAllConnections()) {
                Point p2 = roomPositions.get(neighbor);
                if (p2 != null) {
                    g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }
        }

        // 3. Node rendering
        for (Map.Entry<Integer, List<Room>> entry : roomsByLayer.entrySet()) {
            int layer = entry.getKey();
            List<Room> layerRooms = entry.getValue();

            int y = startY - (layer * spacingY);
            int spacingX = width / (layerRooms.size() + 1);

            for (int i = 0; i < layerRooms.size(); i++) {
                Room room = layerRooms.get(i);
                int x = spacingX * (i + 1) - 60;

                if (room == game.getCurrentRoom()) {
                    g2d.setColor(new Color(255, 165, 0));
                } else if (room.isVisited()) {
                    g2d.setColor(new Color(0, 100, 0));
                } else {
                    g2d.setColor(Color.GRAY);
                }

                g2d.fillRoundRect(x, y, 120, 45, 10, 10);
                g2d.setColor(Color.WHITE);
                g2d.drawRoundRect(x, y, 120, 45, 10, 10);

                String label = (room == game.getCurrentRoom() || room.isVisited()) ? room.getName() : "???";


                FontMetrics fm = g2d.getFontMetrics();

                // If room name doesn't fit -> ...
                while (fm.stringWidth(label) > 110 && label.length() > 3) {
                    label = label.substring(0, label.length() - 4) + "...";
                }

                int textWidth = fm.stringWidth(label);
                int textX = x + (120 - textWidth) / 2;
                int textY = y + 27;

                g2d.drawString(label, textX, textY);
            }
        }
    }
}