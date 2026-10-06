package skytower.model;

import java.util.Map;

public class Armor extends Equippable {
    public Armor(String name, Rarity rarity, Map<Resource, Integer> resourceBonuses, Map<Aptitude, Double> aptitudeBonuses) {
        super(name, rarity, resourceBonuses, aptitudeBonuses);
    }

}