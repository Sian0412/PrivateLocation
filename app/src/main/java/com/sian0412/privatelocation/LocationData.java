package com.sian0412.privatelocation;

import android.os.Parcel;
import android.os.Parcelable;

public class LocationData implements Parcelable {
    public String name;
    public String address;
    public String phone;
    public String remarkColumn;

    public static final Creator<LocationData> CREATOR = new Creator<LocationData>() {
        @Override
        public LocationData createFromParcel(Parcel in) {
            LocationData mLocationData = new LocationData();
            mLocationData.name = in.readString();
            mLocationData.address = in.readString();
            mLocationData.phone = in.readString();
            mLocationData.remarkColumn = in.readString();
            return mLocationData;
        }

        @Override
        public LocationData[] newArray(int size) {
            return new LocationData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(phone);
        dest.writeString(remarkColumn);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRemarkColumn() {
        return remarkColumn;
    }

    public void setRemarkColumn(String remarkColumn) {
        this.remarkColumn = remarkColumn;
    }
}
