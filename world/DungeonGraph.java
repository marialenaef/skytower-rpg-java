package skytower.world;

import java.util.ArrayList;
import java.util.List;

public class DungeonGraph {
    private final List<Room> rooms = new ArrayList<>();

    public void addRoom(Room room) {
        if (!rooms.contains(room)) {
            rooms.add(room);
        }
    }

    public List<Room> getRoomsAtLayer(int layer) {
        List<Room> result = new ArrayList<>();
        for (Room r : rooms) {
            if (r.getLayer() == layer) {
                result.add(r);
            }
        }
        return result;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void removeRoom(Room room) {
        rooms.remove(room);
    }
}
