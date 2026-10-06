package skytower.model;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ItemGenerator {

    private static final List<String> PREFIXES = List.of("Ancient", "Cursed", "Radiant", "Shadow", "Frozen", "Swift");

    private static final List<String> MELEE_BASES = List.of("Sword", "Dagger", "Axe", "Mace");
    private static final List<String> RANGED_BASES = List.of("Bow", "Crossbow", "Shortbow", "Arbalest");
    private static final List<String> MAGIC_BASES = List.of("Staff", "Wand", "Grimoire");

    private static final List<String> SUFFIXES = List.of("of the Void", "of Ruin", "of Light", "of Agony", "of Precision");

    private static final List<String> PHYSICAL_BASES = List.of("Chestplate", "Chainmail", "Helmet", "Greaves");
    private static final List<String> MAGICAL_BASES = List.of("Wizard Robes", "Enchanted Mantle", "Silk Circlet");
    private static final List<String> ARMOR_SUFFIXES = List.of("of Protection", "of the Guardian", "of Resilience", "of Warding");

    private static final List<String> POTION_PREFIXES = List.of("Minor", "Greater", "Super", "Elixir of", "Extract of");
    private static final List<String> HEALTH_BASES = List.of("Health Potion", "Healing Draught", "Red Vial");
    private static final List<String> MANA_BASES = List.of("Mana Potion", "Energy Vial", "Blue Elixir");
    private static final List<String> STAMINA_BASES = List.of("Stamina Draught", "Vigor Tonic", "Endurance Brew");

    private static final Random rng = new Random();

    public static Weapon generateRandomWeapon(Rarity rarity) {
        int weaponType = rng.nextInt(3);
        String baseName;
        Aptitude primaryAptitude;

        if (weaponType == 0) {
            baseName = MELEE_BASES.get(rng.nextInt(MELEE_BASES.size()));
            primaryAptitude = Aptitude.MELEE;
        } else if (weaponType == 1) {
            baseName = RANGED_BASES.get(rng.nextInt(RANGED_BASES.size()));
            primaryAptitude = Aptitude.RANGED;
        } else {
            baseName = MAGIC_BASES.get(rng.nextInt(MAGIC_BASES.size()));
            primaryAptitude = Aptitude.MAGIC_ATK;
        }

        String prefix = PREFIXES.get(rng.nextInt(PREFIXES.size()));
        String suffix = SUFFIXES.get(rng.nextInt(SUFFIXES.size()));
        String fullName = prefix + " " + baseName + " " + suffix;

        int baseBonus = rarity.getBonusValue();

        Map<Resource, Integer> resourceMap = new EnumMap<>(Resource.class);
        Map<Aptitude, Double> aptitudeMap = new EnumMap<>(Aptitude.class);

        double weaponDamageBonus = baseBonus + rng.nextInt(3) + 1;
        aptitudeMap.put(primaryAptitude, weaponDamageBonus);

        if (rarity == Rarity.RARE || rarity == Rarity.SUPREME) {
            if (primaryAptitude == Aptitude.MAGIC_ATK) {
                resourceMap.put(Resource.MANA, baseBonus * 2);
            } else {
                resourceMap.put(Resource.HP, baseBonus);
            }
        }

        return new Weapon(fullName, rarity, resourceMap, aptitudeMap);
    }

    public static Armor generateRandomArmor(Rarity rarity) {

        int armorType = rng.nextInt(2);
        String baseName;
        Aptitude primaryAptitude;
        Resource primaryResource;
        String prefix = PREFIXES.get(rng.nextInt(PREFIXES.size()));
        String suffix = ARMOR_SUFFIXES.get(rng.nextInt(ARMOR_SUFFIXES.size()));

        if (armorType == 0) {
            baseName = PHYSICAL_BASES.get(rng.nextInt(PHYSICAL_BASES.size()));
            primaryAptitude = Aptitude.DEFENSE;
            primaryResource = Resource.HP;
        }
        else {
            baseName = MAGICAL_BASES.get(rng.nextInt(MAGICAL_BASES.size()));
            primaryAptitude = Aptitude.MAGIC_RST;
            primaryResource = Resource.MANA;
        }

        String fullName = prefix + " " + baseName + " " + suffix;

        int baseBonus = rarity.getBonusValue();
        int rollType = rng.nextInt(2);

        Map<Resource, Integer> resourceMap = new EnumMap<>(Resource.class);
        Map<Aptitude, Double> aptitudeMap = new EnumMap<>(Aptitude.class);


        if (rarity == Rarity.RARE || rarity == Rarity.SUPREME) {
            resourceMap.put(primaryResource, baseBonus * 2 );
            aptitudeMap.put(primaryAptitude, (double) baseBonus);
        }
        else if (rollType == 0){
            resourceMap.put(primaryResource, baseBonus * 2 + rng.nextInt(5));
        }
        else {
            aptitudeMap.put(primaryAptitude, (double) (baseBonus + 1));
        }

        return new Armor(fullName, rarity, resourceMap, aptitudeMap); // Ή με aptitude bonuses αν θες
    }

    public static Consumable generateRandomConsumable(Rarity rarity) {
        int dropType = rng.nextInt(4); // 0: Health, 1: Mana, 2: Stamina, 3: Torch

        if (dropType == 3) {return new Torch();}
        String baseName;
        Resource targetResource;

        if (dropType == 0) {
            baseName = HEALTH_BASES.get(rng.nextInt(HEALTH_BASES.size()));
            targetResource = Resource.HP;
        } else if (dropType == 1) {
            baseName = MANA_BASES.get(rng.nextInt(MANA_BASES.size()));
            targetResource = Resource.MANA;
        } else {
            baseName = STAMINA_BASES.get(rng.nextInt(STAMINA_BASES.size()));
            targetResource = Resource.STAMINA;
        }

        String prefix = POTION_PREFIXES.get(rng.nextInt(POTION_PREFIXES.size()));
        String fullName = prefix + " " + baseName;

        int potency = 10 + (rarity.getBonusValue() * 3) + rng.nextInt(10);

        return new Potion(fullName, rarity, potency, targetResource);
    }
}