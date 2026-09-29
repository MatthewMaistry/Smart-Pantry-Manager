package com.example.smartpantrymanager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class RecipeMatcher {

    private SQLiteDatabase database;

    public RecipeMatcher(SQLiteDatabase database) {
        this.database = database;
    }

    public boolean canMakeRecipe(int recipeId) {

        Cursor recipeIngredients = database.rawQuery(
                "SELECT ingredient_name, required_quantity, unit " +
                        "FROM recipe_ingredients " +
                        "WHERE recipe_id = ?",
                new String[]{String.valueOf(recipeId)}
        );

        if (!recipeIngredients.moveToFirst()) {
            recipeIngredients.close();
            return false;
        }

        do {

            String requiredIngredient =
                    recipeIngredients.getString(
                            recipeIngredients.getColumnIndexOrThrow(
                                    "ingredient_name"
                            )
                    );

            double requiredQuantity =
                    recipeIngredients.getDouble(
                            recipeIngredients.getColumnIndexOrThrow(
                                    "required_quantity"
                            )
                    );

            String requiredUnit =
                    recipeIngredients.getString(
                            recipeIngredients.getColumnIndexOrThrow(
                                    "unit"
                            )
                    );

            Cursor pantryIngredients = database.rawQuery(
                    "SELECT quantity, unit FROM pantry " +
                            "WHERE LOWER(name) = LOWER(?)",
                    new String[]{requiredIngredient}
            );

            double totalAvailable = 0;

            if (pantryIngredients.moveToFirst()) {

                do {

                    double pantryQuantity =
                            pantryIngredients.getDouble(
                                    pantryIngredients.getColumnIndexOrThrow(
                                            "quantity"
                                    )
                            );

                    String pantryUnit =
                            pantryIngredients.getString(
                                    pantryIngredients.getColumnIndexOrThrow(
                                            "unit"
                                    )
                            );

                    double convertedQuantity =
                            convertQuantity(
                                    pantryQuantity,
                                    pantryUnit,
                                    requiredUnit,
                                    requiredIngredient
                            );

                    totalAvailable += convertedQuantity;

                } while (pantryIngredients.moveToNext());
            }

            pantryIngredients.close();

            if (totalAvailable < requiredQuantity) {

                recipeIngredients.close();
                return false;
            }

        } while (recipeIngredients.moveToNext());

        recipeIngredients.close();

        return true;
    }

    public boolean isAlmostThere(int recipeId) {

        Cursor recipeIngredients = database.rawQuery(
                "SELECT ingredient_name, required_quantity, unit " +
                        "FROM recipe_ingredients " +
                        "WHERE recipe_id = ?",
                new String[]{String.valueOf(recipeId)}
        );

        if (!recipeIngredients.moveToFirst()) {
            recipeIngredients.close();
            return false;
        }

        int missingIngredients = 0;

        do {

            String requiredIngredient =
                    recipeIngredients.getString(
                            recipeIngredients.getColumnIndexOrThrow(
                                    "ingredient_name"
                            )
                    );

            double requiredQuantity =
                    recipeIngredients.getDouble(
                            recipeIngredients.getColumnIndexOrThrow(
                                    "required_quantity"
                            )
                    );

            String requiredUnit =
                    recipeIngredients.getString(
                            recipeIngredients.getColumnIndexOrThrow(
                                    "unit"
                            )
                    );

            Cursor pantryIngredients = database.rawQuery(
                    "SELECT quantity, unit FROM pantry " +
                            "WHERE LOWER(name) = LOWER(?)",
                    new String[]{requiredIngredient}
            );

            double totalAvailable = 0;

            if (pantryIngredients.moveToFirst()) {

                do {

                    double pantryQuantity =
                            pantryIngredients.getDouble(
                                    pantryIngredients.getColumnIndexOrThrow(
                                            "quantity"
                                    )
                            );

                    String pantryUnit =
                            pantryIngredients.getString(
                                    pantryIngredients.getColumnIndexOrThrow(
                                            "unit"
                                    )
                            );

                    double convertedQuantity =
                            convertQuantity(
                                    pantryQuantity,
                                    pantryUnit,
                                    requiredUnit,
                                    requiredIngredient
                            );

                    totalAvailable += convertedQuantity;

                } while (pantryIngredients.moveToNext());
            }

            pantryIngredients.close();

            // This ingredient is missing or there is not enough.
            if (totalAvailable < requiredQuantity) {

                missingIngredients++;

                // More than one missing ingredient means
                // this recipe is not "Almost There".
                if (missingIngredients > 1) {

                    recipeIngredients.close();
                    return false;
                }
            }

        } while (recipeIngredients.moveToNext());

        recipeIngredients.close();

        // Exactly one ingredient must be missing.
        return missingIngredients == 1;
    }

    public String getMissingIngredient(int recipeId) {

        Cursor recipeIngredients = database.rawQuery(
                "SELECT ingredient_name, required_quantity, unit " +
                        "FROM recipe_ingredients " +
                        "WHERE recipe_id = ?",
                new String[]{String.valueOf(recipeId)}
        );

        if (!recipeIngredients.moveToFirst()) {
            recipeIngredients.close();
            return "";
        }

        do {

            String requiredIngredient =
                    recipeIngredients.getString(
                            recipeIngredients.getColumnIndexOrThrow(
                                    "ingredient_name"
                            )
                    );

            double requiredQuantity =
                    recipeIngredients.getDouble(
                            recipeIngredients.getColumnIndexOrThrow(
                                    "required_quantity"
                            )
                    );

            String requiredUnit =
                    recipeIngredients.getString(
                            recipeIngredients.getColumnIndexOrThrow(
                                    "unit"
                            )
                    );

            Cursor pantryIngredients = database.rawQuery(
                    "SELECT quantity, unit FROM pantry " +
                            "WHERE LOWER(name) = LOWER(?)",
                    new String[]{requiredIngredient}
            );

            double totalAvailable = 0;

            if (pantryIngredients.moveToFirst()) {

                do {

                    double pantryQuantity =
                            pantryIngredients.getDouble(
                                    pantryIngredients.getColumnIndexOrThrow(
                                            "quantity"
                                    )
                            );

                    String pantryUnit =
                            pantryIngredients.getString(
                                    pantryIngredients.getColumnIndexOrThrow(
                                            "unit"
                                    )
                            );

                    double convertedQuantity =
                            convertQuantity(
                                    pantryQuantity,
                                    pantryUnit,
                                    requiredUnit,
                                    requiredIngredient
                            );

                    totalAvailable += convertedQuantity;

                } while (pantryIngredients.moveToNext());
            }

            pantryIngredients.close();

            if (totalAvailable < requiredQuantity) {

                recipeIngredients.close();

                return requiredIngredient;
            }

        } while (recipeIngredients.moveToNext());

        recipeIngredients.close();

        return "";
    }

    private double convertQuantity(
            double quantity,
            String fromUnit,
            String toUnit,
            String ingredientName) {

        fromUnit = normaliseUnit(fromUnit);
        toUnit = normaliseUnit(toUnit);

        ingredientName = ingredientName.trim().toLowerCase();

        // Same unit
        if (fromUnit.equals(toUnit)) {
            return quantity;
        }

        // Litres to millilitres
        if (fromUnit.equals("l") && toUnit.equals("ml")) {
            return quantity * 1000;
        }

        // Millilitres to litres
        if (fromUnit.equals("ml") && toUnit.equals("l")) {
            return quantity / 1000;
        }

        // Kilograms to grams
        if (fromUnit.equals("kg") && toUnit.equals("g")) {
            return quantity * 1000;
        }

        // Grams to kilograms
        if (fromUnit.equals("g") && toUnit.equals("kg")) {
            return quantity / 1000;
        }

        // Pieces
        if (fromUnit.equals("piece") &&
                toUnit.equals("piece")) {
            return quantity;
        }

        // Cheese conversion
        // The app uses 1 cheese slice = 20 grams.
        if (ingredientName.equals("cheese")) {

            // Cheese slices to grams
            if (fromUnit.equals("slice") &&
                    toUnit.equals("g")) {

                return quantity * 20;
            }

            // Cheese grams to slices
            if (fromUnit.equals("g") &&
                    toUnit.equals("slice")) {

                return quantity / 20;
            }
        }

        // Units that cannot be converted
        return 0;
    }

    private String normaliseUnit(String unit) {

        unit = unit.trim().toLowerCase();

        if (unit.equals("litre") ||
                unit.equals("litres") ||
                unit.equals("liter") ||
                unit.equals("liters")) {

            return "l";
        }

        if (unit.equals("millilitre") ||
                unit.equals("millilitres") ||
                unit.equals("milliliter") ||
                unit.equals("milliliters")) {

            return "ml";
        }

        if (unit.equals("gram") ||
                unit.equals("grams")) {

            return "g";
        }

        if (unit.equals("kilogram") ||
                unit.equals("kilograms")) {

            return "kg";
        }

        if (unit.equals("pieces") ||
                unit.equals("piece")) {

            return "piece";
        }

        if (unit.equals("slice") ||
                unit.equals("slices")) {

            return "slice";
        }

        return unit;
    }
}
