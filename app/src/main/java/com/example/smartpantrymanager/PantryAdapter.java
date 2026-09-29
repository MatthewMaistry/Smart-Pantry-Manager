package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.Intent;
import android.app.AlertDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    private List<PantryItem> pantryItems;

    public PantryAdapter(List<PantryItem> pantryItems) {
        this.pantryItems = pantryItems;
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);

        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = pantryItems.get(position);

        holder.txtIngredientName.setText(item.getName());

        holder.txtIngredientQuantity.setText(
                "Quantity: " + item.getQuantity() + " " + item.getUnit()
        );

        if (item.getExpiryDate() == null || item.getExpiryDate().isEmpty()) {
            holder.txtIngredientExpiry.setText("Expiry date: Not set");
        } else {
            holder.txtIngredientExpiry.setText(
                    "Expiry date: " + item.getExpiryDate()
            );
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AddIngredientActivity.class);
            intent.putExtra("ingredient_id", item.getId());
            v.getContext().startActivity(intent);
        });

        holder.btnDeleteIngredient.setOnClickListener(v -> {

            new AlertDialog.Builder(v.getContext())
                    .setTitle("Delete Ingredient")
                    .setMessage("Are you sure you want to delete " + item.getName() + "?")
                    .setPositiveButton("Delete", (dialog, which) -> {

                        DatabaseHelper databaseHelper =
                                new DatabaseHelper(v.getContext());

                        SQLiteDatabase database =
                                databaseHelper.getWritableDatabase();

                        database.delete(
                                "pantry",
                                "id = ?",
                                new String[]{String.valueOf(item.getId())}
                        );

                        pantryItems.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, pantryItems.size());
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return pantryItems.size();
    }

    public static class PantryViewHolder extends RecyclerView.ViewHolder {

        TextView txtIngredientName;
        TextView txtIngredientQuantity;
        TextView txtIngredientExpiry;
        android.widget.Button btnDeleteIngredient;

        public PantryViewHolder(@NonNull View itemView) {
            super(itemView);

            txtIngredientName = itemView.findViewById(R.id.txtIngredientName);
            txtIngredientQuantity = itemView.findViewById(R.id.txtIngredientQuantity);
            txtIngredientExpiry = itemView.findViewById(R.id.txtIngredientExpiry);
            btnDeleteIngredient = itemView.findViewById(R.id.btnDeleteIngredient);
        }
    }
}