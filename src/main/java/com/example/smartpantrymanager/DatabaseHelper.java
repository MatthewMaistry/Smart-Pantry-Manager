package com.example.smartpantrymanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SmartPantry.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void checkAndSeedRecipes() {

        SQLiteDatabase db = getWritableDatabase();

        // Check if detailed instructions column already exists
        Cursor columnCursor = db.rawQuery(
                "PRAGMA table_info(recipes)",
                null
        );

        boolean detailedInstructionsExists = false;

        if (columnCursor.moveToFirst()) {

            do {

                String columnName =
                        columnCursor.getString(
                                columnCursor.getColumnIndexOrThrow("name")
                        );

                if (columnName.equals("detailed_instructions")) {
                    detailedInstructionsExists = true;
                    break;
                }

            } while (columnCursor.moveToNext());
        }

        columnCursor.close();

        // Add detailed instructions to existing databases
        if (!detailedInstructionsExists) {

            db.execSQL(
                    "ALTER TABLE recipes " +
                            "ADD COLUMN detailed_instructions TEXT"
            );
        }

        // Check if recipes already exist
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM recipes",
                null
        );

        int recipeCount = 0;

        if (cursor.moveToFirst()) {
            recipeCount = cursor.getInt(0);
        }

        cursor.close();

        // Seed recipes if database is empty
        if (recipeCount == 0) {
            seedRecipes(db);
        }

        ContentValues recipeNameValues = new ContentValues();
        recipeNameValues.put("name", "Tomato & Cheese Sandwich");

        db.update(
                "recipes",
                recipeNameValues,
                "name = ?",
                new String[]{"Tomato Sandwich"}
        );

        ContentValues breadValues = new ContentValues();
        breadValues.put("unit", "slices");

        db.update(
                "recipe_ingredients",
                breadValues,
                "ingredient_name = ? AND unit = ?",
                new String[]{"bread", "pieces"}
        );

        updateDetailedInstructions(
                db,
                "Scrambled Eggs",
                "Crack the eggs into a bowl and whisk them with the milk. Heat a frying pan on the stove and add the butter. Pour in the egg mixture and stir gently until the eggs are cooked but still soft."
        );

        updateDetailedInstructions(
                db,
                "Omelette",
                "Beat the eggs and milk together in a bowl. Heat a frying pan and pour in the mixture. Add the cheese and cook until the eggs are set and the cheese has melted. Fold the omelette in half and serve."
        );

        updateDetailedInstructions(
                db,
                "Tomato & Cheese Sandwich",
                "Slice the tomato into thin pieces. Place the tomato and cheese on two slices of bread. Add the remaining slice of bread and serve fresh, or toast the sandwich until the bread is lightly golden."
        );

        updateDetailedInstructions(
                db,
                "Grilled Cheese",
                "Heat a frying pan on the stove. Place the cheese between two slices of bread and spread butter on the outside of the bread. Place the sandwich in the pan and cook both sides until golden and the cheese has melted."
        );

        updateDetailedInstructions(
                db,
                "Pancakes",
                "Add the flour, eggs, milk and sugar to a bowl and mix until a smooth batter forms. Heat a frying pan and lightly grease it with butter or oil. Pour a small amount of batter into the pan and cook until bubbles form. Flip the pancake and cook the other side until golden."
        );

        updateDetailedInstructions(
                db,
                "French Toast",
                "Heat a frying pan on the stove with some oil or butter. Beat the eggs and milk together in a bowl. Dip each slice of bread into the mixture and fry on both sides until golden."
        );

        updateDetailedInstructions(
                db,
                "Spaghetti Tomato",
                "Bring a pot of water to the boil and cook the spaghetti according to the packet instructions. Chop the tomatoes and onion. Heat a frying pan with a little oil and cook the onion and tomato until soft. Add the cooked spaghetti and mix everything together before serving."
        );

        updateDetailedInstructions(
                db,
                "Fried Rice",
                "Cook the rice until tender and drain if necessary. Chop the onion and carrot. Heat a frying pan with a little oil and cook the vegetables. Add the eggs and stir until cooked. Add the rice and mix everything together before serving."
        );

        updateDetailedInstructions(
                db,
                "Chicken Rice",
                "Cook the rice until tender. Cut the chicken into small pieces and chop the onion. Heat a frying pan and cook the chicken and onion until the chicken is fully cooked. Add the cooked rice and mix everything together."
        );

        updateDetailedInstructions(
                db,
                "Chicken Pasta",
                "Bring a pot of water to the boil and cook the pasta until tender. Cut the chicken into small pieces and cook it in a frying pan until fully cooked. Add the chopped tomato and cook until soft. Add the pasta and mix everything together."
        );

        updateDetailedInstructions(
                db,
                "Tuna Sandwich",
                "Drain the tuna and place it in a bowl. Add the mayonnaise and mix well. Spread the tuna mixture evenly over two slices of bread. Add the remaining bread slice and serve."
        );

        updateDetailedInstructions(
                db,
                "Vegetable Stir-Fry",
                "Wash and cut the carrot, onion and pepper into small pieces. Heat a frying pan with a little oil and stir-fry the vegetables until they are tender but still slightly crisp. Add the soy sauce and stir well before serving."
        );

        updateDetailedInstructions(
                db,
                "Potato Omelette",
                "Peel and slice the potatoes and onion. Heat a frying pan with a little oil and cook the potatoes and onion until soft. Beat the eggs in a bowl and pour them over the vegetables. Cook on low heat until the eggs are set, then carefully turn or fold the omelette."
        );

        updateDetailedInstructions(
                db,
                "Tomato Soup",
                "Chop the tomatoes and onion into small pieces. Heat the butter in a pot and cook the onion until soft. Add the tomatoes and cook for a few minutes. Add water and simmer until the vegetables are soft, then blend until smooth."
        );

        updateDetailedInstructions(
                db,
                "Chicken Salad",
                "Cut the chicken into small pieces and cook it in a frying pan until fully cooked. Allow the chicken to cool. Wash and chop the lettuce and tomato, then mix them with the chicken and serve."
        );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Pantry table
        db.execSQL("CREATE TABLE pantry (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "quantity REAL NOT NULL, " +
                "unit TEXT NOT NULL, " +
                "expiry_date TEXT)");

        db.execSQL("CREATE TABLE recipes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "instructions TEXT NOT NULL, " +
                "detailed_instructions TEXT)");

        db.execSQL("CREATE TABLE recipe_ingredients (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "recipe_id INTEGER NOT NULL, " +
                "ingredient_name TEXT NOT NULL, " +
                "required_quantity REAL NOT NULL, " +
                "unit TEXT NOT NULL, " +
                "FOREIGN KEY(recipe_id) REFERENCES recipes(id))");

        seedRecipes(db);
    }

    private void seedRecipes(SQLiteDatabase db) {

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM recipes",
                null
        );

        if (cursor.moveToFirst()) {

            int recipeCount = cursor.getInt(0);

            if (recipeCount > 0) {
                cursor.close();
                return;
            }
        }

        cursor.close();

        // Recipe 1
        long recipeId = insertRecipe(
                db,
                "Scrambled Eggs",
                "Crack the eggs into a bowl and whisk them. Heat butter in a pan, add the eggs and stir until cooked.",
                "Crack the eggs into a bowl and whisk them with the milk. Heat a frying pan on the stove and add the butter. Pour in the egg mixture and stir gently until the eggs are cooked but still soft."
        );

        insertIngredient(db, recipeId, "eggs", 2, "pieces");
        insertIngredient(db, recipeId, "milk", 30, "ml");
        insertIngredient(db, recipeId, "butter", 10, "g");

        // Recipe 2
        recipeId = insertRecipe(
                db,
                "Omelette",
                "Beat the eggs with milk. Pour into a heated pan, add cheese and cook until the eggs are set.",
                "Beat the eggs and milk together in a bowl. Heat a frying pan and pour in the mixture. Add the cheese and cook until the eggs are set and the cheese has melted. Fold the omelette in half and serve."
        );

        insertIngredient(db, recipeId, "eggs", 2, "pieces");
        insertIngredient(db, recipeId, "milk", 30, "ml");
        insertIngredient(db, recipeId, "cheese", 30, "g");

        // Recipe 3
        recipeId = insertRecipe(
                db,
                "Tomato & Cheese Sandwich",
                "Place sliced tomato and cheese between two slices of bread. Toast or serve fresh.",
                "Slice the tomato into thin pieces. Place the tomato and cheese on two slices of bread. Add the remaining slice of bread and serve fresh, or toast the sandwich until the bread is lightly golden."
        );

        insertIngredient(db, recipeId, "bread", 2, "slices");
        insertIngredient(db, recipeId, "tomato", 1, "piece");
        insertIngredient(db, recipeId, "cheese", 30, "g");

        // Recipe 4
        recipeId = insertRecipe(
                db,
                "Grilled Cheese",
                "Place cheese between two slices of bread. Spread butter on the outside and grill until golden.",
                "Heat a frying pan on the stove. Place the cheese between two slices of bread and spread butter on the outside of the bread. Place the sandwich in the pan and cook both sides until golden and the cheese has melted."
        );

        insertIngredient(db, recipeId, "bread", 2, "slices");
        insertIngredient(db, recipeId, "cheese", 40, "g");
        insertIngredient(db, recipeId, "butter", 10, "g");

        // Recipe 5
        recipeId = insertRecipe(
                db,
                "Pancakes",
                "Mix the flour, eggs, milk and sugar into a smooth batter. Cook small portions in a heated pan until golden.",
                "Add the flour, eggs, milk and sugar to a bowl and mix until a smooth batter forms. Heat a frying pan and lightly grease it with butter or oil. Pour a small amount of batter into the pan and cook until bubbles form. Flip the pancake and cook the other side until golden."
        );

        insertIngredient(db, recipeId, "flour", 100, "g");
        insertIngredient(db, recipeId, "eggs", 2, "pieces");
        insertIngredient(db, recipeId, "milk", 150, "ml");
        insertIngredient(db, recipeId, "sugar", 20, "g");

        // Recipe 6
        recipeId = insertRecipe(
                db,
                "French Toast",
                "Beat the eggs and milk together. Dip the bread into the mixture and fry both sides until golden.",
                "Heat a frying pan on the stove with some oil or butter. Beat the eggs and milk together in a bowl. Dip each slice of bread into the mixture and fry on both sides until golden."
        );

        insertIngredient(db, recipeId, "bread", 2, "slices");
        insertIngredient(db, recipeId, "eggs", 2, "pieces");
        insertIngredient(db, recipeId, "milk", 50, "ml");

        // Recipe 7
        recipeId = insertRecipe(
                db,
                "Spaghetti Tomato",
                "Cook the spaghetti. Fry the onion and tomato, then add the cooked pasta and mix together.",
                "Bring a pot of water to the boil and cook the spaghetti according to the packet instructions. Chop the tomatoes and onion. Heat a frying pan with a little oil and cook the onion and tomato until soft. Add the cooked spaghetti and mix everything together before serving."
        );

        insertIngredient(db, recipeId, "spaghetti", 100, "g");
        insertIngredient(db, recipeId, "tomato", 2, "pieces");
        insertIngredient(db, recipeId, "onion", 1, "piece");

        // Recipe 8
        recipeId = insertRecipe(
                db,
                "Fried Rice",
                "Cook the rice. Fry the onion, carrot and eggs, then add the rice and stir everything together.",
                "Cook the rice until tender and drain if necessary. Chop the onion and carrot. Heat a frying pan with a little oil and cook the vegetables. Add the eggs and stir until cooked. Add the rice and mix everything together before serving."
        );

        insertIngredient(db, recipeId, "rice", 150, "g");
        insertIngredient(db, recipeId, "eggs", 2, "pieces");
        insertIngredient(db, recipeId, "carrot", 1, "piece");
        insertIngredient(db, recipeId, "onion", 1, "piece");

        // Recipe 9
        recipeId = insertRecipe(
                db,
                "Chicken Rice",
                "Cook the rice. Fry the chicken and onion until cooked, then mix with the rice.",
                "Cook the rice until tender. Cut the chicken into small pieces and chop the onion. Heat a frying pan and cook the chicken and onion until the chicken is fully cooked. Add the cooked rice and mix everything together."
        );

        insertIngredient(db, recipeId, "chicken", 150, "g");
        insertIngredient(db, recipeId, "rice", 150, "g");
        insertIngredient(db, recipeId, "onion", 1, "piece");

        // Recipe 10
        recipeId = insertRecipe(
                db,
                "Chicken Pasta",
                "Cook the pasta. Cook the chicken with tomato, then add the pasta and mix together.",
                "Bring a pot of water to the boil and cook the pasta until tender. Cut the chicken into small pieces and cook it in a frying pan until fully cooked. Add the chopped tomato and cook until soft. Add the pasta and mix everything together."
        );

        insertIngredient(db, recipeId, "chicken", 150, "g");
        insertIngredient(db, recipeId, "pasta", 100, "g");
        insertIngredient(db, recipeId, "tomato", 2, "pieces");

        // Recipe 11
        recipeId = insertRecipe(
                db,
                "Tuna Sandwich",
                "Mix the tuna with mayonnaise. Spread the mixture between two slices of bread.",
                "Drain the tuna and place it in a bowl. Add the mayonnaise and mix well. Spread the tuna mixture evenly over two slices of bread. Add the remaining bread slice and serve."
        );

        insertIngredient(db, recipeId, "bread", 2, "slices");
        insertIngredient(db, recipeId, "tuna", 1, "can");
        insertIngredient(db, recipeId, "mayonnaise", 20, "g");

        // Recipe 12
        recipeId = insertRecipe(
                db,
                "Vegetable Stir-Fry",
                "Cut the vegetables into small pieces. Fry the vegetables and add soy sauce before serving.",
                "Wash and cut the carrot, onion and pepper into small pieces. Heat a frying pan with a little oil and stir-fry the vegetables until they are tender but still slightly crisp. Add the soy sauce and stir well before serving."
        );

        insertIngredient(db, recipeId, "carrot", 1, "piece");
        insertIngredient(db, recipeId, "onion", 1, "piece");
        insertIngredient(db, recipeId, "pepper", 1, "piece");
        insertIngredient(db, recipeId, "soy sauce", 15, "ml");

        // Recipe 13
        recipeId = insertRecipe(
                db,
                "Potato Omelette",
                "Cook the sliced potato and onion until soft. Add beaten eggs and cook until the omelette is set.",
                "Peel and slice the potatoes and onion. Heat a frying pan with a little oil and cook the potatoes and onion until soft. Beat the eggs in a bowl and pour them over the vegetables. Cook on low heat until the eggs are set, then carefully turn or fold the omelette."
        );

        insertIngredient(db, recipeId, "potato", 2, "pieces");
        insertIngredient(db, recipeId, "eggs", 3, "pieces");
        insertIngredient(db, recipeId, "onion", 1, "piece");

        // Recipe 14
        recipeId = insertRecipe(
                db,
                "Tomato Soup",
                "Cook the tomato and onion until soft. Add water and simmer, then blend until smooth.",
                "Chop the tomatoes and onion into small pieces. Heat the butter in a pot and cook the onion until soft. Add the tomatoes and cook for a few minutes. Add water and simmer until the vegetables are soft, then blend until smooth."
        );

        insertIngredient(db, recipeId, "tomato", 3, "pieces");
        insertIngredient(db, recipeId, "onion", 1, "piece");
        insertIngredient(db, recipeId, "butter", 10, "g");

        // Recipe 15
        recipeId = insertRecipe(
                db,
                "Chicken Salad",
                "Cook the chicken and allow it to cool. Mix it with lettuce and tomato and serve.",
                "Cut the chicken into small pieces and cook it in a frying pan until fully cooked. Allow the chicken to cool. Wash and chop the lettuce and tomato, then mix them with the chicken and serve."
        );

        insertIngredient(db, recipeId, "chicken", 150, "g");
        insertIngredient(db, recipeId, "lettuce", 50, "g");
        insertIngredient(db, recipeId, "tomato", 1, "piece");
    }

    private long insertRecipe(
            SQLiteDatabase db,
            String name,
            String instructions,
            String detailedInstructions) {

        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("instructions", instructions);
        values.put("detailed_instructions", detailedInstructions);

        return db.insert("recipes", null, values);
    }

    private void updateDetailedInstructions(
            SQLiteDatabase db,
            String recipeName,
            String detailedInstructions) {

        ContentValues values = new ContentValues();

        values.put(
                "detailed_instructions",
                detailedInstructions
        );

        db.update(
                "recipes",
                values,
                "name = ?",
                new String[]{recipeName}
        );
    }

    private void insertIngredient(
            SQLiteDatabase db,
            long recipeId,
            String ingredientName,
            double quantity,
            String unit) {

        ContentValues values = new ContentValues();

        values.put("recipe_id", recipeId);
        values.put("ingredient_name", ingredientName);
        values.put("required_quantity", quantity);
        values.put("unit", unit);

        db.insert("recipe_ingredients", null, values);
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS recipe_ingredients");
        db.execSQL("DROP TABLE IF EXISTS recipes");
        db.execSQL("DROP TABLE IF EXISTS pantry");

        onCreate(db);
    }
}