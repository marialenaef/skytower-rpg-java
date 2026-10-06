package skytower.world;

import skytower.model.EnemyFactory;
import skytower.model.Player;
import skytower.model.Enemy;
import skytower.model.LightLevel;
import java.util.Random;

public class WalkSection extends Section {
    private final int LightDrain;
    private static final Random rng = new Random();
    private Enemy enemy;

    public WalkSection(int lightDrain) {
        super("Dark Corridor", lightDrain); // 2-8 lightdrain
        this.LightDrain = lightDrain;
    }

    @Override
    public void interact(Player player, LightLevel lightLevel) {
        if (isCompleted()) return;
        player.drain(skytower.model.Resource.LIGHT, LightDrain);
        if (rng.nextDouble() < lightLevel.getSurpriseEncounterChance())
        {
            int effectiveLevel = player.getLevel() + lightLevel.getEnemyLevelBonus();
            this.enemy = EnemyFactory.spawnEnemy(effectiveLevel, player.getLevel());
        }
        else completed = true;
    }

    public Enemy getEnemy() { return enemy;}

}
