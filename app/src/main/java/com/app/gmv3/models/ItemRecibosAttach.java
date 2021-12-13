package com.app.gmv3.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ItemRecibosAttach implements Parcelable {
    private String imageName;
    private String imageID;

    private boolean mDelete;

    public boolean ismDelete() {
        return mDelete;
    }

    public void setmDelete(boolean mDelete) {
        this.mDelete = mDelete;
    }

    public String getImageName() {
        return imageName;
    }

    public String getImageID() {
        return imageID;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }

    public static final Creator CREATOR = new Creator() {
        public ItemRecibosAttach createFromParcel(Parcel in) {
            return new ItemRecibosAttach(in);
        }

        public ItemRecibosAttach[] newArray(int size) {
            return new ItemRecibosAttach[size];
        }
    };

    public ItemRecibosAttach() {
    }

    public ItemRecibosAttach(Parcel in) {
        super();
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        imageName = in.readString();
        imageID = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageName);
        dest.writeString(imageID);
    }
}