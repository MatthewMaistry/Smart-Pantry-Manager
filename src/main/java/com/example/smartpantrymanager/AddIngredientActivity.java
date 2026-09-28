package com.example.smartpantrymanager;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class AddIngredientActivity extends AppCompatActivity {

    private EditText edtIngredientName;
    private EditText edtQuantity;
    private EditText edtUnit;
    private EditText edtExpiryDate;
    private DatabaseHelper databaseHelper;

    private int ingredientId = -1;
    private boolean editMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_ingredient);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
            );
            return insets;
        });

        edtIngredientName = findViewById(R.id.edtIngredientName);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtUnit = findViewById(R.id.edtUnit);
        edtExpiryDate = findViewById(R.id.edtExpiryDate);

        databaseHelper = new DatabaseHelper(this);

        // Check if an existing ingredient is being edited
        ingredientId = getIntent().getIntExtra("ingredient_id", -1);

        if (ingredientId != -1) {
            editMode = true;
            loadIngredient();
        }

        edtExpiryDate.setOnClickListener(v -> showDatePicker());

        Button btnSaveIngredient = findViewById(R.id.btnSaveIngredient);

        btnSaveIngredient.setOnClickListener(v -> saveIngredient());
    }

    private void loadIngredient() {

        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        Cursor cursor = database.rawQuery(
                "SELECT name, quantity, unit, expiry_date FROM pantry WHERE id = ?",
                new String[]{String.valueOf(ingredientId)}
        );

        if (cursor.moveToFirst()) {

            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow("name")
            );

            double quantity = cursor.getDouble(
                    cursor.getColumnIndexOrThrow("quantity")
            );

            String unit = cursor.getString(
                    cursor.getColumnIndexOrThrow("unit")
            );

            String expiryDate = cursor.getString(
                    cursor.getColumnIndexOrThrow("expiry_date")
            );

            edtIngredientName.setText(name);
            edtQuantity.setText(String.valueOf(quantity));
            edtUnit.setText(unit);

            if (expiryDate != null) {
                edtExpiryDate.setText(expiryDate);
            }
        }

        cursor.close();
    }

    private void saveIngredient() {

        String name = edtIngredientName.getText().toString().trim();
        String quantityText = edtQuantity.getText().toString().trim();
        String unit = edtUnit.getText().toString().trim();
        String expiryDate = edtExpiryDate.getText().toString().trim();

        // Check required fields
        if (name.isEmpty()) {
            edtIngredientName.setError("Please enter an ingredient name");
            edtIngredientName.requestFocus();
            return;
        }

        if (quantityText.isEmpty()) {
            edtQuantity.setError("Please enter a quantity");
            edtQuantity.requestFocus();
            return;
        }

        if (unit.isEmpty()) {
            edtUnit.setError("Please enter a unit");
            edtUnit.requestFocus();
            return;
        }

        // Convert quantity to a number
        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            edtQuantity.setError("Please enter a valid quantity");
            edtQuantity.requestFocus();
            return;
        }

        if (quantity <= 0) {
            edtQuantity.setError("Quantity must be greater than 0");
            edtQuantity.requestFocus();
            return;
        }

        // Save to SQLite
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("quantity", quantity);
        values.put("unit", unit);

        if (expiryDate.isEmpty()) {
            values.putNull("expiry_date");
        } else {
            values.put("expiry_date", expiryDate);
        }

        long result;

        if (editMode) {

            // Update existing ingredient
            result = database.update(
                    "pantry",
                    values,
                    "id = ?",
                    new String[]{String.valueOf(ingredientId)}
            );

            if (result > 0) {
                Toast.makeText(
                        this,
                        "Ingredient updated successfully",
                        Toast.LENGTH_SHORT
                ).show();

                finish();
            } else {
                Toast.makeText(
                        this,
                        "Failed to update ingredient",
                        Toast.LENGTH_SHORT
                ).show();
            }

        } else {

            // Add new ingredient
            result = database.insert("pantry", null, values);

            if (result != -1) {
                Toast.makeText(
                        this,
                        "Ingredient saved successfully",
                        Toast.LENGTH_SHORT
                ).show();

                finish();
            } else {
                Toast.makeText(
                        this,
                        "Failed to save ingredient",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    private void showDatePicker() {

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {

                    String date = selectedYear + "-" +
                            String.format("%02d", selectedMonth + 1) + "-" +
                            String.format("%02d", selectedDay);

                    edtExpiryDate.setText(date);
                },
                year,
                month,
                day
        );

        datePickerDialog.show();
    }
}