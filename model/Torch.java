package skytower.model;

public class Torch extends Consumable {

    private static Runnable onTorchUsedAction;

    public Torch() {
        super("Torch", Rarity.COMMON, 100);
    }

    public static void setOnTorchUsedAction(Runnable action) {
        onTorchUsedAction = action;
    }

    @Override
    public void use(Player player) {
        if (onTorchUsedAction != null) {
            onTorchUsedAction.run();
        }
    }
    @Override
    public String toString() {
        return String.format("[%s] %s (Restores %d LIGHT)", rarity, name, potency);
    }
}