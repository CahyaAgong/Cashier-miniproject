package com.jeje.cashier;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jeje.cashier.Adapter.BarangAdapter;
import com.jeje.cashier.Adapter.TransaksiAdapter;
import com.jeje.cashier.Model.Barang;
import com.jeje.cashier.SQL.DataHelper;

import java.util.ArrayList;
import java.util.List;

public class Transaksi_Activity extends AppCompatActivity {

    private DataHelper dataHelper;
    private RecyclerView rec_transaksi;
    public static TextView tv;
    public static List<Barang> cartFoods = new ArrayList<>();
    private List<Barang> barangList;
    private TransaksiAdapter transaksiAdapter;
    private SQLiteDatabase read;
    protected Cursor cursor;

    FloatingActionButton savedOrder;

    protected RecyclerView.LayoutManager mLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaksi_activity);

        getSupportActionBar().setTitle("Menu Transaksi");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initWidget();
        cartUpdate();

        listData();

        savedOrder = findViewById(R.id.FAB_savedOrder);
        savedOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go = new Intent(Transaksi_Activity.this, Orders_Activity.class);
                startActivity(go);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        cartUpdate();
        MenuItem item = menu.findItem(R.id.action_addcart);
        MenuItemCompat.setActionView(item, R.layout.cart_count_layout);
        RelativeLayout notifCount = (RelativeLayout) MenuItemCompat.getActionView(item);
        tv = notifCount.findViewById(R.id.hotlist_hot);
        View view = notifCount.findViewById(R.id.hotlist_bell);

        cartUpdate();

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Transaksi_Activity.this, CartActivity.class);
                startActivity(myIntent);
            }});

        return true;
    }

    void initWidget(){
        dataHelper  =  new DataHelper(this);
        read        = dataHelper.getReadableDatabase();

        barangList          = new ArrayList<>();
        transaksiAdapter    = new TransaksiAdapter(barangList, this);

        rec_transaksi = findViewById(R.id.rec_transaksi);
        mLayoutManager = new LinearLayoutManager(this);
        rec_transaksi.setLayoutManager(mLayoutManager);
    }

    public void listData(){
        cursor     = read.rawQuery("SELECT * FROM tb_barang WHERE stok > 0", null);
        if (cursor != null){
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                Barang barang = new Barang(
                        cursor.getInt(cursor.getColumnIndex("id_barang")),
                        cursor.getInt(cursor.getColumnIndex("stok")),
                        cursor.getString(cursor.getColumnIndex("kode_barang")),
                        cursor.getString(cursor.getColumnIndex("nama_barang")));
                barangList.add(barang);

                mLayoutManager = new LinearLayoutManager(this);
                rec_transaksi.setLayoutManager(mLayoutManager);
                rec_transaksi.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
                rec_transaksi.setItemAnimator(new DefaultItemAnimator());
                rec_transaksi.setHasFixedSize(true);
                rec_transaksi.setAdapter(transaksiAdapter);

            }
        } else {
            Toast.makeText(this, "Data Kosong!", Toast.LENGTH_SHORT).show();
        }
    }

    public static void cartUpdate() {
        if (tv != null && cartFoods != null)
            tv.setText(Integer.toString(cartFoods.size()));
    }

}