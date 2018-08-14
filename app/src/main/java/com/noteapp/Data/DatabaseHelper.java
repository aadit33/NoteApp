package com.noteapp.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.noteapp.Model.NoteData;
import com.noteapp.Model.User;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 4;

    // Database Name
    private static final String DATABASE_NAME = "Note.db";

    // User table name
    private static final String TABLE_USER = "users";
    private static final String TABLE_NOTE = "note";

    // User Table Columns names
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USER_EMAIL = "user_email";
    private static final String COLUMN_USER_PASSWORD = "user_password";

    private static final String COLUMN_NOTE_ID = "note_id";
    private static final String COLUMN_NOTE_USER_ID = "note_user_id";
    private static final String COLUMN_NOTE_TITLE = "note_title";
    private static final String COLUMN_NOTE_DESC = "note_desc";
    private static final String COLUMN_NOTE_CREATE_DATE = "note_create_date";
    private static final String COLUMN_NOTE_UPDATE_DATE = "note_update_date";
    private static final String COLUMN_NOTE_IMAGE = "note_image";

    // create table sql query
    private String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_EMAIL + " TEXT,"
            + COLUMN_USER_PASSWORD + " TEXT,"
            + " UNIQUE(" + COLUMN_USER_EMAIL
            + ")ON CONFLICT REPLACE );";


    // create table sql query
    private String CREATE_NOTE_TABLE = "CREATE TABLE " + TABLE_NOTE + "("
            + COLUMN_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NOTE_USER_ID + " TEXT,"
            + COLUMN_NOTE_TITLE + " TEXT,"
            + COLUMN_NOTE_DESC + " TEXT,"
            + COLUMN_NOTE_CREATE_DATE + " TEXT,"
            + COLUMN_NOTE_IMAGE + " TEXT,"
            + COLUMN_NOTE_UPDATE_DATE + " TEXT" + ");";


    // drop table sql query
    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;
    private String DROP_USER_NOTE = "DROP TABLE IF EXISTS " + TABLE_NOTE;

    /**
     * Constructor
     *
     * @param context
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_NOTE_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //Drop User Table if exist
        db.execSQL(DROP_USER_TABLE);
        db.execSQL(DROP_USER_NOTE);

        // Create tables again
        onCreate(db);

    }

    /**
     * This method is to create user record
     *
     * @param user
     */
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());

        // Inserting Row
        db.insert(TABLE_USER, null, values);
        db.close();
    }

    public void addNote(NoteData noteData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_USER_ID, noteData.getUserId());
        values.put(COLUMN_NOTE_TITLE, noteData.getTitle());
        values.put(COLUMN_NOTE_DESC, noteData.getContent());
        values.put(COLUMN_NOTE_CREATE_DATE, noteData.getCreationDate());
        values.put(COLUMN_NOTE_UPDATE_DATE, noteData.getLastUpdateDate());
        values.put(COLUMN_NOTE_IMAGE, noteData.getSnap());

        // Inserting Row
        db.insert(TABLE_NOTE, null, values);
        db.close();
    }


    /**
     * This method to check user exist or not
     *
     * @param email
     * @return true/false
     */
    public boolean checkUser(String email) {

        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = COLUMN_USER_EMAIL + " = ?";

        // selection argument
        String[] selectionArgs = {email};

        // query user table with condition
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com';
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        if (cursorCount > 0) {
            return true;
        }

        return false;
    }

    /**
     * This method to check user exist or not
     *
     * @param email
     * @param password
     * @return true/false
     */
    public boolean checkUser(String email, String password) {

        // array of columns to fetch
        String[] columns = {COLUMN_USER_ID};
        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_USER_EMAIL + " = ?" + " AND " + COLUMN_USER_PASSWORD + " = ?";

        // selection arguments
        String[] selectionArgs = {email, password};

        // query user table with conditions
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com' AND user_password = 'qwerty';
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);                      //The sort order

        int cursorCount = cursor.getCount();

        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }

        return false;
    }

    public String getUserId(String email) {

        String userId = null;
        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = COLUMN_USER_EMAIL + " = ?";

        // selection argument
        String[] selectionArgs = {email};

        // query user table with condition
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com';
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                userId = cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID));

                // Adding user   record to list
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();


        return userId;
    }

    public ArrayList<NoteData> getAllNotes() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_NOTE_ID,
                COLUMN_NOTE_TITLE,
                COLUMN_NOTE_DESC,
                COLUMN_NOTE_CREATE_DATE,
                COLUMN_NOTE_IMAGE
        };
        // sorting orders
        String sortOrder =
                COLUMN_NOTE_ID + " ASC";
        ArrayList<NoteData> noteList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();


        Cursor cursor = db.query(TABLE_NOTE, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                NoteData note = new NoteData();
                note.setNoteId((cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_ID))));
                note.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_TITLE)));
                note.setContent(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_DESC)));
                note.setCreationDate(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_CREATE_DATE)));
                note.setSnap(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_IMAGE)));
                // Adding user record to list
                noteList.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return user list
        return noteList;
    }


    public void upDateNoti(String title, String content, String id,String img) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NOTE_TITLE, title);
        contentValues.put(COLUMN_NOTE_DESC, content);
        contentValues.put(COLUMN_NOTE_IMAGE, img);
        db.update(TABLE_NOTE, contentValues, COLUMN_NOTE_ID + "=" + id, null);
        db.close();

    }

    public void deleteNotifications( String notiID) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTE, COLUMN_NOTE_ID + " = ?",
                new String[]{notiID});
        db.close();

    }



}
