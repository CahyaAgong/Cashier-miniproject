package com.jeje.cashier.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jeje.cashier.CartActivity;
import com.jeje.cashier.Model.BarangFirebaseGet;
import com.jeje.cashier.R;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder>{

    private List<BarangFirebaseGet> barangFirebaseGets;
    private Context c;

    public OrderAdapter(List<BarangFirebaseGet> barangFirebaseGets, Context c) {
        this.barangFirebaseGets = barangFirebaseGets;
        this.c = c;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_firebase_layout, parent, false);
        return new OrderAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, int position) {
        BarangFirebaseGet barangFirebaseGet = barangFirebaseGets.get(position);
        holder.tvNamaBarang.setText(barangFirebaseGet.getNama_barang());
        holder.tvKodeBarang.setText("#Order Id : " + barangFirebaseGet.getId_pesanan());
        holder.tvJmlhBarang.setText(barangFirebaseGet.getJumlah_barang() + " Barang");
        holder.tvIdBarang.setText(Integer.toString(barangFirebaseGet.getId_barang()));
    }

    @Override
    public int getItemCount() {
        return barangFirebaseGets.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvNamaBarang, tvKodeBarang, tvJmlhBarang, tvIdBarang;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNamaBarang   = itemView.findViewById(R.id.tv_namaBarangFirebase);
            tvKodeBarang   = itemView.findViewById(R.id.tv_kodeBarangFirebase);
            tvJmlhBarang   = itemView.findViewById(R.id.tv_jumlahBarangFirebase);
            tvIdBarang     = itemView.findViewById(R.id.tv_idBarangFirebase);
        }
    }
}
