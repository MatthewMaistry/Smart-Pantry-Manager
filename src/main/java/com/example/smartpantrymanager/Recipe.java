package com.example.smartpantrymanager;

public class Recipe {

    private int id;
    private String name;
    private String instructions;
    private String missingIngredient;

    // Used for normal recipes
    public Recipe(int id, String name, String instructions) {
        this.id = id;
        this.name = name;
        this.instructions = instructions;
        this.missingIngredient = "";
    }

    // Used for Almost There recipes
    public Recipe(
            int id,
            String name,
            String instructions,
            String missingIngredient) {

        this.id = id;
        this.name = name;
        this.instructions = instructions;
        this.missingIngredient = missingIngredient;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getMissingIngredient() {
        return missingIngredient;
    }
}