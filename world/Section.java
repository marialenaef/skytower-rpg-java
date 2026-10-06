package skytower.world;

import skytower.model.Player;
import skytower.model.LightLevel;

public abstract class Section {
    protected final String name;
    protected final int lightCost;
    protected boolean completed = false;

    public Section(String name, int lightCost) {
        this.name = name;
        this.lightCost = lightCost;
    }

    public abstract void interact(Player player, LightLevel lightLevel);

    public String getName() { return name; }
    public int getLightCost() { return lightCost; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}
