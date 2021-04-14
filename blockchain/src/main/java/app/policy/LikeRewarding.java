package app.policy;

public class LikeRewarding {
    private final long numberLikes;
    private final long totalNumberLikes;
    private final double basePointAmount;

    public static final double splitToAuthorRatio = 0.5;
    public static final double inflationRate = 0.02;

    public long getNumberLikes() {
        return numberLikes;
    }

    public long getGlobalNumberLikes() {
        return totalNumberLikes;
    }

    public double getBasePointAmount() {
        return basePointAmount;
    }

    public LikeRewarding(final long numberLikes, final long totalNumberLikes, final double basePointAmount) {
        this.numberLikes = numberLikes;
        this.totalNumberLikes = totalNumberLikes;
        this.basePointAmount = basePointAmount;
    }

    public double determineAuthorRewarding() {
        return this.basePointAmount * (splitToAuthorRatio + inflationRate * Math.log(this.totalNumberLikes));
    }

    /// TODO: beta distribution
}
