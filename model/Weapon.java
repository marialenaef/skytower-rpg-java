package skytower.model;

import java.util.Map;

public class Weapon extends Equippable {
    public Weapon(String name, Rarity rarity, Map<Resource, Integer> resourceBonuses, Map<Aptitude, Double> aptitudeBonuses) {
        super(name, rarity, resourceBonuses, aptitudeBonuses);
    }

}