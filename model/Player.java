package skytower.model;

import java.util.EnumMap;
import java.util.Map;

public class Player {

    private static final int BASE_HP = 30;
    private static final int BASE_STAMINA = 20;
    private static final int BASE_MANA = 15;
    private static final int HP_FACTOR = 20;
    private static final int STAMINA_FACTOR = 15;
    private static final int MANA_FACTOR = 10;
    private static final int MAX_FOOD = 20;
    private static final int MAX_LIGHT = 100;
    private static final int DEFENSE_FACTOR = 5;

    private static final int[] XP_TABLE = {
            0,      // Level 1
            100,    // Level 2
            300,    // Level 3
            700,    // Level 4
            1500,   // Level 5
            3000,   // Level 6
            6000,   // Level 7
            12000,  // Level 8
            24000,  // Level 9
            48000   // Level 10
    };

    private final String name;
    private int level;
    private int xp;
    private final Inventory inventory = new Inventory();

    private final Map<Aptitude, Double> currentAptitudes = new EnumMap<>(Aptitude.class);
    private final Map<Resource, Integer> currentResources = new EnumMap<>(Resource.class);

    public Player(String name, double melee, double ranged, double defense, double magicAtk, double magicRst) {
        this.name = name;
        this.level = 1;
        this.xp = 0;

        // Normalization of aptitudes to sum to 5.0
        double sum = melee + ranged + defense + magicAtk + magicRst;
        if (sum <= 0) sum = 5.0;
        double norm = 5.0 / sum;

        currentAptitudes.put(Aptitude.MELEE, melee * norm);
        currentAptitudes.put(Aptitude.RANGED, ranged * norm);
        currentAptitudes.put(Aptitude.DEFENSE, defense * norm);
        currentAptitudes.put(Aptitude.MAGIC_ATK, magicAtk * norm);
        currentAptitudes.put(Aptitude.MAGIC_RST, magicRst * norm);

        // Initialization to max
        restoreToMax(true);
    }

    public int getMax(Resource resource) {
        // 1.Basic max limit
        int baseMax = switch (resource) {
            case HP -> (int) Math.round(BASE_HP + level * (getAptitude(Aptitude.DEFENSE) * HP_FACTOR));
            case STAMINA -> (int) Math.round(BASE_STAMINA + level * (0.6 * getAptitude(Aptitude.MELEE) + 0.4 * getAptitude(Aptitude.DEFENSE)) * STAMINA_FACTOR);
            case MANA -> (int) Math.round(BASE_MANA + level * ((getAptitude(Aptitude.MAGIC_ATK) + getAptitude(Aptitude.MAGIC_RST)) * MANA_FACTOR));
            case FOOD -> MAX_FOOD;
            case LIGHT -> MAX_LIGHT;
        };

        // 2. Equipment bonus (Weapon & Armor)
        int equipmentBonus = this.inventory.getEquipmentResourceBonus(resource);

        return baseMax + equipmentBonus;
    }

    public int get(Resource resource) {
        return currentResources.getOrDefault(resource, 0);
    }

    public void set(Resource resource, int value) {
        int max = getMax(resource);
        currentResources.put(resource, Math.clamp(value, 0, max));
    }

    public void updateResourceLimits() {
        for (Resource r : Resource.values()) {
            set(r, get(r));
        }
    }

    public void drain(Resource resource, int amount) {
        set(resource, get(resource) - amount);
    }

    public void restore(Resource resource, int amount) {
        set(resource, get(resource) + amount);
    }

    public void restoreToMax(boolean lighting) {
        for (Resource r : Resource.values()) {
            if (!lighting && r == Resource.LIGHT) {
                continue;
            }
            else currentResources.put(r, getMax(r));
        }
    }

    public double getAptitude(Aptitude aptitude) {
        double baseValue = currentAptitudes.getOrDefault(aptitude, 0.0);
        double equippedBonus = this.inventory.getEquipmentAptitudeBonus(aptitude);
        return baseValue + equippedBonus;
    }

    public int getResource(Resource resource) {
        int baseValue = currentResources.getOrDefault(resource, 0);
        int equippedBonus = this.inventory.getEquipmentResourceBonus(resource);
        return baseValue + equippedBonus;
    }

    // XP addition and check for level up
    public boolean addXP(int amount) {
        this.xp += amount;
        boolean leveledUp = false;

        while (level < 10 && xp >= XP_TABLE[level]) {
            level++;
            leveledUp = true;

            for (Map.Entry<Aptitude, Double> entry : currentAptitudes.entrySet()) {
                double currentValue = entry.getValue();
                entry.setValue(currentValue * 1.3); // aptitudes up 3% per level
            }

            restoreToMax(false); // Restore resources to max on level up
        }
        return leveledUp;
    }

    public boolean isAlive() {
        return get(Resource.HP) > 0;
    }

    public String getName() { return name; }
    public int getLevel() { return level; }
    public int getXP() { return xp; }
    public Inventory getInventory() {
        return inventory;
    }

    public void takeDamage(int rawDamage) {

        double staminaRatio = (double) get(Resource.STAMINA) / getMax(Resource.STAMINA);

        int effectiveDefense = (int) (getAptitude(Aptitude.DEFENSE) * DEFENSE_FACTOR * staminaRatio);

        int damageTaken = Math.max(1, rawDamage - effectiveDefense); // damage >= 1

        drain(Resource.HP, damageTaken);
    }

    public boolean useConsumable(int index) {
        Consumable[] consumables = inventory.getConsumables();

        if (index >= 0 && index < consumables.length && consumables[index] != null) {
            Consumable item = consumables[index];

            item.use(this);

            consumables[index] = null;
            return true;
        }
        return false; // Empty slot or wrong index
    }
}
