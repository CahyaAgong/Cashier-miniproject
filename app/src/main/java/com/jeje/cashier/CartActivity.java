package com.jeje.cashier;

import static com.jeje.cashier.Transaksi_Activity.cartFoods;
import static com.jeje.cashier.Transaksi_Activity.cartUpdate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jeje.cashier.Adapter.CartAdapter;
import com.jeje.cashier.Model.Barang;
import com.jeje.cashier.Model.BarangFirebaseGet;
import com.jeje.cashier.Model.BarangFirebaseSet;
import com.jeje.cashier.SQL.DataHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CartActivity extends AppCompatActivity {
    private SQLiteDatabase read, write;
    protected Cursor cursor;
    RecyclerView recyclerviewCart;
    Button btn_cancel, btn_save, btn_submit;

    /* firebase */
    FirebaseDatabase database;
    DatabaseReference getReference;

    ArrayList<BarangFirebaseGet> barangFirebaseGet;
    String Keys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_activity);
        getSupportActionBar().setTitle("Cart");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        init();
        countDuplicateData();

        setListener();
    }

    void init(){

        DataHelper dataHelper = new DataHelper(this);
        read        = dataHelper.getReadableDatabase();
        write      = dataHelper.getWritableDatabase();

        // initiate recycler
        recyclerviewCart = findViewById(R.id.rec_cart);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerviewCart.setLayoutManager(linearLayoutManager);
        recyclerviewCart.setNestedScrollingEnabled(false);
        recyclerviewCart.setAdapter(new CartAdapter(cartFoods, R.layout.list_cart_layout, getApplicationContext()));

        btn_cancel = findViewById(R.id.btn_cancel_cart);
        btn_save   = findViewById(R.id.btn_save_order);
        btn_submit = findViewById(R.id.btn_submit_cart);

        database = FirebaseDatabase.getInstance();
    }

    public static void countDuplicateData(){
        try {
            Set<Barang> set = new HashSet<Barang>(cartFoods);
            ArrayList<Barang> temp_array = new ArrayList<>();
            temp_array.addAll(set);
            for (int i = 0 ; i < temp_array.size(); i++){
                Log.e(temp_array.get(i).getKode_barang()," => "+ Collections.frequency(cartFoods, temp_array.get(i)));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void setListener(){
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartFoods.size() > 0){
                    Set<Barang> set = new HashSet<Barang>(cartFoods);
                    ArrayList<Barang> temp_array = new ArrayList<>();
                    temp_array.addAll(set);
                    for (int i = 0 ; i < temp_array.size(); i++){
                        cursor = read.rawQuery("SELECT stok FROM tb_barang WHERE kode_barang = '"+ temp_array.get(i).getKode_barang() +"' ", null);
                        if (cursor != null){
                            cursor.moveToNext();
                            int stokMin = cursor.getInt(cursor.getColumnIndex("stok")) - Collections.frequency(cartFoods, temp_array.get(i));
                            write.execSQL("UPDATE tb_barang SET stok = '"+stokMin+"' WHERE kode_barang = '"+temp_array.get(i).getKode_barang()+"'");
                        } else {
                            Log.e("Cursor => ", "Kosong");
                        }
                    }
                    Toast.makeText(CartActivity.this, "Berhasil Order!", Toast.LENGTH_SHORT).show();
                    cartFoods.clear();
                    temp_array.clear();
                    Intent intent = new Intent(CartActivity.this, Confirmation_Activity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(CartActivity.this, "Tidak ada barang tersedia!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReference = database.getReference();

                Set<Barang> set = new HashSet<Barang>(cartFoods);
                ArrayList<Barang> temp_array = new ArrayList<>();
                temp_array.addAll(set);

                getReference.child("Saved_Order").orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()){
                        for (int i = 0 ; i < temp_array.size(); i++){
                            Keys = getReference.push().getKey();
                            getReference.child("Saved_Order").child(Keys)
                                    .setValue(new BarangFirebaseSet(1, temp_array.get(i).getId_barang(), temp_array.get(i).getStok(), Collections.frequency(cartFoods, temp_array.get(i)), temp_array.get(i).getKode_barang(), temp_array.get(i).getNama_barang()))
                                    .addOnSuccessListener(CartActivity.this, new OnSuccessListener() {
                                        @Override
                                        public void onSuccess(Object o) {
                                            cartFoods.clear();
                                            temp_array.clear();
                                            cartUpdate();
                                            finish();
                                            Toast.makeText(CartActivity.this, "Berhasil di simpan di firebase!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        barangFirebaseGet = new ArrayList<>();
                        for (DataSnapshot snp : snapshot.getChildren()){
                            BarangFirebaseGet r  = snp.getValue(BarangFirebaseGet.class);
                            r.setKeys(snp.getKey());
                            barangFirebaseGet.add(r);
                            Keys = snp.getKey();
                        }
                        int Id_Order = Integer.parseInt(snapshot.child(Keys).child("id_pesanan").getValue().toString()) + 1;
                        for (int i = 0 ; i < temp_array.size(); i++){
                            Keys = getReference.push().getKey();
                            getReference.child("Saved_Order").child(Keys)
                                    .setValue(new BarangFirebaseSet(Id_Order, temp_array.get(i).getId_barang(), temp_array.get(i).getStok(), Collections.frequency(cartFoods, temp_array.get(i)), temp_array.get(i).getKode_barang(), temp_array.get(i).getNama_barang()))
                                    .addOnSuccessListener(CartActivity.this, new OnSuccessListener() {
                                        @Override
                                        public void onSuccess(Object o) {
                                            cartFoods.clear();
                                            temp_array.clear();
                                            cartUpdate();
                                            finish();
                                            Toast.makeText(CartActivity.this, "Berhasil di simpan di firebase!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent launchNextActivity = new Intent(CartActivity.this, Transaksi_Activity.class);
                launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(launchNextActivity);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

    }
}