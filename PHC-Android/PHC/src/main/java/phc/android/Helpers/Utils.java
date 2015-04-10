package phc.android.Helpers;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility methods for use in multiple fragments and activities.
 */

public class Utils {

    /**
     * Takes in the name of a salesforce object attribute and returns the human-
     * readable version. Truncates the last 3 characters to get rid of "__c" and
     * replaces remaining underscores ("_") with spaces (" ").
     *
     * @param columnName: the salesforce column name to be converted
     * @return human readable version of columnName
     */
    public static String fieldNameHelper(String columnName) {
        columnName = columnName.substring(0, columnName.length()-3);
        columnName = columnName.replace("_", " ");
        return columnName;
    }

    /**
     * Converts display name to Salesforce name.
     * @param displayName display name of service
     * @return salesforce name of service
     */
    public static String fieldNameHelperReverse(String displayName) {
        displayName = displayName.replace(" ", "_");
        return displayName + "__c";
    }

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * Generate a value suitable for id retrieval / creation
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
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

}
