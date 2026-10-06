package skytower.world;

import skytower.model.Player;
import skytower.model.LightLevel;
import skytower.model.Enemy;
import skytower.model.EnemyFactory;

public class EncounterSection extends Section {
    private Enemy enemy;

    public EncounterSection() {
        super("Monster Lair", 0);
    }

    @Override
    public void interact(Player player, LightLevel lightLevel) {
        if (isCompleted()) return;
        int effectiveLevel = player.getLevel() + lightLevel.getEnemyLevelBonus();
        this.enemy = EnemyFactory.spawnEnemy(effectiveLevel, player.getLevel());
    }

    public Enemy getEnemy() { return enemy; }
}
