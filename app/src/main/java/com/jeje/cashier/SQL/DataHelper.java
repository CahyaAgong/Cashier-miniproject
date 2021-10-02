package com.jeje.cashier.SQL;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class DataHelper extends SQLiteOpenHelper{

    public static final String db = "db_crud";
    public static final String tbl = "tb_barang";


    public static final List<String> create = new ArrayList<String>(){{

        add("create table " + tbl +
                " (kode_barang TEXT UNIQUE, nama_barang TEXT NOT NULL, stok INTEGER NOT NULL, id_barang INTEGER PRIMARY KEY AUTOINCREMENT)");

    }};

    public static final List<String> table = new ArrayList<String>(){{
        add(tbl);
    }};

    public DataHelper(Context context) {
        super(context, db, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        for (int i = 0; i < create.size(); i++){
            sqLiteDatabase.execSQL(create.get(i));
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        for (int j = 0; j < table.size(); j++){
            sqLiteDatabase.execSQL(table.get(j));
        }
    }
}
