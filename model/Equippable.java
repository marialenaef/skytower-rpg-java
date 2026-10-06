package skytower.model;

import java.util.EnumMap;
import java.util.Map;

public abstract class Equippable extends Item {
    protected final Map<Resource, Integer> resourceBonuses;
    protected final Map<Aptitude, Double> aptitudeBonuses;

    public Equippable(String name, Rarity rarity, Map<Resource, Integer> resourceBonuses, Map<Aptitude, Double> aptitudeBonuses) {
        super(name, rarity);
        this.resourceBonuses = (resourceBonuses != null) ? resourceBonuses : new EnumMap<>(Resource.class);
        this.aptitudeBonuses = (aptitudeBonuses != null) ? aptitudeBonuses : new EnumMap<>(Aptitude.class);
    }

    public Map<Resource, Integer> getResourceBonuses() { return resourceBonuses; }

    public Map<Aptitude, Double> getAptitudeBonuses() {
        return aptitudeBonuses;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("[%s] %s", rarity, name));

        if (!resourceBonuses.isEmpty()) {
            sb.append(" [Resources: ");
            resourceBonuses.forEach((resource, value) -> sb.append(String.format("%s: +%d ", resource, value)));
            sb.append("]");
        }

        if (!aptitudeBonuses.isEmpty()) {
            sb.append(" [Aptitudes: ");
            aptitudeBonuses.forEach((aptitude, value) ->
                    sb.append(String.format("%s: +%.1f ", aptitude, value))
            );
            sb.append("]");
        }

        return sb.toString().trim();
    }

}
