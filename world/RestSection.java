package skytower.world;

import skytower.model.Player;
import skytower.model.LightLevel;
import skytower.model.Resource;

public class RestSection extends Section {
    private int lastHealAmount = 0;
    private boolean restedSuccessfully = false;
    private boolean lightDrained = false;

    public RestSection() {
        super("Campfire", 2);
    }

    @Override
    public void interact(Player player, LightLevel lightLevel) {
        completed = true;
        if (!lightDrained) {
            player.drain(Resource.LIGHT, 2);
            lightDrained = true;
        }
    }

    public void rest (Player player, LightLevel lightLevel) {
        int foodCurrent = player.get(Resource.FOOD);
        if (foodCurrent >= 2) {
            player.drain(Resource.FOOD, 2);

            int baseHeal = 10;
            int healAmount = (int) (baseHeal * lightLevel.getRestEffectiveness());
            lastHealAmount = healAmount;

            int maxHP = player.getMax(Resource.HP);
            int restoreHP = (int) (maxHP * (healAmount / 100.0));
            player.restore(Resource.HP, restoreHP);

            int maxStamina = player.getMax(Resource.STAMINA);
            int restoreStamina = (int) (maxStamina * (healAmount / 100.0));
            player.restore(Resource.STAMINA, restoreStamina);

            int maxMANA = player.getMax(Resource.MANA);
            int restoreMANA = (int) (maxMANA * (healAmount / 100.0));
            player.restore(Resource.MANA, restoreMANA);

            restedSuccessfully = true;
            completed = true;
        } else {
            restedSuccessfully = false;
        }
    }

    @Override
    public String toString() {
        if (restedSuccessfully) {
            return "You lit a campfire! You recovered " + lastHealAmount + " % HP, Stamina and Mana.";
        }
        return "Campfire (Not enough food for rest!)";
    }
}