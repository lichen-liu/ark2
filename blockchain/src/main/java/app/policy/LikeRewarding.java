package app.policy;

import org.apache.commons.math3.distribution.BetaDistribution;

public class LikeRewarding {
    private final long numberLikes;
    private final long totalNumberLikes;
    private final double basePointAmount;

    private final BetaDistribution likerRewardingDistribution = new BetaDistribution(2, 2);

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

    public LikeRewarding(final long existingNumberLikes, final long existingTotalNumberLikes,
            final double basePointAmount) {
        this.numberLikes = existingNumberLikes + 1;
        this.totalNumberLikes = existingTotalNumberLikes + 1;
        this.basePointAmount = basePointAmount;
    }

    public double determineAuthorRewarding() {
        final double ratio = (splitToAuthorRatio + inflationRate * Math.log(this.totalNumberLikes));
        return this.basePointAmount * ratio;
    }

    public boolean isLikerRewarded(final long likerRank) {
        return likerRank * 2 < this.numberLikes;
    }

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
