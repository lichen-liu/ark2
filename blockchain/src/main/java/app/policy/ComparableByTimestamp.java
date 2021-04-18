package app.policy;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

public interface ComparableByTimestamp {
    /**
     * 
     * @return timestamp String in the format of:
     * 
     *         <pre>
     *         ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT); // "2015-04-14T11:07:36.639Z"
     *         </pre>
     */
    public abstract String getTimestamp();

    public default int compareToByTimestamp(final ComparableByTimestamp other) {
        ZonedDateTime myInstant;
        try {
            myInstant = ZonedDateTime.parse(this.getTimestamp());
        } catch (final DateTimeParseException e) {
            myInstant = ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        }

        ZonedDateTime otherInstant;
        try {
            otherInstant = ZonedDateTime.parse(other.getTimestamp());
        } catch (final DateTimeParseException e) {
            otherInstant = ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        }

        return myInstant.compareTo(otherInstant);
    }
}