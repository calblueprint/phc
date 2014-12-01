package phc.android;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility methods for use in multiple fragments and activities. Currently contains:
 * 1. generateViewId(): generate unique integer ID for programmatically created view.
 * 2. keyToKeyConverter(): converts a view's string ID into SalesForce format.
 */
public class Utils {
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * Generate a unique ID.
     * This value will not collide with ID values generated at build time by aapt for R.id.
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    /**
     * Takes in a string ID and converts it to key format
     * used in the SalesForce database via the following changes:
     *   - add "__c" to the end of the field
     *   - replace nonalphanumeric characters (e.g. "/" and "-") with "_".
     */
    public String keyToKeyConverter(String key){
        key = key.replaceAll("[\\W\\s]","_") + "__c";
        return key;
    }
}
