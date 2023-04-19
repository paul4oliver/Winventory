package org.snhu.inventoryapp;

public class Item {
    private Long mId;
    private String mName;
    private String mCount;
    private String mDescription;

    public Item() {}

    public Item(Long id, String name, String count, String description) {
        mId = id;
        mName = name;
        mCount = count;
        mDescription = description;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        this.mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getCount() {
        return mCount;
    }

    public void setCount(String count) {
        this.mCount = count;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }
}
