package app.policy;

public class LikeRewarding {
    private final long numberLikes;
    private final double basePointAmount;

    private static final double splitToAuthorRatio = 0.5;
    private static final double inflationRate = 0.02;

    public long getNumberLikes() {
        return numberLikes;
    }

    public double getBasePointAmount() {
        return basePointAmount;
    }

    public LikeRewarding(long numberLikes, double basePointAmount) {
        this.numberLikes = numberLikes;
        this.basePointAmount = basePointAmount;
    }

    public double determineAuthorRewarding() {
        return this.basePointAmount * (splitToAuthorRatio + inflationRate * Math.log(this.numberLikes));
    }

}
