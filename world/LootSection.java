package skytower.world;

import skytower.model.Player;
import skytower.model.LightLevel;
import skytower.model.Item;
import skytower.model.ItemGenerator;
import java.util.Random;
import skytower.model.Rarity;

public class LootSection extends Section {
    Item randomLoot;
    Rarity rarity;
    private static final Random rng = new Random();
    public LootSection() {
        super("Loot", 4);
    }
    @Override
    public void interact(Player player, LightLevel lightLevel) {
        if (!completed) {
            player.drain(skytower.model.Resource.LIGHT, 4);
            rarity = switch (lightLevel) {
                case BRIGHT -> Rarity.FEEBLE;
                case DIM -> Rarity.COMMON;
                case DARK -> Rarity.RARE;
                case PITCH_BLACK -> Rarity.SUPREME;
            };
            int roll =rng.nextInt(2);
            if (roll == 0) {
                this.randomLoot = ItemGenerator.generateRandomWeapon(rarity);
            }
            else {this.randomLoot = ItemGenerator.generateRandomArmor(rarity);}

            completed = true;
        }
    }


    public Item getGroundItem() {
        return randomLoot;
    }

    public void setGroundItem(Item groundItem) {
        this.randomLoot = groundItem;
    }
}
