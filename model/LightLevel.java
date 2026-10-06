package skytower.model;

public enum LightLevel {
    BRIGHT(0, 1.00, 1.0, 1.00, 0.00),
    DIM(1, 0.85, 0.9, 0.85, 0.10),
    DARK(2, 0.65, 0.7, 0.65, 0.20),
    PITCH_BLACK(3, 0.40, 0.5, 0.40, 0.30);

    private final int enemyLevelBonus;
    private final double staminaMultiplier;
    private final double restEffectiveness;
    private final double resourceMultiplier;
    private final double surpriseEncounterChance;

    LightLevel(int enemyLevelBonus, double staminaMultiplier, double restEffectiveness,
               double resourceMultiplier, double surpriseEncounterChance) {
        this.enemyLevelBonus = enemyLevelBonus;
        this.staminaMultiplier = staminaMultiplier;
        this.restEffectiveness = restEffectiveness;
        this.resourceMultiplier = resourceMultiplier;
        this.surpriseEncounterChance = surpriseEncounterChance;
    }

    public static LightLevel fromLightValue(int light) {
        if (light >= 75) return BRIGHT;
        if (light >= 50) return DIM;
        if (light >= 25) return DARK;
        return PITCH_BLACK;
    }
    // GETTERS
    public int getEnemyLevelBonus() { return enemyLevelBonus; }
    public double getStaminaMultiplier() { return staminaMultiplier; }
    public double getRestEffectiveness() { return restEffectiveness; }
    public double getResourceMultiplier() { return resourceMultiplier; }
    public double getSurpriseEncounterChance() { return surpriseEncounterChance; }
}
