package com.jeje.cashier.Adapter;

import static com.jeje.cashier.CartActivity.countDuplicateData;
import static com.jeje.cashier.Transaksi_Activity.cartFoods;
import static com.jeje.cashier.Transaksi_Activity.cartUpdate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.jeje.cashier.Model.Barang;
import com.jeje.cashier.R;
import com.jeje.cashier.Transaksi_Activity;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private CartAdapter mCartAdapter;


    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_cart_layout, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartViewHolder holder, final int position) {
        holder.tv_NamaBarangCart.setText(cartFoods.get(position).getNama_barang());
        holder.tv_KodeBarangCart.setText("#"+cartFoods.get(position).getKode_barang());

        holder.cartDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Barang item = cartFoods.get(position);
                cartFoods.remove(item);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cartFoods.size());
                cartUpdate();
                countDuplicateData();
                Toast.makeText(context, "Berhasil Menghapus Barang", Toast.LENGTH_SHORT).show();
            }});


    }

    @Override
    public int getItemCount() {
        return cartFoods.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tv_KodeBarangCart, tv_NamaBarangCart;
        ImageButton cartDelete;

        public CartViewHolder(View itemView) {
            super(itemView);

            tv_KodeBarangCart = itemView.findViewById(R.id.tv_kodeBarangCart);
            tv_NamaBarangCart = itemView.findViewById(R.id.tv_namaBarangCart);
            cartDelete        = itemView.findViewById(R.id.cart_food_delete);

        }
    }

    public CartAdapter(List<Barang> cartFoods, int recyclerview_cart, Context context){
        this.context = context;
        Transaksi_Activity.cartFoods = cartFoods;

    }


}
