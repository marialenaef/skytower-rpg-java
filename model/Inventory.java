package skytower.model;

import java.util.function.Consumer;

public class Inventory {
    private Weapon equippedWeapon;
    private Armor equippedArmor;
    private final Consumable[] consumables;

    public Inventory() {
        this.equippedWeapon = null;
        this.equippedArmor = null;
        this.consumables = new Consumable[2];
    }

    // --- WEAPON MANAGEMENT ---
    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public Weapon swapWeapon(Weapon newWeapon) {
        Weapon oldWeapon = this.equippedWeapon;
        this.equippedWeapon = newWeapon;
        return oldWeapon;
    }

    // --- ARMOR MANAGEMENT ---
    public Armor getEquippedArmor() {
        return equippedArmor;
    }

    public Armor swapArmor(Armor newArmor) {
        Armor oldArmor = this.equippedArmor;
        this.equippedArmor = newArmor;
        return oldArmor;
    }

    public double getEquipmentAptitudeBonus(Aptitude aptitude) {
        double aptitude_bonus = 0.0;
        if (equippedWeapon != null) {
            aptitude_bonus += equippedWeapon.getAptitudeBonuses().getOrDefault(aptitude, 0.0);
        }
        if (equippedArmor != null) {
            aptitude_bonus += equippedArmor.getAptitudeBonuses().getOrDefault(aptitude, 0.0);
        }
        return aptitude_bonus;
    }

    public int getEquipmentResourceBonus(Resource resource) {
        int totalBonus = 0;
        if (equippedWeapon != null) {
            totalBonus += equippedWeapon.getResourceBonuses().getOrDefault(resource, 0);
        }
        if (equippedArmor != null) {
            totalBonus += equippedArmor.getResourceBonuses().getOrDefault(resource, 0);
        }
        return totalBonus;
    }


    // --- CONSUMABLE MANAGEMENT ---
    public Consumable[] getConsumables() {
        return consumables;
    }

    public Item handleItemSelection(Player player, Item groundItem, char key, Consumer<String> log) {
        if (key == 'Y') {
            if (groundItem instanceof Weapon newWeapon) {
                Weapon oldWeapon = swapWeapon(newWeapon);
                log.accept("You equipped the weapon: " + newWeapon.getName());
                if (oldWeapon != null) {
                    log.accept("Your old weapon fell to the floor.");
                }
                return oldWeapon;

            } else if (groundItem instanceof Armor newArmor) {
                Armor oldArmor = swapArmor(newArmor);
                log.accept("You put on the armor.: " + newArmor.getName());
                if (oldArmor != null) {
                    log.accept("Your old armor fell to the floor.");
                }
                return oldArmor;
            }
        } else if (key == 'N') {
            log.accept("You did not get anything");
            return groundItem;
        } else if (groundItem instanceof Consumable consumableItem) {

            int slot = key-'1';
            if (slot >= 0 && slot < 2) {
                Consumable oldItem = consumables[slot];
                consumables[slot] = consumableItem;
                log.accept("The consumable got into Slot "+(slot+1));
                return oldItem;
            } else {
                log.accept("Invalid Slot!");
                return groundItem;
            }
        }

        return groundItem;
    }

    public String getLootComparisonText(Item newLoot) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n========================================\n");
        sb.append("YOU FOUND LOOT: ").append(newLoot).append("\n");

        if (newLoot instanceof Weapon || newLoot instanceof Armor) {
            Item currentEquipped = (newLoot instanceof Weapon) ? equippedWeapon : equippedArmor;
            sb.append("--- CURRENT EQUIPMENT ---\n");
            sb.append(currentEquipped != null ? currentEquipped : "Nothing").append("\n");
            sb.append("\nOptions: Press 'Y' to equip (the old equipment falls to the ground) or 'N' to reject.");
        } else if (newLoot instanceof Potion) {
            sb.append("--- CURRENT CONSUMABLE ---\n");
            for (int i = 0; i < consumables.length; i++) {
                sb.append("Slot ").append(i + 1).append(": ");
                if (consumables[i] != null) {
                    sb.append(consumables[i].toString()).append("\n");
                } else {
                    sb.append("Empty\n");
                }
            }
            sb.append("\nOptions: Press '1' or '2' to store to the corresponding slot, or 'N' to discard.");
        }
        sb.append("\n========================================");
        return sb.toString();
    }
}
