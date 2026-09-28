package com.example.smartpantrymanager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecipeCollectionActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerRecipes;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_collection);

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

        recyclerRecipes = findViewById(R.id.recyclerRecipes);

        recyclerRecipes.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recipes = new ArrayList<>();

        recipeAdapter = new RecipeAdapter(recipes);

        recyclerRecipes.setAdapter(recipeAdapter);

        databaseHelper = new DatabaseHelper(this);

        loadRecipes();
    }

    private void loadRecipes() {

        recipes.clear();

        SQLiteDatabase database =
                databaseHelper.getReadableDatabase();

        Cursor cursor = database.rawQuery(
                "SELECT id, name, instructions FROM recipes",
                null
        );

        if (cursor.moveToFirst()) {

            do {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")
                );

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow("name")
                );

                String instructions = cursor.getString(
                        cursor.getColumnIndexOrThrow("instructions")
                );

                Recipe recipe = new Recipe(
                        id,
                        name,
                        instructions
                );

                recipes.add(recipe);

            } while (cursor.moveToNext());
        }

        cursor.close();

        recipeAdapter.notifyDataSetChanged();
    }
}
