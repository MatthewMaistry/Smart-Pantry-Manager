package com.example.smartpantrymanager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RecipeDetailActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    private TextView txtRecipeDetailName;
    private TextView txtRecipeIngredients;
    private TextView txtRecipeInstructions;

    private int recipeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_detail);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        txtRecipeDetailName =
                findViewById(R.id.txtRecipeDetailName);

        txtRecipeIngredients =
                findViewById(R.id.txtRecipeIngredients);

        txtRecipeInstructions =
                findViewById(R.id.txtRecipeInstructions);

        databaseHelper = new DatabaseHelper(this);

        recipeId = getIntent().getIntExtra("recipe_id", -1);

        if (recipeId != -1) {
            loadRecipeDetails();
        }
    }

    private void loadRecipeDetails() {

        SQLiteDatabase database =
                databaseHelper.getReadableDatabase();

        // Load recipe name and instructions
        Cursor recipeCursor = database.rawQuery(
                "SELECT name, detailed_instructions FROM recipes WHERE id = ?",
                new String[]{String.valueOf(recipeId)}
        );

        if (recipeCursor.moveToFirst()) {

            String recipeName = recipeCursor.getString(
                    recipeCursor.getColumnIndexOrThrow("name")
            );

            String instructions = recipeCursor.getString(
                    recipeCursor.getColumnIndexOrThrow(
                            "detailed_instructions"
                    )
            );

            txtRecipeDetailName.setText(recipeName);
            txtRecipeInstructions.setText(instructions);
        }

        recipeCursor.close();

        // Load recipe ingredients
        Cursor ingredientCursor = database.rawQuery(
                "SELECT ingredient_name, required_quantity, unit " +
                        "FROM recipe_ingredients " +
                        "WHERE recipe_id = ?",
                new String[]{String.valueOf(recipeId)}
        );

        StringBuilder ingredients = new StringBuilder();

        if (ingredientCursor.moveToFirst()) {

            do {

                String ingredientName = ingredientCursor.getString(
                        ingredientCursor.getColumnIndexOrThrow(
                                "ingredient_name"
                        )
                );

                double quantity = ingredientCursor.getDouble(
                        ingredientCursor.getColumnIndexOrThrow(
                                "required_quantity"
                        )
                );

                String unit = ingredientCursor.getString(
                        ingredientCursor.getColumnIndexOrThrow(
                                "unit"
                        )
                );

                ingredients.append("• ")
                        .append(quantity)
                        .append(" ")
                        .append(unit)
                        .append(" ")
                        .append(ingredientName)
                        .append("\n");

            } while (ingredientCursor.moveToNext());
        }

        ingredientCursor.close();

        txtRecipeIngredients.setText(
                ingredients.toString()
        );
    }
}