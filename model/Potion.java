package skytower.model;

public class Potion extends Consumable {
    private final Resource targetResource;

    public Potion(String name, Rarity rarity, int potency, Resource targetResource) {
        super(name, rarity, potency);
        this.targetResource = targetResource;
    }

    public Resource getTargetResource() {
        return targetResource;
    }

    @Override
    public void use(Player player) {
        int maxResource = player.getMax(targetResource);
        int restoreAmount = (int) (maxResource * (potency / 100.0));
        restoreAmount = Math.max(1, restoreAmount);
        player.restore(targetResource, restoreAmount);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (Restores %d%% %s)", rarity, name, potency, targetResource);
    }
}