package com.jeje.cashier.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jeje.cashier.Model.Barang;
import com.jeje.cashier.R;

import java.util.List;

public class BarangAdapter extends RecyclerView.Adapter<BarangAdapter.ViewHolder> {

    private List<Barang> listBarang;

    public BarangAdapter(List<Barang> listBarang) {
        this.listBarang = listBarang;
    }

    @NonNull
    @Override
    public BarangAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_barang_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BarangAdapter.ViewHolder holder, int position) {
        Barang barang = listBarang.get(position);
        holder.tvNamaBarang.setText(barang.getNama_barang());
        holder.tvKodeBarang.setText("#" + barang.getKode_barang());
        holder.tvStokBarang.setText(barang.getStok() + " Stok");
        holder.tvIdBarang.setText(Integer.toString(barang.getId_barang()));
    }

    @Override
    public int getItemCount() {
        return listBarang.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvNamaBarang, tvKodeBarang, tvStokBarang, tvIdBarang;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNamaBarang   = itemView.findViewById(R.id.tv_namaBarang);
            tvKodeBarang   = itemView.findViewById(R.id.tv_kodeBarang);
            tvStokBarang   = itemView.findViewById(R.id.tv_stokBarang);
            tvIdBarang     = itemView.findViewById(R.id.tv_idBarang);
        }
    }
}
