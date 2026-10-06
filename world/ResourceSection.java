package skytower.world;

import skytower.model.Player;
import skytower.model.LightLevel;
import skytower.model.Resource;
import java.util.Random;

public class ResourceSection extends Section {
    private static final Random rng = new Random();
    private int lastAmountGained = 0;
    private String lastResourceRestored = "";
    public ResourceSection() {
        super("Supply Cache", 4);
    }

    @Override
    public void interact(Player player, LightLevel lightLevel) {
        if (!completed) {
            player.drain(skytower.model.Resource.LIGHT, 4);
            replenish(5, 10, player, lightLevel);
            completed = true;
        }
        else if (rng.nextDouble() <0.5) replenish(2, 5, player, lightLevel);
        else {
            lastAmountGained = 0;
            lastResourceRestored = "";
        }
    }
    public void replenish (int food, int light, Player player, LightLevel lightLevel) {
        if (rng.nextDouble() >0.7) {
            lastAmountGained = (int) (food * lightLevel.getResourceMultiplier());
            if (lastAmountGained > (player.getMax(Resource.FOOD) - player.get(Resource.FOOD))) {
                lastAmountGained = player.getMax(Resource.FOOD) - player.get(Resource.FOOD);
            }
            player.restore(Resource.FOOD, lastAmountGained);
            lastResourceRestored = "Food";
        }
        else{
            lastAmountGained = (int) (light * lightLevel.getResourceMultiplier());
            if (lastAmountGained > (player.getMax(Resource.LIGHT) - player.get(Resource.LIGHT))) {
                lastAmountGained = player.getMax(Resource.LIGHT) - player.get(Resource.LIGHT);
            }
            player.restore(Resource.LIGHT, lastAmountGained);
            lastResourceRestored = "Light";
        }
    }

    @Override
    public String toString(){
        if (lastAmountGained > 0) {
            return "You found supplies (Supply Cache)! You recovered +" + lastAmountGained + " " + lastResourceRestored + ".";
        }
        else return "You didn't find any supplies!";
    }
}
