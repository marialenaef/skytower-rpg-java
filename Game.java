package skytower;

import skytower.model.*;
import skytower.world.Room;
import skytower.world.RoomGenerator;
import skytower.world.Section;
import skytower.world.DungeonGraph;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class Game {
    public enum GameMode {
        EXPLORING,
        IN_COMBAT,
        CHOOSING_LOOT,
        AT_EXIT,
        GAME_OVER
    }

    private final Player player;
    private final DungeonGraph allRooms = new DungeonGraph();
    private Room currentRoom;
    private int currentSectionIndex = 0;
    private GameMode mode = GameMode.EXPLORING;
    private final Random rng = new Random();
    private Item pendingLoot = null;
    private final List<String> messageBuffer = new ArrayList<>();
    private int displaySectionIndex = 0;
    private Room finalBossRoom = null;

    private void logMessage(String msg) {
        messageBuffer.add(msg);
    }

    public List<String> flushMessages() {
        List<String> msgs = new ArrayList<>(messageBuffer);
        messageBuffer.clear();
        return msgs;
    }

    public Game(Player player) {
        this.player = player;
        // Creation of 1st room (layer 0)
        Room startRoom = new Room("The Entrance Hall", 0);

        allRooms.addRoom(startRoom);
        enterRoom(startRoom);

        Torch.setOnTorchUsedAction(() -> {
            this.player.set(Resource.LIGHT, 100);
        });

    }


    public void handleInput(char key) {
        LightLevel lightLevel = LightLevel.fromLightValue(player.get(Resource.LIGHT));

        switch (mode) {
            case EXPLORING -> handleExploring(key, lightLevel);
            case IN_COMBAT -> handleCombat(key);
            case AT_EXIT -> handleExit(key);
            case CHOOSING_LOOT -> handleChoosingLoot(key);
            case GAME_OVER -> {}
        }
    }

    private void handleExploring(char key, LightLevel lightLevel) {
        List<Section> sections = currentRoom.getSections();

        if (key == ' ' || key == '\n') {
            if (currentSectionIndex <= sections.size()) {
                displaySectionIndex = currentSectionIndex;
                Section currentSec = sections.get(currentSectionIndex);

                switch (currentSec) {
                    case skytower.world.EncounterSection encSec -> {
                        encSec.interact(player, lightLevel);
                        if (encSec.getEnemy() != null && encSec.getEnemy().isAlive()) {
                            mode = GameMode.IN_COMBAT;
                            logMessage("You have entered a battle with the enemy: " + encSec.getEnemy().getName());
                            logMessage("[COMBAT] Enemy: " + encSec.getEnemy().getName() + " (Level: " + encSec.getEnemy().getLevel() + " |HP: " + encSec.getEnemy().getHp() + "/" + encSec.getEnemy().getMaxHp() + " | Dmg: " + encSec.getEnemy().getDamage() + ")");
                            return; //We are halting progress until the battle ends.
                        }
                    }
                    case skytower.world.WalkSection walkSec -> {
                        walkSec.interact(player, lightLevel);
                        if (walkSec.getEnemy() != null && walkSec.getEnemy().isAlive()) {
                            mode = GameMode.IN_COMBAT;
                            logMessage("Surprise attack! You have entered a battle.");
                            logMessage("[COMBAT] Enemy: " + walkSec.getEnemy().getName() + " (Level: " + walkSec.getEnemy().getLevel() + " |HP: " + walkSec.getEnemy().getHp() + "/" + walkSec.getEnemy().getMaxHp() + " | Dmg: " + walkSec.getEnemy().getDamage() + ")");
                            return;
                        }
                        else logMessage("Exploration -> Press [SPACE] to advance | 1 or 2 for consumables");
                    }
                    case skytower.world.FinalSection finalSec -> {
                        if (finalSec.getEnemy() != null && finalSec.getEnemy().isAlive()) {
                            mode = GameMode.IN_COMBAT;
                            logMessage("You arrived at the Sanctuary of Hastur! The final battle begins!");
                            return;
                        }
                    }
                    case skytower.world.TrapSection trapSec -> {
                        if (!trapSec.isCompleted()) {
                            trapSec.interact(player, lightLevel);
                            if (!player.isAlive()) {
                                mode = GameMode.GAME_OVER;
                                logMessage("Game Over! You got defeated!");
                                System.exit(0);
                            }
                            logMessage(trapSec.toString());
                        }
                        logMessage("Exploration -> Press [SPACE] to advance | 1 or 2 for consumables");
                    }
                    case skytower.world.ResourceSection resSec -> {
                        resSec.interact(player, lightLevel);
                        logMessage(resSec.toString());
                        logMessage("Exploration -> Press [SPACE] to advance | 1 or 2 for consumables");
                    }
                    case skytower.world.RestSection restSec -> {
                        if (!restSec.isCompleted()) {
                            restSec.interact(player, lightLevel);
                            logMessage("You arrived at the Campfire. Press 'R' to rest or 'SPACE' to skip.");
                            return;
                        }
                        logMessage("You chose not to rest!");
                    }
                    case skytower.world.LootSection lootSec -> {
                        lootSec.interact(player, lightLevel);
                        logMessage("You found loot on the floor! Press 'E' to check it.");
                        return;
                    }
                    default -> {
                        currentSec.interact(player, lightLevel);
                        logMessage("Exploration -> Press [SPACE] to advance | 1 or 2 for consumables");
                    }
                }

                currentSectionIndex++;
                if (currentSectionIndex == sections.size()) {
                    mode = GameMode.AT_EXIT;
                    logMessage(currentRoom.getExitOptionsDescription());
                }

            }
        }
        else if (Character.toUpperCase(key) == 'E') { // 'E' for interaction with Loot
            Section currentSec = sections.get(currentSectionIndex);

            if (currentSec instanceof skytower.world.LootSection lootSec) {
                Item groundItem = lootSec.getGroundItem();
                if (groundItem != null) {
                    pendingLoot = groundItem;
                    mode = GameMode.CHOOSING_LOOT;
                }
                else {
                    logMessage("Nothing here!");
                    currentSectionIndex++;
                    if (currentSectionIndex == sections.size()) {
                        mode = GameMode.AT_EXIT;
                        logMessage(currentRoom.getExitOptionsDescription());
                    }
                }
            }
        }
        else if (Character.toUpperCase(key) == 'R') {
            Section currentSec = sections.get(currentSectionIndex);
            if (currentSec instanceof skytower.world.RestSection restSec) {
                restSec.rest(player, lightLevel);
                logMessage(restSec.toString());
                logMessage("Exploration -> Press [SPACE] to advance | 1 or 2 for consumables");
                currentSectionIndex++;
                if (currentSectionIndex == sections.size()) {
                    mode = GameMode.AT_EXIT;
                    logMessage(currentRoom.getExitOptionsDescription());}
            } else {
                logMessage("There is not a campfire to rest!");
            }
        }
        else if (key == '1') {
            // Use the item in the first inventory slot.
            boolean success = player.useConsumable(0);
            if (!success) {
                logMessage("Slot 1 is empty!");
            }
        } else if (key == '2') {
            // Use the item in the second inventory slot.
            boolean success = player.useConsumable(1);
            if (!success) {
                logMessage("Slot 2 is empty!");
            }
        }
    }

    private void handleCombat(char key) {
        boolean levelup;
        skytower.world.Section currentSec = currentRoom.getSections().get(currentSectionIndex);
        Enemy enemy = null;
        switch (currentSec) {
            case skytower.world.EncounterSection encSec -> enemy = encSec.getEnemy();
            case skytower.world.WalkSection walkSec -> enemy = walkSec.getEnemy();
            case skytower.world.FinalSection finalSec -> enemy = finalSec.getEnemy();
            case null, default -> {
                return;
            }
        }

        if (enemy == null || !enemy.isAlive()) return;
        if (Character.toUpperCase(key) == 'F') { // flee the battle
            if (player.get(Resource.STAMINA) > 5) {
                player.drain(Resource.STAMINA, 5);

                List<Room> backs = currentRoom.getBackConnections();
                if (!backs.isEmpty()) {
                    currentRoom = backs.get(0); // 1st previous room
                    currentSectionIndex = 0;    // return to the 1st section of the room
                    logMessage("You retreated to the previous room!");
                } else {
                    logMessage("You can't escape!");
                }
                // ----------------------------------------------

                mode = GameMode.EXPLORING;
                logMessage("You escaped the battle!");
                return;
            } else {
                logMessage("Not enough stamina to flee!");
                return;
            }
        }

        boolean playerActed = Combat.processPlayerTurn(key, player, enemy, this::logMessage);

        if (playerActed) {
            player.drain(Resource.STAMINA, 2);

            if (!enemy.isAlive()) {

                if (currentSec instanceof skytower.world.FinalSection) {
                    logMessage("===============================================");
                    logMessage("★★★ VICTORY! ★★★");
                    logMessage("You have defeated the Avatar of Hastur!");
                    logMessage("The Sky Tower crumbles, but your legend begins. YOU WIN!");
                    logMessage("===============================================");

                    mode = GameMode.GAME_OVER;
                    return;
                }

                logMessage("Enemy defeated!");
                levelup = player.addXP(enemy.getXpReward());
                if (levelup){logMessage("You Leveled Up!");}

                if (player.getLevel() >= 10) {
                    logMessage("The tower trembles... All paths now lead to the Sanctuary!");
                    if (finalBossRoom == null) {
                        finalBossRoom = new Room("Sanctuary of Hastur", currentRoom.getLayer() + 1);
                        allRooms.addRoom(finalBossRoom);
                    }

                    for (Room orphan : currentRoom.getForwardConnections()) {
                        if (orphan != finalBossRoom) {
                            allRooms.removeRoom(orphan);
                        }
                    }
                    currentRoom.getForwardConnections().clear();
                    currentRoom.addForwardConnection(finalBossRoom);
                }

                currentSec.setCompleted(true);

                pendingLoot = Combat.EnemyDrop(LightLevel.fromLightValue(player.get(Resource.LIGHT)),this::logMessage);

                if (pendingLoot != null) {
                    mode = GameMode.CHOOSING_LOOT;
                } else {
                    logMessage("Exploration -> Press [SPACE] to advance | 1 or 2 for consumables");
                    mode = GameMode.EXPLORING;
                    currentSectionIndex++;
                }
                return;
            }

            // Enemy Attack
            String enemyActionMsg = enemy.performAction();
            if (enemyActionMsg != null) {
                logMessage(enemyActionMsg);
            }

            player.takeDamage(enemy.getDamage());

            if (!player.isAlive()) {
                mode = GameMode.GAME_OVER;
                logMessage("Game Over! You got defeated!.");
                System.exit(0);
            }
            logMessage("[COMBAT] Enemy: " + enemy.getName() + " (Level: " + enemy.getLevel() + " |HP: " + enemy.getHp() + "/" + enemy.getMaxHp() + " | Dmg: " + enemy.getDamage() + ")");
        }
    }

    private void handleChoosingLoot(char key) {
        char upperKey = Character.toUpperCase(key);
        Section currentSec = currentRoom.getSections().get(currentSectionIndex);

        if ((pendingLoot instanceof Consumable) && upperKey == 'Y') {
            logMessage("Press '1' (Slot 1) or '2' (Slot 2) or 'N' (No).");
            return;
        }

        if (upperKey == 'Y' || upperKey == 'N' || upperKey == '1' || upperKey == '2') {
            Item leftoverItem = player.getInventory().handleItemSelection(player, pendingLoot, upperKey, this::logMessage);
            if (currentSec instanceof skytower.world.LootSection lootSec) {
                lootSec.setGroundItem(leftoverItem);
            }

            logMessage("Loot procedure completed.");
            player.updateResourceLimits();
        } else {
            logMessage("Invalid option. Press 'Y', 'N', '1' ή '2'.");
            return;
        }

        pendingLoot = null;
        logMessage("Exploration -> Press [SPACE] to advance | 1 or 2 for consumables");
        mode = GameMode.EXPLORING;
        currentSectionIndex++;

    }


    private void handleExit(char key) {
        // Choose room by pressing the corresponding number
        if (key >= '1' && key <= '9') {
            int idx = key - '1';
            List<Room> connections = currentRoom.getAllConnections();
            if (idx < connections.size()) {
                Room nextRoom = connections.get(idx);
                enterRoom(nextRoom);
            }
        }
    }

    public void enterRoom(Room room) {
        this.currentRoom = room;
        if (!room.isVisited()) {
            room.setVisited(true);
            room.setSections(RoomGenerator.buildSections(room.getName(), currentRoom.getLayer(), allRooms));

            int currentLayer = room.getLayer();
            int nextLayer = currentLayer + 1;

            if (room.getName().equals("Sanctuary of Hastur")) {
                // no forward room
            }
            else if (player.getLevel() >= 10) {
                // if player is on a random room
                if (finalBossRoom == null) {
                    finalBossRoom = new Room("Sanctuary of Hastur", nextLayer);
                    allRooms.addRoom(finalBossRoom);
                }
                // all unexplored rooms when explored lead to the final room
                if (!room.getForwardConnections().contains(finalBossRoom)) {
                    room.addForwardConnection(finalBossRoom);
                }

            }else{
                int forwardCount = 1 + rng.nextInt(2); // 1 or 2 forward rooms

                // 1. FORWARD CONNECTIONS
                for (int i = 0; i < forwardCount; i++) {
                    Room placeholder = new Room(RoomGenerator.generateRoomName(), nextLayer);
                    allRooms.addRoom(placeholder);
                    room.addForwardConnection(placeholder);
                }
            }

            // 2. BACK CONNECTIONS (Visited)
            List<Room> prevRooms = allRooms.getRoomsAtLayer(currentLayer - 1);
            List<Room> visitedPrevRooms = new java.util.ArrayList<>();

            for (Room r : prevRooms) {
                if (r.isVisited()) visitedPrevRooms.add(r);
            }

            if (!visitedPrevRooms.isEmpty()) {
                // shuffle to randomly select
                java.util.Collections.shuffle(visitedPrevRooms);
                int backCount = 1 + rng.nextInt(Math.min(2, visitedPrevRooms.size()));

                for (int i = 0; i < backCount; i++) {
                    room.addBackConnection(visitedPrevRooms.get(i));
                }
            }

            // 3. SAME LAYER CONNECTIONS
            List<Room> sameLayerRooms = allRooms.getRoomsAtLayer(currentLayer);
            int currentIndex = sameLayerRooms.indexOf(room);
            if (currentIndex != -1 && currentIndex < sameLayerRooms.size() - 1) {
                Room nextSameLayerRoom = sameLayerRooms.get(currentIndex + 1);
                if (!room.getAllConnections().contains(nextSameLayerRoom)) {
                    room.addSameLayerConnection(nextSameLayerRoom);
                }
            }
        } else {
            for (Section sec : room.getSections()) {
                if (sec instanceof skytower.world.RestSection) {
                    sec.setCompleted(false);
                }
            }
        }

        logMessage("You entered " + room.getName() + "...");

        if (room.getName().equals("Sanctuary of Hastur")) {
            this.currentSectionIndex = 0;
        }else {
            if (!currentRoom.getSections().isEmpty()) {
                currentRoom.getSections().get(0).setCompleted(true);
            }
            this.currentSectionIndex = 1;
        }
        this.displaySectionIndex = 0;
        this.mode = GameMode.EXPLORING;
    }

    public Player getPlayer() { return player; }
    public Room getCurrentRoom() { return currentRoom; }
    public int getCurrentSectionIndex() { return currentSectionIndex; }
    public GameMode getMode() { return mode; }
    public List<Room> getAllRooms() { return allRooms.getRooms(); }
    public Item getPendingLoot() {return pendingLoot;}
    public int getDisplaySectionIndex() {
        return displaySectionIndex;
    }
}
