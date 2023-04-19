package org.snhu.inventoryapp;

public class User {

    private Long mId;
    private String mUsername;
    private String mUsernameMatch;
    private String mPhoneNumber;

    public User() {}

    public User(Long id, String username, String usernameMatch, String phoneNumber) {
        mId = id;
        mUsername = username;
        mUsernameMatch = usernameMatch;
        mPhoneNumber = phoneNumber;

    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        this.mId = id;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        this.mUsername = username;
    }

    public String getUsernameMatch() {
        return mUsernameMatch;
    }

    public void setUsernameMatch(String usernameMatch) {
        this.mUsernameMatch = usernameMatch;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.mPhoneNumber = phoneNumber;
    }
}
