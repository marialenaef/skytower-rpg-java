package skytower.world;

import skytower.model.Player;
import skytower.model.LightLevel;
import static skytower.model.Resource.HP;

public class TrapSection extends Section {
    private int lastDamageDealt = 0;
    public TrapSection() {
        super("Hidden Trap", 4); }

    @Override
    public void interact(Player player, LightLevel lightLevel) {
        if (!completed) {
            player.drain(skytower.model.Resource.LIGHT, 4);
            int hpBefore = player.get(HP);
            int rawDamage = (int) (player.get(HP) * 0.10);
            if (rawDamage == 0){
                player.takeDamage(hpBefore);
            }
            else {
                player.takeDamage(rawDamage);
            }
            lastDamageDealt = hpBefore - player.get(HP);
            completed = true;
        }
    }

    @Override
    public String toString() {
        if (lastDamageDealt > 0) {
            return "You fell into a Hidden Trap! You lost " + lastDamageDealt +"HP.";
        }
        return null;
    }
}
