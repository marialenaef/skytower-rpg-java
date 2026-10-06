package skytower.model;

public abstract class Item {
    protected final String name;
    protected final Rarity rarity;

    public Item(String name, Rarity rarity) {
        this.name = name;
        this.rarity = rarity;
    }

    public String getName() { return name; }
    public Rarity getRarity() { return rarity; }
}