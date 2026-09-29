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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AlmostThereActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    private RecyclerView recyclerAlmostThere;
    private TextView txtAlmostThereInfo;

    private RecipeAdapter recipeAdapter;
    private List<Recipe> almostThereRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_almost_there);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
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

        recyclerAlmostThere =
                findViewById(R.id.recyclerAlmostThere);

        txtAlmostThereInfo =
                findViewById(R.id.txtAlmostThereInfo);

        recyclerAlmostThere.setLayoutManager(
                new LinearLayoutManager(this)
        );

        almostThereRecipes = new ArrayList<>();

        recipeAdapter =
                new RecipeAdapter(almostThereRecipes);

        recyclerAlmostThere.setAdapter(recipeAdapter);

        databaseHelper =
                new DatabaseHelper(this);

        loadAlmostThereRecipes();
    }

    private void loadAlmostThereRecipes() {

        almostThereRecipes.clear();

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
                                cursor.getColumnIndexOrThrow(
                                        "id"
                                )
                        );

                String recipeName =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "name"
                                )
                        );

                String instructions =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "instructions"
                                )
                        );

                // Only add recipes that are missing exactly one ingredient.
                if (recipeMatcher.isAlmostThere(recipeId)) {

                    String missingIngredient =
                            recipeMatcher.getMissingIngredient(recipeId);

                    Recipe recipe =
                            new Recipe(
                                    recipeId,
                                    recipeName,
                                    instructions,
                                    missingIngredient
                            );

                    almostThereRecipes.add(recipe);
                }

            } while (cursor.moveToNext());
        }

        cursor.close();

        recipeAdapter.notifyDataSetChanged();

        if (almostThereRecipes.isEmpty()) {

            txtAlmostThereInfo.setText(
                    "No recipes are missing only one ingredient."
            );

        } else {

            txtAlmostThereInfo.setText(
                    "These recipes are missing only one ingredient:"
            );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null &&
                recipeAdapter != null) {

            loadAlmostThereRecipes();
        }
    }
}