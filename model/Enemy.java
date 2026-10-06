package skytower.model;

import java.util.Random;

public class Enemy {
    private final String name;
    private final int level;
    private int hp;
    private final int maxHp;
    private final int damage;
    private final int xpReward;
    private boolean hasPotion;
    private static final Random rng = new Random();

    public Enemy(String name, int level, int hp, int damage, int xpReward) {
        this.name = name;
        this.level = level;
        this.hp = hp;
        this.maxHp = hp;
        this.damage = damage;
        this.xpReward = xpReward;
        this.hasPotion = rng.nextDouble() < 0.30;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public void takeDamage(int amount) {
        this.hp = Math.max(0, this.hp - amount);
    }

    // Enemy uses position if there is one and HP<30%
    public String performAction() {
        if (hasPotion && hp < maxHp * 0.3) {
            int healAmount = (int) (maxHp * 0.3);
            hp = Math.min(maxHp, hp + healAmount);
            hasPotion = false;
            return name + " drinks a healing potion and restores " + healAmount + " HP!";
        }
        return null;
    }

    public String getName() { return name; }
    public int getLevel() { return level; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getDamage() { return damage; }
    public int getXpReward() { return xpReward; }
}
