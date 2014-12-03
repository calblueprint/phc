package phc.android;

import java.util.Date;

/**
 * Java class to encapsulate the responses received from the rails server for search results.
 *
 * Created by Nishant on 11/23/14.
 */
public class SearchResult {

    private String firstName;
    private String lastName;
    private String salesForceId;
    private Date birthday;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSalesForceId() {
        return salesForceId;
    }

    public void setSalesForceId(String salesForceId) {
        this.salesForceId = salesForceId;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }



}
