package skytower.world;

import skytower.model.Player;
import skytower.model.LightLevel;

public class GraceSection extends Section {
    public GraceSection(String name) {
        super(name, 0);
    }

    @Override
    public void interact(Player player, LightLevel lightLevel) {
        completed = true;
    }
}
