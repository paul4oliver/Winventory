package org.snhu.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class InventoryDatabase extends SQLiteOpenHelper {

    private static final String DB_Name = "inventory.db";
    private static final int VERSION = 3;

    // Create version 1 of database stored in inventory.db
    public InventoryDatabase(Context context) {
        super(context, DB_Name, null, VERSION);
    }

    // Table for item data
    private static final class ItemTable {
        private static final String TABLE = "items";
        private static final String COL_ID = "_id";
        private static final String COL_NAME = "name";
        private static final String COL_QUANTITY = "quantity";
        private static final String COL_DESC= "description";
    }

    // Table for user data
    private static final class UserTable {
        private static final String UTABLE = "users";
        private static final String COL_ID = "_id";
        private static final String COL_UNAME = "username";
        private static final String COL_UPASS= "password";
        private static final String COL_PHONO = "phone_number";
    }

    // Create table with 4 columns for item ID,name, quantity, and description
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + ItemTable.TABLE + " (" +
                ItemTable.COL_ID + " integer primary key autoincrement, " +
                ItemTable.COL_NAME + " text, " +
                ItemTable.COL_QUANTITY + " text, " +
                ItemTable.COL_DESC + " text)");

        db.execSQL("create table " + UserTable.UTABLE + " (" +
                UserTable.COL_ID + " integer primary key autoincrement, " +
                UserTable.COL_UNAME + " text, " +
                UserTable.COL_UPASS + " text, " +
                UserTable.COL_PHONO + " text)");

        ContentValues values = new ContentValues();
        values.put(UserTable.COL_UNAME, "orange");
        values.put(UserTable.COL_UPASS, "red");
        values.put(UserTable.COL_PHONO, "blue");
        db.insert(UserTable.UTABLE, null, values);
    }

    // Method to add an item to the inventory db
    public long addItem(String name, String quantity, String description) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ItemTable.COL_NAME, name);
        values.put(ItemTable.COL_QUANTITY, quantity);
        values.put(ItemTable.COL_DESC, description);

        long itemId = db.insert(ItemTable.TABLE, null, values);

        return itemId;
    }

    // Method to add a user to the inventory db
    public boolean addUser(String username, String password, String phoneNumber) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserTable.COL_UNAME, username);
        values.put(UserTable.COL_UPASS, password);
        values.put(UserTable.COL_PHONO, phoneNumber);
        db.insert(UserTable.UTABLE, null, values);
        return true;
    }

    // Method to read all items in the inventory db
    public ArrayList<User> readUsers()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        // Create cursor for items in inventory db
        Cursor cursorUsers = db.rawQuery("SELECT * FROM " + UserTable.UTABLE, null);


        ArrayList<User> UserArrayList = new ArrayList<>();

        if (cursorUsers.moveToFirst()) {
            do {
                UserArrayList.add(new User(
                        cursorUsers.getLong(0),
                        cursorUsers.getString(1),
                        cursorUsers.getString(2),
                        cursorUsers.getString(3)));
            } while (cursorUsers.moveToNext());
        }

        cursorUsers.close();

        return UserArrayList;
    }

    // Method to read all items in the inventory db
    public ArrayList<Item> readItems()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        // Create cursor for items in inventory db
        Cursor cursorItems = db.rawQuery("SELECT * FROM " + ItemTable.TABLE, null);


        ArrayList<Item> ItemArrayList = new ArrayList<>();

        if (cursorItems.moveToFirst()) {
            do {
                ItemArrayList.add(new Item(
                        cursorItems.getLong(0),
                        cursorItems.getString(1),
                        cursorItems.getString(2),
                        cursorItems.getString(3)));
            } while (cursorItems.moveToNext());
        }

        cursorItems.close();

        return ItemArrayList;
    }

    // Method to get a specific item from the inventory db
    public Item getItem(long id){
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + ItemTable.TABLE + " WHERE "
                + ItemTable.COL_ID + " = " + id;

        Cursor result = db.rawQuery(selectQuery, null);

        if(result != null){
            result.moveToFirst();
        }

        Item rItem = new Item();
        rItem.setId(result.getLong(result.getColumnIndexOrThrow(ItemTable.COL_ID)));
        rItem.setName(result.getString(result.getColumnIndexOrThrow(ItemTable.COL_NAME)));
        rItem.setCount(result.getString(result.getColumnIndexOrThrow(ItemTable.COL_QUANTITY)));
        rItem.setDescription(result.getString(result.getColumnIndexOrThrow(ItemTable.COL_DESC)));
        result.close();

        return rItem;
    }

    // Method to update an item from the inventory db
    public boolean updateItem(long id, String name, String quantity, String description) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ItemTable.COL_NAME, name);
        values.put(ItemTable.COL_QUANTITY, quantity);
        values.put(ItemTable.COL_DESC, description);

        int rowsUpdated = db.update(ItemTable.TABLE, values, "_id = ?",
                new String[] { Float.toString(id) });

        return rowsUpdated > 0;
    }

    // Method to delete an item from the inventory db
    public boolean deleteItem(long id) {
        SQLiteDatabase db = getWritableDatabase();
        int rowsDeleted = db.delete(ItemTable.TABLE, ItemTable.COL_ID + " = ?",
                new String[] { Long.toString(id) });
        return rowsDeleted > 0;
    }

    // Method to get a specific user from the inventory db
    public User getUser(String uName){
        SQLiteDatabase db = this.getReadableDatabase();
        User pUser = new User();
        String userQuery = "SELECT * FROM users WHERE username = ?";
        Cursor result = db.rawQuery(userQuery, new String [] {uName});

        if(result != null && result.moveToFirst()){
            pUser.setUsername(result.getString(result.getColumnIndexOrThrow(UserTable.COL_UNAME)));
            pUser.setUsernameMatch(result.getString(result.getColumnIndexOrThrow(UserTable.COL_UPASS)));
            pUser.setPhoneNumber(result.getString(result.getColumnIndexOrThrow(UserTable.COL_PHONO)));
        }

        result.close();

        return pUser;
    }

    // Method to check if table already exists
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL("drop table if exists " + ItemTable.TABLE);
        db.execSQL("drop table if exists " + UserTable.UTABLE);
        onCreate(db);
    }
}
