package com.example.healthyolder.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class GoodsItem implements Parcelable {
    public String img;
    public int id;
    public int typeId;
    public String name;
    public String typeName;
    public String intro;
    public int count;

    public GoodsItem(int id, String name, int typeId, String typeName, String img, String intro) {
        this.id = id;
        this.name = name;
        this.typeId = typeId;
        this.typeName = typeName;
        this.img = img;
        this.intro = intro;
    }

    private static ArrayList<GoodsItem> goodsList;
    private static ArrayList<GoodsItem> typeList;

    protected GoodsItem(Parcel in) {
        id = in.readInt();
        typeId = in.readInt();
        name = in.readString();
        typeName = in.readString();
        count = in.readInt();
        img = in.readString();
        intro = in.readString();
    }

    public static final Creator<GoodsItem> CREATOR = new Creator<GoodsItem>() {
        @Override
        public GoodsItem createFromParcel(Parcel in) {
            return new GoodsItem(in);
        }

        @Override
        public GoodsItem[] newArray(int size) {
            return new GoodsItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(typeId);
        parcel.writeString(name);
        parcel.writeString(typeName);
        parcel.writeInt(count);
        parcel.writeString(img);
        parcel.writeString(intro);
    }

}
