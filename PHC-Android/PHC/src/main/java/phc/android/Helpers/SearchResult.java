package phc.android.Helpers;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Java class to encapsulate the responses received from the rails server for search results.
 *
 * Created by Nishant on 11/23/14.
 */
public class SearchResult implements Parcelable{

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeString(salesForceId);
        parcel.writeSerializable(birthday);
    }
}
