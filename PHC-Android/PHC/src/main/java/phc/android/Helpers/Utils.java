package phc.android.Helpers;

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

}
