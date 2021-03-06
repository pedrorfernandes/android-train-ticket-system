package org.feup.cmov.userticketapp.Models;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;

import org.feup.cmov.userticketapp.R;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "ticket.db";
    public final static Gson gson = new Gson();
    private static Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        DatabaseHelper.context = context;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TicketContract.SQL_CREATE_ENTRIES);
        db.execSQL(CreditCardContract.SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(TicketContract.SQL_DELETE_ENTRIES);
        db.execSQL(CreditCardContract.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public static Ticket getTicketWithUUID(SQLiteDatabase db, String uuid) {
        String[] projection = { TicketEntry.COLUMN_NAME_JSON };
        String selection = TicketEntry.COLUMN_NAME_UUID + " LIKE ?";
        String[] selectionArgs = { uuid };

        Cursor cursor = db.query(
                TicketEntry.TABLE_NAME,  // The table to query
                projection,              // The columns to return
                selection,               // The columns for the WHERE clause
                selectionArgs,           // The values for the WHERE clause
                null,                    // don't group the rows
                null,                    // don't filter by row groups
                null                     // The sort order
        );

        Ticket ticket = null;
        while (cursor.moveToNext()) {
            String ticketJson = cursor.getString(cursor.getColumnIndexOrThrow(TicketEntry.COLUMN_NAME_JSON));
            ticket = gson.fromJson(ticketJson, Ticket.class);
        }
        cursor.close();

        return ticket;
    }

    public static int updateTicket(SQLiteDatabase db, Ticket ticket) {
        ContentValues values = ticket.toDatabaseValues();

        String selection = TicketEntry.COLUMN_NAME_UUID + " LIKE ?";
        String[] selectionArgs = { ticket.getUuid() };

        return db.update(TicketEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    public static long insertTicket(SQLiteDatabase db, Ticket ticket) {
        return db.insert(
                TicketEntry.TABLE_NAME,
                null,
                ticket.toDatabaseValues());
    }

    public static long insertCreditCard(SQLiteDatabase db, CreditCard creditCard) {
        db.execSQL("delete from " + CreditCardEntry.TABLE_NAME +
                " where " + CreditCardEntry.COLUMN_NAME_NUMBER + " = " + creditCard.getNumber());

        return db.insert(
                CreditCardEntry.TABLE_NAME,
                null,
                creditCard.toDatabaseValues(context));
    }

    public static void deleteAllTickets(SQLiteDatabase db) {
        db.execSQL("delete from " + TicketEntry.TABLE_NAME);
    }

    public static Ticket[] getAllTickets(SQLiteDatabase db) {
        String[] projection = { TicketEntry.COLUMN_NAME_JSON };

        Cursor cursor = db.query(
                TicketEntry.TABLE_NAME,  // The table to query
                projection,              // The columns to return
                null,               // The columns for the WHERE clause
                null,           // The values for the WHERE clause
                null,                    // don't group the rows
                null,                    // don't filter by row groups
                null                     // The sort order
        );

        Ticket[] tickets = new Ticket[cursor.getCount()];
        int i = 0;
        while (cursor.moveToNext()) {
            String ticketJson = cursor.getString(cursor.getColumnIndexOrThrow(TicketEntry.COLUMN_NAME_JSON));
            Ticket ticket = gson.fromJson(ticketJson, Ticket.class);
            tickets[i] = ticket;
            i++;
        }
        cursor.close();

        return tickets;
    }

    public static CreditCard[] getAllCreditCards(SQLiteDatabase db) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.shared_preferences_identifier),
                Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString(
                context.getString(R.string.shared_preferences_user_id_key), "0");

        String[] projection = {
                CreditCardEntry.COLUMN_NAME_NUMBER,
                CreditCardEntry.COLUMN_NAME_CODE,
                CreditCardEntry.COLUMN_NAME_MONTH,
                CreditCardEntry.COLUMN_NAME_YEAR,
        };
        String selection = CreditCardEntry.COLUMN_NAME_USER_ID + " = ?";
        String[] selectionArgs = { userId };

        Cursor cursor = db.query(
                CreditCardEntry.TABLE_NAME,  // The table to query
                projection,              // The columns to return
                selection,               // The columns for the WHERE clause
                selectionArgs,           // The values for the WHERE clause
                null,                    // don't group the rows
                null,                    // don't filter by row groups
                null                     // The sort order
        );

        CreditCard[] creditCards = new CreditCard[cursor.getCount()];
        int i = 0;
        while (cursor.moveToNext()) {
            CreditCard card = new CreditCard(
                    cursor.getLong(cursor.getColumnIndexOrThrow(CreditCardEntry.COLUMN_NAME_NUMBER)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(CreditCardEntry.COLUMN_NAME_MONTH)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(CreditCardEntry.COLUMN_NAME_YEAR)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(CreditCardEntry.COLUMN_NAME_CODE))
            );
            creditCards[i] = card;
            i++;
        }
        cursor.close();

        return creditCards;
    }
}