package com.jeje.cashier;

import static com.jeje.cashier.Transaksi_Activity.cartFoods;
import static com.jeje.cashier.Transaksi_Activity.cartUpdate;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jeje.cashier.Adapter.OrderAdapter;
import com.jeje.cashier.Model.Barang;
import com.jeje.cashier.Model.BarangFirebaseGet;
import com.jeje.cashier.Util.SpacesItemDecoration;

import java.util.ArrayList;


public class Orders_Activity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference getReference;

    ArrayList<BarangFirebaseGet> barangFirebaseGet;

    String Keys;

    RecyclerView recyclerViewFirebase;
    RecyclerView.Adapter Adapter;
    RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orders_activity);
        getSupportActionBar().setTitle("Draft Transaksi");
        init();
        getDataFirebase();

        recyclerViewFirebase.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
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

                    AlertDialog.Builder builder = new AlertDialog.Builder(Orders_Activity.this);
                    builder.setMessage("Choose!");
                    builder.setPositiveButton("Cart", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cartFoods.clear();
                            getReference = database.getReference();
                            getReference.child("Saved_Order").orderByChild("id_pesanan").equalTo(barangFirebaseGet.get(position).getId_pesanan())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            barangFirebaseGet = new ArrayList<>();
                                            if (dataSnapshot.exists()){
                                                int idx = 0;
                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                                    BarangFirebaseGet r  = snapshot.getValue(BarangFirebaseGet.class);
                                                    r.setKeys(snapshot.getKey());
                                                    barangFirebaseGet.add(r);
                                                    Keys = snapshot.getKey();

                                                    for (int jml_barang = 0; jml_barang < barangFirebaseGet.get(idx).getJumlah_barang(); jml_barang++){
                                                        Barang barang = new Barang(
                                                                barangFirebaseGet.get(idx).getId_barang(),
                                                                barangFirebaseGet.get(idx).getStok(),
                                                                barangFirebaseGet.get(idx).getKode_barang(),
                                                                barangFirebaseGet.get(idx).getNama_barang()
                                                        );
                                                        cartFoods.add(barang);
                                                        cartUpdate();
                                                    }
                                                    idx++;
                                                }

                                                Intent go = new Intent(Orders_Activity.this, CartActivity.class);
                                                startActivity(go);
                                                finish();
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Log.e("fail =>", String.valueOf(databaseError));
                                        }
                                    });

                        }
                    });
                    builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getReference = database.getReference();
                            getReference.child("Saved_Order").orderByChild("id_pesanan").equalTo(barangFirebaseGet.get(position).getId_pesanan())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            barangFirebaseGet = new ArrayList<>();
                                            if (dataSnapshot.exists()){
                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                                    BarangFirebaseGet r  = snapshot.getValue(BarangFirebaseGet.class);
                                                    r.setKeys(snapshot.getKey());
                                                    barangFirebaseGet.add(r);
                                                    Keys = snapshot.getKey();

                                                    getReference.child("Saved_Order")
                                                            .child(Keys)
                                                            .removeValue()
                                                            .addOnSuccessListener(new OnSuccessListener() {
                                                                @Override
                                                                public void onSuccess(Object o) {
                                                                    Log.e("HAPUS Data => ", "Berhasil!");
                                                                }
                                                            });
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Log.e("fail =>", String.valueOf(databaseError));
                                        }
                                    });

                        }
                    });

                    AlertDialog ad = builder.create();
                    ad.show();

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

    void init(){
        database = FirebaseDatabase.getInstance();
        recyclerViewFirebase = findViewById(R.id.rec_firebaseData);
    }

    void getDataFirebase(){
        getReference = database.getReference();
        getReference.child("Saved_Order")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        barangFirebaseGet = new ArrayList<>();
                        if (dataSnapshot.exists()){
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                BarangFirebaseGet r  = snapshot.getValue(BarangFirebaseGet.class);
                                r.setKeys(snapshot.getKey());
                                barangFirebaseGet.add(r);
                                Keys = snapshot.getKey();
                            }

                            Adapter = new OrderAdapter(barangFirebaseGet, getApplicationContext());
                            recyclerViewFirebase.setHasFixedSize(true);
                            layoutManager = new LinearLayoutManager(getApplicationContext());
                            recyclerViewFirebase.setLayoutManager(layoutManager);
                            recyclerViewFirebase.addItemDecoration(new SpacesItemDecoration(12));
                            recyclerViewFirebase.setAdapter(Adapter);

                            Log.e("Res => ", "Data Lengkap!");
                        } else {
                            finish();
                            Toast.makeText(Orders_Activity.this, "Data Kosong!", Toast.LENGTH_SHORT).show();
                            Log.e("Res => ", "Data Kosong!");
                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(),"Data Gagal Dimuat", Toast.LENGTH_LONG).show();
                    }
                });
    }

}