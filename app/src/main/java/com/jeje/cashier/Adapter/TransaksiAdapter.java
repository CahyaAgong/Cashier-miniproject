package com.jeje.cashier.Adapter;

import static com.jeje.cashier.Transaksi_Activity.cartFoods;
import static com.jeje.cashier.Transaksi_Activity.cartUpdate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jeje.cashier.Model.Barang;
import com.jeje.cashier.R;

import java.util.ArrayList;
import java.util.List;

public class TransaksiAdapter extends RecyclerView.Adapter<TransaksiAdapter.ViewHolder>{

    private List<Barang> listBarang;
    private Context c;

    public TransaksiAdapter(List<Barang> listBarang, Context c) {
        this.listBarang = listBarang;
        this.c = c;
    }

    @NonNull
    @Override
    public TransaksiAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_transaksi_layout, parent, false);
        return new TransaksiAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransaksiAdapter.ViewHolder holder, int position) {
        holder.tvNamaBarangTr.setText(listBarang.get(position).getNama_barang());
        holder.tvKodeBarangTr.setText("#"+listBarang.get(position).getKode_barang());
        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartFoods.add(listBarang.get(position));
                cartUpdate();
                Toast.makeText(c, "Berhasil Menambah Barang", Toast.LENGTH_SHORT).show();
            }});
    }

    @Override
    public int getItemCount() {
        return listBarang.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvNamaBarangTr, tvKodeBarangTr;
        private ImageView plus;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNamaBarangTr   = itemView.findViewById(R.id.tv_namaBarangTransaksi);
            tvKodeBarangTr   = itemView.findViewById(R.id.tv_kodeBarangTransaksi);
            plus             = itemView.findViewById(R.id.img_barang_plus);
        }
    }
}
