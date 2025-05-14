package com.example.nailshop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orderList;

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Dátum formázása
        String dateStr = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
                .format(new Date(order.getTimestamp()));
        holder.tvOrderDate.setText("Dátum: " + dateStr);

        // Termékek listája szövegként
        StringBuilder itemsStr = new StringBuilder();
        double total = 0;
        for (CartItem item : order.getItems()) {
            itemsStr.append(item.getName())
                    .append(" (")
                    .append(item.getQuantity())
                    .append(" db) - ")
                    .append(item.getPrice())
                    .append(" Ft\n");
            total += item.getPrice() * item.getQuantity();
        }
        holder.tvOrderItems.setText(itemsStr.toString().trim());
        holder.tvOrderTotal.setText("Összesen: " + total + " Ft");
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderDate, tvOrderItems, tvOrderTotal;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderItems = itemView.findViewById(R.id.tvOrderItems);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
        }
    }
}
