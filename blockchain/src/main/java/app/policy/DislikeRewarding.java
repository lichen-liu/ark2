package app.policy;

import org.apache.commons.math3.distribution.BetaDistribution;

public class DislikeRewarding {
    private final long numberDislikes;
    private final double dislikerPointAmount;
    private final double authorPointAmount;

    private final BetaDistribution likerRewardingDistribution = new BetaDistribution(2, 2);

    public static final double inflationRate = 0.01;

    public long getNumberDislikes() {
        return numberDislikes;
    }

    public double getBasePointAmount() {
        return dislikerPointAmount;
    }

    public double getAuthorPointAmount() {
        return authorPointAmount;
    }

    public DislikeRewarding(final long existingNumberDislikes, final double dislikerPointAmount,
            final double authorPointAmount) {
        this.numberDislikes = existingNumberDislikes;
        this.dislikerPointAmount = dislikerPointAmount;
        this.authorPointAmount = authorPointAmount;
    }

    public double determineAuthorPenalty() {
        double economyDeclineRatio = inflationRate * Math.log(this.numberDislikes + 1);
        economyDeclineRatio = Math.min(1, economyDeclineRatio);
        final double penaltyRatio = economyDeclineRatio;
        return this.authorPointAmount * penaltyRatio;
    }

    /**
     * 
     * @param dislikerRank only for existing dislikers
     * @return
     */
    public boolean isDislikerRewarded(final long dislikerRank) {
        return dislikerRank * 2 < this.numberDislikes;
    }

    /**
     * 
     * @param dislikerRank only for existing dislikers
     * @return
     */
    public double determineLikerRewarding(long dislikerRank) {
        dislikerRank = Math.min(dislikerRank, this.numberDislikes - 1);
        if (this.isDislikerRewarded(dislikerRank)) {
            double economyDeclineRatio = inflationRate * Math.log(this.numberDislikes + 1 + inflationRate);
            economyDeclineRatio = Math.min(1, economyDeclineRatio);
            final double dislikerDistributionRatio = 2 * this.likerRewardingDistribution.probability(
                    0.5 + (((double) dislikerRank) / (double) this.numberDislikes),
                    0.5 + (((double) dislikerRank + 1) / (double) this.numberDislikes));
            return this.dislikerPointAmount * (1 - economyDeclineRatio) * dislikerDistributionRatio;
        } else {
            return 0;
        }
    }
}
