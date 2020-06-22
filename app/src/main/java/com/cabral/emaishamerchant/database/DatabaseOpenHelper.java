package com.cabral.emaishamerchant.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.cabral.emaishamerchant.R;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import es.dmoral.toasty.Toasty;

public class DatabaseOpenHelper extends SQLiteAssetHelper {
    public static final String DATABASE_NAME = "smart_pos.db";
    private static final int DATABASE_VERSION = 1;
    private Context mContext;

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }


    public void backup(String outFileName) {

        //database path
        final String inFileName = mContext.getDatabasePath(DATABASE_NAME).toString();

        try {

            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();

            Toasty.success(mContext, mContext.getString(R.string.backup_completed_successfully), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toasty.error(mContext, R.string.unable_to_backup_database_retry, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    public void importDB(String inFileName) {

        final String outFileName = mContext.getDatabasePath("smart_pos.db").toString();


        try {

            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();

            Toasty.success(mContext, R.string.database_Import_completed, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toasty.error(mContext, R.string.unable_to_import_database_retry, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//
//        // If you need to add a new column
//        if (newVersion > oldVersion) {
//
//            db.execSQL("ALTER TABLE  suppliers ADD COLUMN suppliers_address_two TEXT");
//            db.execSQL("ALTER TABLE  suppliers ADD COLUMN suppliers_image TEXT");
//            db.execSQL("ALTER TABLE  customers ADD COLUMN customer_address_two TEXT");
//            db.execSQL("ALTER TABLE  customers ADD COLUMN customer_image TEXT");
//        }
//    }


}