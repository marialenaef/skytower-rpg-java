package skytower.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RoomGenerator {
    private static final Random rng = new Random();

    private static final List<String> ADJECTIVES = List.of("Dark", "Ancient", "Cursed", "Silent", "Hidden", "Frozen", "Obsidian");
    private static final List<String> NOUNS = List.of("Chamber", "Hall", "Sanctuary", "Vault", "Library", "Terrace", "Crypt");

    public static String generateRoomName() {
        String adj = ADJECTIVES.get(rng.nextInt(ADJECTIVES.size()));
        String noun = NOUNS.get(rng.nextInt(NOUNS.size()));
        return adj + " " + noun;
    }

    public static List<Section> buildSections(String roomName, int currentLayer, DungeonGraph graph) {
        List<Section> sections = new ArrayList<>();

        if (roomName.equals("Sanctuary of Hastur")) {
            sections.add(new FinalSection());
            return sections;
        }

        sections.add(new GraceSection("The Entrance"));

        // 2. 2-6 sections between entrance and exit
        int intermediateCount = 2 + rng.nextInt(5);

        boolean hasEncounter = false;
        boolean hasRest = false;
        boolean hasTrap = false;
        boolean hasLoot = false;
        boolean hasResource = false;

        if (!hasRestOrResourceRecently(currentLayer, graph)) {
            int typeRoll = rng.nextInt(2);
            if (typeRoll == 0) {
                sections.add(new ResourceSection());
                hasResource = true;
            } else {
                sections.add(new RestSection());
                hasRest = true;
            }
            intermediateCount--;
        }

        for (int i = 0; i < intermediateCount; i++) {
            int typeRoll = rng.nextInt(6);
            switch (typeRoll) {

                case 0:
                    if (!hasEncounter) // No more than 1 encounter in each room
                    {
                        sections.add(new EncounterSection());
                        hasEncounter = true;
                    } else {
                        sections.add(new WalkSection(2 + rng.nextInt(7)));
                    }
                    break;

                case 1:
                    if (!hasRest) {
                        sections.add(new RestSection());
                        hasRest = true;
                    } else {
                        sections.add(new WalkSection(2 + rng.nextInt(7)));
                    }
                    break;

                case 2:
                    if (!hasResource) {
                        sections.add(new ResourceSection());
                        hasResource = true;
                    } else {
                        sections.add(new WalkSection(2 + rng.nextInt(7)));
                    }
                    break;

                case 3:
                    if (!hasTrap) {
                        sections.add(new TrapSection());
                        hasTrap = true;
                    } else {
                        sections.add(new WalkSection(2 + rng.nextInt(7)));
                    }
                    break;

                case 4:
                    if (!hasLoot) {
                        sections.add(new LootSection());
                        hasLoot = true;
                    } else {
                        sections.add(new WalkSection(2 + rng.nextInt(7)));
                    }
                    break;

                case 5:
                    sections.add(new WalkSection(2 + rng.nextInt(7)));
                    break;
            }
        }

        sections.add(new GraceSection("The Exit"));

        return sections;
    }

    // There has to be a Rest or Resource Section at least at every two rooms
    public static boolean hasRestOrResourceRecently(int currentLayer, DungeonGraph graph) {
        for (int l = currentLayer - 2; l < currentLayer; l++) {
            if (l < 0) continue;
            List<Room> roomsAtLayer = graph.getRoomsAtLayer(l);
            for (Room r : roomsAtLayer) {
                for (Section s : r.getSections()) {
                    if (s instanceof RestSection || s instanceof ResourceSection) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
