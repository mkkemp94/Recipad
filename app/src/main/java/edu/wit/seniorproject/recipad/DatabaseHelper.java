package edu.wit.seniorproject.recipad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by kempm on 7/8/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
    //DATABASE NAME
    public static final String DATABASE_NAME = "Recipad";

    public static final int DATABASE_VERSION = '3';

    //TABLE NAMES
    public static final String TABLE_GROCERY_LIST = "grocery_list";
    public static final String TABLE_INVENTORY_LIST = "inventory_list";
    public static final String TABLE_RECIPE_LIST = "recipe_list";
    public static final String TABLE_SAVED = "saved_list";

    //COLUMNS
    public static final String GROCERY_ID = "ID";
    public static final String GROCERY_NAME = "NAME";
    public static final String GROCERY_QUANTITY = "QTY";
    public static final String GROCERY_EXPIRATION = "EXP";

    public static final String INVENTORY_ID = "ID";
    public static final String INVENTORY_NAME = "NAME";
    public static final String INVENTORY_QUANTITY = "QTY";
    public static final String INVENTORY_EXPIRATION = "EXP";

    public static final String RECIPE_ID = "ID";
    public static final String RECIPE_NAME = "NAME";
    public static final String RECIPE_TIME = "TIME";
    public static final String RECIPE_INGREDIENTS = "INGREDIENTS";
    public static final String RECIPE_INSTRUCTIONS = "INSTRUCTIONS";

    public static final String SAVED_ID = "ID";
    public static final String SAVED_NAME = "NAME";
    public static final String SAVED_QUANTITY = "QTY";
    public static final String SAVED_EXPIRATION = "EXP";

    //CREATE COMMANDS
    static final String CREATE_TB_GROCERY = "create table " + TABLE_GROCERY_LIST +
            " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, QTY INTEGER, EXP STRING)";

    static final String CREATE_TB_INVENTORY = "create table " + TABLE_INVENTORY_LIST +
            " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, QTY INTEGER, EXP STRING)";

    static final String CREATE_TB_RECIPE = "create table " + TABLE_RECIPE_LIST +
            " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, TIME STRING, INGREDIENTS STRING, INSTRUCTIONS STRING)";

    static final String CREATE_TB_SAVED = "create table " + TABLE_SAVED +
            " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, QTY INTEGER, EXP STRING)";


    SQLiteDatabase db;
    DatabaseHelper helper;

    /**
     * Constructor
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //helper = new DatabaseHelper(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        try
        {
            /* Creates a table in database */
            db.execSQL(CREATE_TB_GROCERY);
            db.execSQL(CREATE_TB_INVENTORY);
            db.execSQL(CREATE_TB_RECIPE);
            db.execSQL(CREATE_TB_SAVED);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //UPGRADE THE DATABASE
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.w("DBAdapter", "Upgrading DB");

        /* Drops table and recreates it (upgrade) */
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROCERY_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTORY_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVED);

        onCreate(db);
    }

    // OPEN THE DB
    public DatabaseHelper openDB()
    {
        try
        {
            // OPEN THE DB
            db = getWritableDatabase();  //helper.

        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        return this;
    }

    // CLOSE THE DATABASE
    public void close()
    {
        //helper.close();
        // CLOSE THE DATABASE
        db.close();
    }

    // INSERT INTO TABLE
    public long insertGroceryData(String name, String qty, String exp)
    {
        try
        {
            /* Get content values instance and insert data into columns */
            ContentValues contentValues = new ContentValues();
            contentValues.put(GROCERY_NAME, name);
            contentValues.put(GROCERY_QUANTITY, qty);
            contentValues.put(GROCERY_EXPIRATION, exp);

            long result = db.insert(TABLE_GROCERY_LIST, GROCERY_ID, contentValues);

            return result;

        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        return 0;
    }

    public long insertInventoryData(String name, String qty, String exp)
    {
        try
        {
            /* Get content values instance and insert data into columns */
            ContentValues contentValues = new ContentValues();
            contentValues.put(INVENTORY_NAME, name);
            contentValues.put(INVENTORY_QUANTITY, qty);
            contentValues.put(INVENTORY_EXPIRATION, exp);

            long result = db.insert(TABLE_INVENTORY_LIST, INVENTORY_ID, contentValues);

            return result;

        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        return 0;
    }

    public long insertRecipeData(String name, String time, String ingredients, String instructions)
    {
        try
        {
            /* Get content values instance and insert data into columns */
            ContentValues contentValues = new ContentValues();
            contentValues.put(RECIPE_NAME, name);
            contentValues.put(RECIPE_TIME, time);
            contentValues.put(RECIPE_INGREDIENTS, ingredients);
            contentValues.put(RECIPE_INSTRUCTIONS, instructions);

            long result = db.insert(TABLE_RECIPE_LIST, RECIPE_ID, contentValues);

            return result;

        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        return 0;
    }

    public long insertSavedData(String name, String qty, String exp)
    {
        try
        {
            /* Get content values instance and insert data into columns */
            ContentValues contentValues = new ContentValues();
            contentValues.put(SAVED_NAME, name);
            contentValues.put(SAVED_QUANTITY, qty);
            contentValues.put(SAVED_EXPIRATION, exp);

            long result = db.insert(TABLE_SAVED, SAVED_ID, contentValues);

            return result;

        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * GET ALL VALUES
     * But only display names in the list view.
     */
    public Cursor getAllGroceryNames()
    {
        String[] columns = {GROCERY_ID, GROCERY_NAME, GROCERY_QUANTITY, GROCERY_EXPIRATION};

        return db.query(TABLE_GROCERY_LIST, columns, null, null, null, null, null);
    }

    public Cursor getAllInventoryNames()
    {
        String[] columns = {INVENTORY_ID, INVENTORY_NAME, INVENTORY_QUANTITY, INVENTORY_EXPIRATION};

        return db.query(TABLE_INVENTORY_LIST, columns, null, null, null, null, null);
    }

    public Cursor getAllRecipeNames()
    {
        String[] columns = {RECIPE_ID, RECIPE_NAME, RECIPE_TIME, RECIPE_INGREDIENTS, RECIPE_INSTRUCTIONS};

        return db.query(TABLE_RECIPE_LIST, columns, null, null, null, null, null);
    }

    public Cursor getAllSavedNames()
    {
        String[] columns = {SAVED_ID, SAVED_NAME, SAVED_QUANTITY, SAVED_EXPIRATION};

        return db.query(TABLE_SAVED, columns, null, null, null, null, null);
    }

    /**
     *  Gets all data from the database
     */
    public Cursor getAllGroceryData()
    {
        /* Gets all data from the database */
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_GROCERY_LIST, null);
        return res;
    }

    public Cursor getAllInventoryData()
    {
        /* Gets all data from the database */
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_INVENTORY_LIST, null);
        return res;
    }

    public Cursor getAllRecipeData()
    {
        /* Gets all data from the database */
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_RECIPE_LIST, null);
        return res;
    }

    public Cursor getAllSavedData()
    {
        /* Gets all data from the database */
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_SAVED, null);
        return res;
    }

    /*
    public Cursor getAllRows(SQLiteDatabase db) {
        String where = null;
        Cursor c = db.query(true, TABLE_GROCERY_LIST, ALL)
    }
    */

    /**
     * Updates a particular value in the database.
     */
    public boolean updateGroceryData(String id, String name, String qty, String exp)
    {
        /* Get database instance */
        SQLiteDatabase db = this.getWritableDatabase();

        /* Get content values instance and insert data into columns */
        ContentValues contentValues = new ContentValues();
        contentValues.put(GROCERY_NAME, name);
        contentValues.put(GROCERY_QUANTITY, qty);
        contentValues.put(GROCERY_EXPIRATION, exp);

        /* Update the row entry at that id */
        db.update(TABLE_GROCERY_LIST, contentValues, "ID = ?", new String[] {id});

        return true;
    }

    public boolean updateInventoryData(String id, String name, String qty, String exp)
    {
        /* Get database instance */
        SQLiteDatabase db = this.getWritableDatabase();

        /* Get content values instance and insert data into columns */
        ContentValues contentValues = new ContentValues();
        contentValues.put(INVENTORY_NAME, name);
        contentValues.put(INVENTORY_QUANTITY, qty);
        contentValues.put(INVENTORY_EXPIRATION, exp);

        /* Update the row entry at that id */
        db.update(TABLE_INVENTORY_LIST, contentValues, "ID = ?", new String[] {id});

        return true;
    }

    public boolean updateRecipeData(String id, String name, String time, String ingredients, String instructions)
    {
        /* Get database instance */
        SQLiteDatabase db = this.getWritableDatabase();

        /* Get content values instance and insert data into columns */
        ContentValues contentValues = new ContentValues();

        contentValues.put(RECIPE_NAME, name);
        contentValues.put(RECIPE_TIME, time);
        contentValues.put(RECIPE_INGREDIENTS, ingredients);
        contentValues.put(RECIPE_INSTRUCTIONS, instructions);

        /* Update the row entry at that id */
        db.update(TABLE_RECIPE_LIST, contentValues, "ID = ?", new String[] {id});

        return true;
    }

    public boolean updateSavedData(String id, String name, String qty, String exp)
    {
        /* Get database instance */
        SQLiteDatabase db = this.getWritableDatabase();

        /* Get content values instance and insert data into columns */
        ContentValues contentValues = new ContentValues();
        contentValues.put(SAVED_NAME, name);
        contentValues.put(SAVED_QUANTITY, qty);
        contentValues.put(SAVED_EXPIRATION, exp);

        /* Update the row entry at that id */
        db.update(TABLE_SAVED, contentValues, "ID = ?", new String[] {id});

        return true;
    }


    /**
     * Deletes specified rows from database.
     */
    public Integer deleteGroceryItem(String id)
    {
        Log.v("remove", "ID of removed item dbi is: " + id);

        /* Get database instance */
        SQLiteDatabase db = this.getWritableDatabase();

        /* Delete the row entry at that id */
        return db.delete(TABLE_GROCERY_LIST, "ID = ?", new String[] {id});
    }

    public Integer deleteInventoryItem(String id)
    {
        Log.v("remove", "ID of removed item dbi is: " + id);

        /* Get database instance */
        SQLiteDatabase db = this.getWritableDatabase();

        /* Delete the row entry at that id */
        return db.delete(TABLE_INVENTORY_LIST, "ID = ?", new String[] {id});
    }

    public Integer deleteRecipeItem(String id)
    {
        Log.v("remove", "ID of removed item dbi is: " + id);

        /* Get database instance */
        SQLiteDatabase db = this.getWritableDatabase();

        /* Delete the row entry at that id */
        return db.delete(TABLE_RECIPE_LIST, "ID = ?", new String[] {id});
    }

    public Integer deleteSavedItem(String id)
    {
        Log.v("remove", "ID of removed item dbi is: " + id);

        /* Get database instance */
        SQLiteDatabase db = this.getWritableDatabase();

        /* Delete the row entry at that id */
        return db.delete(TABLE_SAVED, "ID = ?", new String[] {id});
    }


    /**
     * Delete all items from database
     */
    public Integer deleteAllGrocery()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        /* Delete the row entry at that id */
        return db.delete(TABLE_GROCERY_LIST, null, null);
    }

    public Integer deleteAllInventory()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        /* Delete the row entry at that id */
        return db.delete(TABLE_INVENTORY_LIST, null, null);
    }

    public Integer deleteAllRecipe()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        /* Delete the row entry at that id */
        return db.delete(TABLE_RECIPE_LIST, null, null);
    }

    public Integer deleteAllSaved()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        /* Delete the row entry at that id */
        return db.delete(TABLE_SAVED, null, null);
    }
}
