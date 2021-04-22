package app.policy;

import org.apache.commons.math3.distribution.BetaDistribution;

public class LikeRewarding {
    private final long numberLikes;
    private final double basePointAmount;

    private final BetaDistribution likerRewardingDistribution = new BetaDistribution(2, 2);

    public static final double splitToAuthorRatio = 0.5;
    public static final double inflationRate = 0.02;

    public long getNumberLikes() {
        return numberLikes;
    }

    public double getBasePointAmount() {
        return basePointAmount;
    }

    public LikeRewarding(final long existingNumberLikes, final double basePointAmount) {
        this.numberLikes = existingNumberLikes;
        this.basePointAmount = basePointAmount;
    }

    public double determineAuthorRewarding() {
        double economyGrowthRatio = 0.0;
        if (this.numberLikes > 0) {
            economyGrowthRatio = inflationRate * Math.log(this.numberLikes);
        }
        return this.basePointAmount * (splitToAuthorRatio + economyGrowthRatio);
    }

    /**
     * 
     * @param likerRank only for existing likers
     * @return
     */
    public boolean isLikerRewarded(final long likerRank) {
        return likerRank * 2 < this.numberLikes;
    }

    /**
     * 
     * @param likerRank only for existing likers
     * @return
     */
    public double determineLikerRewarding(long likerRank) {
        likerRank = Math.min(likerRank, this.numberLikes - 1);
        if (this.isLikerRewarded(likerRank)) {
            final double ratio = this.likerRewardingDistribution.probability(
                    0.5 + (((double) likerRank) / (double) this.numberLikes),
                    0.5 + (((double) likerRank + 1) / (double) this.numberLikes));
            return this.basePointAmount * ratio;
        } else {
            return 0;
        }
    }
}
