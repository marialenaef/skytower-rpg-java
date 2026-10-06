package skytower.world;

import skytower.model.Enemy;
import skytower.model.LightLevel;
import skytower.model.Player;

public class FinalSection extends Section {
        private Enemy enemy ;

        public FinalSection() {
            super("Sanctuary of Hastur", 0);
            this.enemy = new Enemy("Avatar of Hastur", 10, 3000, 50, 0);
        }

        @Override
        public void interact(Player player, LightLevel lightLevel) {}

        public Enemy getEnemy() { return enemy; }
}

