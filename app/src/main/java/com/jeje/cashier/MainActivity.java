package com.jeje.cashier;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button menuBarang, transaksiBarang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Home");

        initWidget();
        setListener();

    }

    private void initWidget(){
        menuBarang = findViewById(R.id.menu_barang);
        transaksiBarang = findViewById(R.id.transaksi_barang);
    }

    private void setListener(){
        menuBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(i);
            }
        });

        transaksiBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Transaksi_Activity.class);
                startActivity(i);
            }
        });
    }

}