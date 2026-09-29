package com.example.smartpantrymanager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    private RecyclerView recyclerSuggestedRecipes;
    private TextView txtNoRecipes;

    private RecipeAdapter recipeAdapter;
    private List<Recipe> suggestedRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_suggested_recipes);

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

        recyclerSuggestedRecipes =
                findViewById(R.id.recyclerSuggestedRecipes);

        txtNoRecipes =
                findViewById(R.id.txtNoRecipes);

        recyclerSuggestedRecipes.setLayoutManager(
                new LinearLayoutManager(this)
        );

        suggestedRecipes = new ArrayList<>();

        recipeAdapter =
                new RecipeAdapter(suggestedRecipes);

        recyclerSuggestedRecipes.setAdapter(recipeAdapter);

        databaseHelper = new DatabaseHelper(this);

        loadSuggestedRecipes();
        findViewById(R.id.btnAlmostThere).setOnClickListener(v -> {

            Intent intent = new Intent(
                    SuggestedRecipesActivity.this,
                    AlmostThereActivity.class
            );

            startActivity(intent);
        });
    }

    private void loadSuggestedRecipes() {

        suggestedRecipes.clear();

        SQLiteDatabase database =
                databaseHelper.getReadableDatabase();

        RecipeMatcher recipeMatcher =
                new RecipeMatcher(database);

        Cursor cursor = database.rawQuery(
                "SELECT id, name, instructions FROM recipes",
                null
        );

        if (cursor.moveToFirst()) {

            do {

                int recipeId =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow("id")
                        );

                String recipeName =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("name")
                        );

                String instructions =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "instructions"
                                )
                        );

                // Only add the recipe if every required
                // ingredient is available.
                if (recipeMatcher.canMakeRecipe(recipeId)) {

                    Recipe recipe = new Recipe(
                            recipeId,
                            recipeName,
                            instructions
                    );

                    suggestedRecipes.add(recipe);
                }

            } while (cursor.moveToNext());
        }

        cursor.close();

        recipeAdapter.notifyDataSetChanged();

        if (suggestedRecipes.isEmpty()) {

            txtNoRecipes.setText(
                    "No recipes can be made with current pantry."
            );

        } else {

            txtNoRecipes.setText(
                    "Recipes you can make with current pantry:"
            );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null &&
                recipeAdapter != null) {

            loadSuggestedRecipes();
        }
    }
}