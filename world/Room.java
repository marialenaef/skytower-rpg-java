package skytower.world;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private final String name;
    private final int layer;
    private boolean visited;
    private List<Section> sections;
    private final List<Room> forwardConnections;
    private final List<Room> backConnections;
    private final List<Room> sameLayerConnections;

    public Room(String name, int layer) {
        this.name = name;
        this.layer = layer;
        this.visited = false;
        this.sections = new ArrayList<>();
        this.forwardConnections = new ArrayList<>();
        this.backConnections = new ArrayList<>();
        this.sameLayerConnections = new ArrayList<>();
    }

    public void addForwardConnection(Room room) {
        forwardConnections.add(room);
    }

    public void addBackConnection(Room room) {
        backConnections.add(room);
    }

    public void addSameLayerConnection(Room room) {
        sameLayerConnections.add(room);
    }

    public List<Room> getAllConnections() {
        List<Room> all = new ArrayList<>(forwardConnections);
        all.addAll(backConnections);
        all.addAll(sameLayerConnections);
        return all;
    }

    public String getExitOptionsDescription() {
        List<Room> connections = getAllConnections();
        StringBuilder sb = new StringBuilder("--- AT EXIT --- Choose next room:\n");

        for (int i = 0; i < connections.size(); i++) {
            Room r = connections.get(i);
            String relationType = "";

            if (r.getLayer() > this.layer) {
                relationType = "Next Floor (Forward)";
            } else if (r.getLayer() < this.layer) {
                relationType = "Previous Floor (Backtracking)";
            } else {
                relationType = "Same Floor (Side Room)";
            }
            String displayName = r.isVisited() ? r.getName() : "???";
            sb.append("[").append(i + 1).append("] ")
                    .append(displayName)
                    .append(" (Layer ").append(r.getLayer()).append(") - ").append(relationType).append("\n");
        }

        return sb.toString();
    }

    // Getters & Setters
    public String getName() { return name; }
    public int getLayer() { return layer; }
    public boolean isVisited() { return visited; }
    public void setVisited(boolean visited) { this.visited = visited; }
    public List<Section> getSections() { return sections; }
    public void setSections(List<Section> sections) { this.sections = sections; }
    public List<Room> getForwardConnections() { return forwardConnections; }
    public List<Room> getBackConnections() { return backConnections; }
}
