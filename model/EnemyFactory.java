package skytower.model;

import java.util.Random;

public class EnemyFactory {
    private static final Random rng = new Random();

    public static Enemy spawnEnemy(int effectiveLevel, int playerLevel) {

        if (effectiveLevel >= 9) {
            int roll = rng.nextInt(3);
            if (roll == 0) return new Enemy("Mi-Go", 9, 1000, 40, 1000);
            if (roll == 1) return new Enemy("Shoggoth", 8, 500, 30, 600);
            return new Enemy("Umbral Colossus", 8, 300, 28, 400);

        } else if (effectiveLevel == 8) {
            return new Enemy("Shoggoth", 8, 500, 30, 600);

        } else if (effectiveLevel == 7) {
            int roll = rng.nextInt(5);
            if (roll == 0) return new Enemy("Umbral Colossus", 8, 300, 28, 400);
            if (roll == 1) return new Enemy("Ghoul", 6, 200, 20, 300);
            if (roll == 2) return new Enemy("Palid Dancer", 7, 250, 25, 400);
            return new Enemy("Void Sentinel", 6, 150, 15, 250);

        }else if (effectiveLevel == 6) {
            int roll = rng.nextInt(4);
            if (roll == 0) return new Enemy("Palid Dancer", 7, 250, 25, 400);
            if (roll == 1) return new Enemy("Ghoul", 6, 200, 20, 300);
            if (roll == 2) return new Enemy("Void Sentinel", 6, 150, 15, 250);
            return new Enemy("Chaos Specter", 5, 120, 10, 160);

        } else if (effectiveLevel == 5) {
            int roll = rng.nextInt(3);
            if (roll == 0) return new Enemy("Void Sentinel", 6, 150, 15, 250);
            if (roll == 1) return new Enemy("Chaos Specter", 5, 120, 10, 160);
            return new Enemy("Hollow Knight", 4, 90, 6, 100);

        } else if (effectiveLevel == 4) {
            return rng.nextBoolean() ? new Enemy("Chaos Specter", 5, 120, 10, 160)
                    : new Enemy("Hollow Knight", 4, 90, 6, 100);

        } else if (effectiveLevel == 3) {
            return rng.nextBoolean() ? new Enemy("Pale Warden", 2, 50, 3, 60)
                    : new Enemy("Hollow Knight", 4, 90, 6, 100);

        } else if (effectiveLevel == 2) {
            return rng.nextBoolean() ? new Enemy("Pale Warden", 2, 50, 3, 60)
                    : new Enemy("Wandering Shadow", 1, 25, 1, 30);

        } else {
            return new Enemy("Wandering Shadow", 1, 25, 1, 30);
        }
    }
}
