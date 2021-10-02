package com.jeje.cashier;

import static com.jeje.cashier.Transaksi_Activity.cartFoods;
import static com.jeje.cashier.Transaksi_Activity.cartUpdate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jeje.cashier.Adapter.BarangAdapter;
import com.jeje.cashier.Model.Barang;
import com.jeje.cashier.Model.BarangFirebaseGet;
import com.jeje.cashier.SQL.DataHelper;
import com.jeje.cashier.Util.SpacesItemDecoration;
import com.jeje.cashier.Util.SwipeUtilDelete;
import com.jeje.cashier.Util.SwipeUtilEdit;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    private enum LayoutManagerType {
        LINEAR_LAYOUT_MANAGER
    }
    protected MenuActivity.LayoutManagerType mCurrentLayoutManagerType;

    private EditText NamaBarang, KodeBarang, StokBarang;
    private Button submitData, resetFormEdit;

    private DataHelper dataHelper;
    private RecyclerView recy_listBarang;
    private SQLiteDatabase read, insert;
    private List<Barang> barangList;
    private BarangAdapter barangAdapter;
    protected Cursor cursor;
    private AlertDialog.Builder alertBuilder;
    private AlertDialog alertDialog;

    protected RecyclerView.LayoutManager mLayoutManager;

    private int STATUS_BTN = 0;
    private int ID_BARANG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);

        getSupportActionBar().setTitle("Menu Barang");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initWidget();
        setListener();


        listData();
        setSwipeForRecyclerView();
        recy_listBarang.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    final int position = rv.getChildAdapterPosition(child);
                    Toast.makeText(MenuActivity.this, "Swipe data for edit & delete", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

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

    private void initWidget(){
        //init SQL HELPER
        dataHelper  =  new DataHelper(this);
        read        = dataHelper.getReadableDatabase();
        insert      = dataHelper.getWritableDatabase();

        // init model, adapter, recycleview
        recy_listBarang  = findViewById(R.id.rec_listBarang);
        barangList       = new ArrayList<>();
        barangAdapter    = new BarangAdapter(barangList);

        mLayoutManager = new LinearLayoutManager(this);
        mCurrentLayoutManagerType = MenuActivity.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        recy_listBarang.setLayoutManager(mLayoutManager);

        // set adapter to recylerview

//        recy_listBarang.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        recy_listBarang.addItemDecoration(new SpacesItemDecoration(12));
        recy_listBarang.setItemAnimator(new DefaultItemAnimator());
        recy_listBarang.setHasFixedSize(true);
        recy_listBarang.setAdapter(barangAdapter);

        //init editText
        NamaBarang  = findViewById(R.id.et_namaBarang);
        KodeBarang  = findViewById(R.id.et_kodeBarang);
        StokBarang  = findViewById(R.id.et_stokBarang);

        //init Button
        submitData  = findViewById(R.id.btn_tambahData);
        resetFormEdit = findViewById(R.id.btn_resetFormEdit);
    }

    public void setSwipeForRecyclerView() {

        SwipeUtilDelete swipeHelper = new SwipeUtilDelete(0, ItemTouchHelper.LEFT, this) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                BarangAdapter adapter = (BarangAdapter) recy_listBarang.getAdapter();
                dialogHapus(barangList.get(position).getId_barang());
            }
        };

        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(swipeHelper);
        mItemTouchHelper.attachToRecyclerView(recy_listBarang);
        swipeHelper.setLeftcolorCode(ContextCompat.getColor(this, R.color.colorRed));

        SwipeUtilEdit swipeHelpers = new SwipeUtilEdit(0, ItemTouchHelper.RIGHT, this) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                BarangAdapter adapter = (BarangAdapter) recy_listBarang.getAdapter();

                STATUS_BTN = 1;
                KodeBarang.setText(barangList.get(position).getKode_barang());
                KodeBarang.requestFocus();
                NamaBarang.setText(barangList.get(position).getNama_barang());
                StokBarang.setText(Integer.toString(barangList.get(position).getStok()));
                ID_BARANG = barangList.get(position).getId_barang();
                barangAdapter.notifyDataSetChanged();
                openKeyB();
            }
        };

        ItemTouchHelper mItemTouchHelpers = new ItemTouchHelper(swipeHelpers);
        mItemTouchHelpers.attachToRecyclerView(recy_listBarang);
        swipeHelpers.setLeftcolorCode(ContextCompat.getColor(this, R.color.colorGreen));

    }

    public void listData(){
        cursor     = read.rawQuery("SELECT * FROM tb_barang", null);
        if (cursor != null){
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                Barang barang = new Barang(
                        cursor.getInt(cursor.getColumnIndex("id_barang")),
                        cursor.getInt(cursor.getColumnIndex("stok")),
                        cursor.getString(cursor.getColumnIndex("kode_barang")),
                        cursor.getString(cursor.getColumnIndex("nama_barang")));
                barangList.add(barang);
            }
        } else {
          Toast.makeText(this, "Data Kosong!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setListener(){
        submitData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty( KodeBarang.getText() )){
                    Toast.makeText(MenuActivity.this, "Isi Kode Barang!", Toast.LENGTH_SHORT).show();
                } else if(TextUtils.isEmpty( NamaBarang.getText() )) {
                    Toast.makeText(MenuActivity.this, "Isi Nama Barang!", Toast.LENGTH_SHORT).show();
                } else if( TextUtils.isEmpty( StokBarang.getText() ) ){
                    Toast.makeText(MenuActivity.this, "Isi Stok Barang!", Toast.LENGTH_SHORT).show();
                } else {
                    if(STATUS_BTN == 0){
                        try{
                            insert.execSQL("INSERT INTO tb_barang (kode_barang, nama_barang, stok) VALUES ('"+KodeBarang.getText()+"', '"+NamaBarang.getText()+"', '"+StokBarang.getText()+"')");
                            Toast.makeText(MenuActivity.this, "Berhasil Menambah", Toast.LENGTH_SHORT).show();
                            closeKeyB();
                            clearWidget();
                            barangList.clear();
                            listData();
                            barangAdapter.notifyDataSetChanged();

                        }catch (Exception e){
                            Toast.makeText(MenuActivity.this, ""+e, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        try{
                            insert.execSQL("UPDATE tb_barang SET kode_barang = '"+KodeBarang.getText()+"', nama_barang = '"+NamaBarang.getText()+"', stok = '"+StokBarang.getText()+"' " +
                                    "WHERE id_barang = '"+ID_BARANG+"'");
                            Toast.makeText(MenuActivity.this, "Berhasil Mengupdate", Toast.LENGTH_SHORT).show();
                            closeKeyB();
                            clearWidget();
                            barangList.clear();
                            listData();
                            barangAdapter.notifyDataSetChanged();

                            STATUS_BTN = 0;
                        }catch (Exception e){
                            Toast.makeText(MenuActivity.this, ""+e, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        resetFormEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                STATUS_BTN = 0;
                clearWidget();
            }
        });
    }

    public void dialogHapus(final int id){
        alertBuilder = new AlertDialog.Builder(MenuActivity.this);
        alertBuilder
                .setTitle("Peringatan!")
                .setMessage("Apakah Anda akan menghapus nya?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try{
                            insert.execSQL("DELETE FROM tb_barang WHERE id_barang ='"+id+"'");
                            Toast.makeText(MenuActivity.this, "Berhasil", Toast.LENGTH_SHORT).show();
                            barangList.clear();
                            listData();
                            barangAdapter.notifyDataSetChanged();
                        }catch (Exception e){
                            Toast.makeText(MenuActivity.this, ""+e, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        barangList.clear();
                        listData();
                        barangAdapter.notifyDataSetChanged();
                    }
                });
        alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    private void clearWidget(){
        NamaBarang.setText("");
        KodeBarang.setText("");
        StokBarang.setText("");

        NamaBarang.clearFocus();
        KodeBarang.clearFocus();
        StokBarang.clearFocus();
    }

    private void closeKeyB(){
        View v = this.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

    }

    private void openKeyB(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
}