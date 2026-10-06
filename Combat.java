package skytower;

import skytower.model.*;
import java.util.function.Consumer;
import java.util.Random;

public class Combat {
    private static final Random rng = new Random();

    // Executes the player's actions and returns true if the player performed a valid action.
    public static boolean processPlayerTurn(char key, Player player, Enemy enemy, Consumer<String> log) {
        int damageDealt = 0;
        boolean playerActed = false;
        double randomMultiplier = 0.7 + (rng.nextDouble() * 0.3);

        switch (Character.toUpperCase(key)) {
            case 'X' -> { // Melee Attack
                damageDealt = (int) (player.getAptitude(Aptitude.MELEE) * randomMultiplier);
                playerActed = true;
            }
            case 'V' -> { // Ranged Attack
                damageDealt = (int) (player.getAptitude(Aptitude.RANGED) * randomMultiplier);
                playerActed = true;
            }
            case 'C' -> { // Cast Spell (Magic Attack)
                if (player.get(Resource.MANA) >= 5) {
                    player.drain(Resource.MANA, 5);
                    damageDealt = (int) (player.getAptitude(Aptitude.MAGIC_ATK));
                    playerActed = true;
                } else {
                    log.accept("Not enough mana!");
                }
            }
            case 'Z' -> { // Heal with magic
                if (player.get(Resource.MANA) >= 5) {
                    player.drain(Resource.MANA, 5);
                    int heal = (int) (player.getAptitude(Aptitude.MAGIC_RST)*10);
                    player.restore(Resource.HP, heal);
                    playerActed = true;
                } else {
                    log.accept("Not enough mana to Heal!");
                }
            }
            case '1' -> {
                if (player.useConsumable(0)) playerActed = true;
                else log.accept("Slot 1 is empty!");
            }
            case '2' -> {
                if (player.useConsumable(1)) playerActed = true;
                else log.accept("Slot 2 is empty!");
            }
        }

        if (playerActed) {
            player.drain(Resource.STAMINA, 2);
            enemy.takeDamage(damageDealt);
        }

        return playerActed;
    }

    public static Item EnemyDrop(LightLevel lightLevel, Consumer<String> log) {
        Rarity rarity = switch (lightLevel) {
            case BRIGHT -> Rarity.FEEBLE;
            case DIM -> Rarity.COMMON;
            case DARK -> Rarity.RARE;
            case PITCH_BLACK -> Rarity.SUPREME;
        };

        // 5% probability for Weapon or Armor
        if (rng.nextDouble() < 0.05) {
            if (rng.nextBoolean()) {
                Weapon weapon = ItemGenerator.generateRandomWeapon(rarity);
                log.accept("RARE loot (Weapon): " + weapon.getName());
                return weapon;
            } else {
                Armor armor = ItemGenerator.generateRandomArmor(rarity);
                log.accept("RARE loot (Armor): " + armor.getName());
                return armor;
            }
        }

        // 25% probability for Consumable
        if (rng.nextDouble() < 0.25) {
            Consumable consumable = ItemGenerator.generateRandomConsumable(rarity);
            log.accept("Loot (Consumable): " + consumable.getName());
            return consumable;
        }

        return null; // No loot
    }

}
