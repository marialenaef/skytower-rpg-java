package skytower.model;

public enum Rarity {
    FEEBLE(1),
    COMMON(3),
    RARE(6),
    SUPREME(10);

    private final int bonusValue;

    Rarity(int bonusValue) {
        this.bonusValue = bonusValue;
    }

    public int getBonusValue() {
        return bonusValue;
    }
}
