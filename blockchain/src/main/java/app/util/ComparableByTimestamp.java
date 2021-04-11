package app.util;

public interface ComparableByTimestamp {
    /**
     * 
     * @return timestamp String in the format of:
     * 
     *         <pre>
     *         ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT); // "2015-04-14T11:07:36.639Z"
     *         </pre>
     */
    public abstract String getTimestampString();

    public default int compareToByTimestamp(final ComparableByTimestamp other) {
        return getTimestampString().compareToIgnoreCase(other.getTimestampString());
    }
}