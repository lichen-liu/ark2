package app.policy;

@FunctionalInterface
public interface ComparableByRelativeOrder {
    /**
     * 
     * @return relative order (monotonic) within the same objectTypeName and/or
     *         keyForFiltering
     */
    public abstract long getRelativeOrder();

    public default int compareToByRelativeOrder(final ComparableByRelativeOrder other) {
        return Long.compare(getRelativeOrder(), other.getRelativeOrder());
    }
}