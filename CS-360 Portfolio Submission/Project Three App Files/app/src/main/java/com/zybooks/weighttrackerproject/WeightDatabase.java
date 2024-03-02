package com.zybooks.weighttrackerproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class WeightDatabase extends SQLiteOpenHelper {

    private static final String DB_NAME = "weights.db";
    private static final int VERSION = 1;

    public WeightDatabase(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    private static final class LoginTable {
        private static final String TABLE = "login";
        private static final String COL_USERNAME = "username";
        private static final String COL_PASSWORD = "password";
    }

    private static final class GoalWeightTable {
        private static final String TABLE = "goal_weight";
        private static final String ID = "_id";
        private static final String GOAL_LB = "goal_lb";
        private static final String GOAL_KG = "goal_kg";
    }

    private static final class WeightTable {
        private static final String TABLE = "weights";
        private static final String ID = "_id";
        private static final String WEIGHT_LB = "weight_lb";
        private static final String WEIGHT_KG = "weight_kg";
        private static final String DATE = "date";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the login table
        db.execSQL("CREATE TABLE " + LoginTable.TABLE + " (" +
                LoginTable.COL_USERNAME + " TEXT, " +
                LoginTable.COL_PASSWORD + " TEXT)");

        // Create the goal weight table
        db.execSQL("CREATE TABLE " + GoalWeightTable.TABLE + " (" +
                GoalWeightTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                GoalWeightTable.GOAL_LB + " FLOAT, " +
                GoalWeightTable.GOAL_KG + " FLOAT)");

        // Create the weights table
        db.execSQL("CREATE TABLE " + WeightTable.TABLE + " (" +
                WeightTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                WeightTable.WEIGHT_LB + " FLOAT, " +
                WeightTable.WEIGHT_KG + " FLOAT, " +
                WeightTable.DATE + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + LoginTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + GoalWeightTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + WeightTable.TABLE);
        onCreate(db);
    }

    public boolean isLoginTableEmpty() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT count(*) FROM " + LoginTable.TABLE;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count < 1;
    }

    public long registerUser(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LoginTable.COL_USERNAME, username);
        values.put(LoginTable.COL_PASSWORD, password);

        return db.insert(LoginTable.TABLE, null, values);
    }

    public boolean validateLogin(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT count(*) FROM " + LoginTable.TABLE +
                " WHERE " + LoginTable.COL_USERNAME + " = ?" +
                " AND " + LoginTable.COL_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[] { username, password });
        cursor.moveToFirst();
        boolean isValidLogin = cursor.getInt(0) > 0;
        cursor.close();
        return isValidLogin;
    }

    public List<Weight> readAllWeights(String weightText, String WEIGHT_UNIT) {
        List<Weight> weights = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + WeightTable.TABLE + " ORDER BY " + WeightTable.DATE + " DESC";
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                // Get the weight in the correct unit
                double weight = cursor.getDouble(WEIGHT_UNIT.equals("lb") ? 1 : 2);
                Date date = Date.valueOf(cursor.getString(3));
                Weight weightEntry = new Weight(id, weight, date, WEIGHT_UNIT, weightText);
                weights.add(weightEntry);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return weights;
    }

    public long addWeight(Weight weight) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        // Add the weight in both units based on the current unit
        if (weight.getUnit().equals("lb")) {
            values.put(WeightTable.WEIGHT_LB, weight.getWeight());
            values.put(WeightTable.WEIGHT_KG, Weight.lbToKg(weight.getWeight()));
        } else {
            values.put(WeightTable.WEIGHT_LB, Weight.kgToLb(weight.getWeight()));
            values.put(WeightTable.WEIGHT_KG, weight.getWeight());
        }
        values.put(WeightTable.DATE, String.valueOf(weight.getDate()));
        return db.insert(WeightTable.TABLE, null, values);
    }

    public int editWeight(Weight weight) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        if (weight.getUnit().equals("lb")) {
            values.put(WeightTable.WEIGHT_LB, weight.getWeight());
            values.put(WeightTable.WEIGHT_KG, Weight.lbToKg(weight.getWeight()));
        } else {
            values.put(WeightTable.WEIGHT_LB, Weight.kgToLb(weight.getWeight()));
            values.put(WeightTable.WEIGHT_KG, weight.getWeight());
        }
        values.put(WeightTable.DATE, String.valueOf(weight.getDate()));
        return db.update(WeightTable.TABLE, values, WeightTable.ID + " = ?", new String[] { String.valueOf(weight.getId()) });
    }

    public int deleteWeight(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(WeightTable.TABLE, WeightTable.ID + " = ?", new String[] { String.valueOf(id) });
    }

    public long updateGoalWeight(double goalWeight, String unit) {
        SQLiteDatabase db = getWritableDatabase();
        // Check if the goal weight table is empty
        String query = "SELECT count(*) FROM " + GoalWeightTable.TABLE;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        ContentValues values = new ContentValues();
        if (unit.equals("lb")) {
            values.put(GoalWeightTable.GOAL_LB, goalWeight);
            values.put(GoalWeightTable.GOAL_KG, Weight.lbToKg(goalWeight));
        } else {
            values.put(GoalWeightTable.GOAL_LB, Weight.kgToLb(goalWeight));
            values.put(GoalWeightTable.GOAL_KG, goalWeight);
        }
        if (count < 1) {
            // Add a goal weight if none exists
            return db.insert(GoalWeightTable.TABLE, null, values);
        }
        // Otherwise, update existing goal weight
        return (long) db.update(GoalWeightTable.TABLE, values, GoalWeightTable.ID + " = 1", null);
    }

    public double readGoalWeight(String unit) {
        SQLiteDatabase db = getReadableDatabase();
        // Check if the goal weight table is empty
        String query = "SELECT count(*) FROM " + GoalWeightTable.TABLE;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        if (count < 1) {
            // Add a default goal weight record
            return -1;
        }
        // Read the goal weight from the table
        query = "SELECT * FROM " + GoalWeightTable.TABLE + " WHERE " + GoalWeightTable.ID + " = 1";
        cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        double goalWeight = cursor.getDouble(unit.equals("lb") ? 1 : 2);
        cursor.close();
        return goalWeight;
    }
}
