package skytower.model;

public abstract class Consumable extends Item {
    protected final int potency;

    public Consumable(String name, Rarity rarity, int potency) {
        super(name, rarity);
        this.potency = potency;
    }

    public abstract void use(Player player);

    public int getPotency() { return potency; }
}
