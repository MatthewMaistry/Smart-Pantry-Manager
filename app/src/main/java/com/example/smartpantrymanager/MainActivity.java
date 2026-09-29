package com.example.smartpantrymanager;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    private static final String PREF_NAME = "SmartPantrySettings";
    private static final String EXPIRY_ALERTS = "expiry_alerts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        SQLiteDatabase database =
                databaseHelper.getWritableDatabase();

        databaseHelper.checkAndSeedRecipes();

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

        findViewById(R.id.btnPantry).setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    PantryActivity.class
            );

            startActivity(intent);
        });

        findViewById(R.id.btnRecipes).setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    SuggestedRecipesActivity.class
            );

            startActivity(intent);
        });

        findViewById(R.id.btnCollection).setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    RecipeCollectionActivity.class
            );

            startActivity(intent);
        });

        findViewById(R.id.btnSettings).setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    SettingsActivity.class
            );

            startActivity(intent);
        });

        checkExpiryAlerts();
    }

    private void checkExpiryAlerts() {

        SharedPreferences preferences =
                getSharedPreferences(
                        PREF_NAME,
                        MODE_PRIVATE
                );

        boolean expiryAlertsEnabled =
                preferences.getBoolean(
                        EXPIRY_ALERTS,
                        false
                );

        if (!expiryAlertsEnabled) {
            return;
        }

        SQLiteDatabase database =
                databaseHelper.getReadableDatabase();

        Cursor cursor = database.rawQuery(
                "SELECT name, expiry_date " +
                        "FROM pantry " +
                        "WHERE expiry_date IS NOT NULL " +
                        "AND expiry_date != ''",
                null
        );

        List<String> expiredIngredients =
                new ArrayList<>();

        List<String> expiringSoonIngredients =
                new ArrayList<>();

        Calendar today = Calendar.getInstance();

        // Remove the current time so that we compare dates only.
        today.set(
                Calendar.HOUR_OF_DAY,
                0
        );
        today.set(
                Calendar.MINUTE,
                0
        );
        today.set(
                Calendar.SECOND,
                0
        );
        today.set(
                Calendar.MILLISECOND,
                0
        );

        Calendar threeDaysFromNow =
                (Calendar) today.clone();

        threeDaysFromNow.add(
                Calendar.DAY_OF_YEAR,
                3
        );

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                );

        dateFormat.setLenient(false);

        if (cursor.moveToFirst()) {

            do {

                String name =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "name"
                                )
                        );

                String expiryDate =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "expiry_date"
                                )
                        );

                try {

                    Date parsedDate =
                            dateFormat.parse(expiryDate);

                    if (parsedDate != null) {

                        Calendar expiryCalendar =
                                Calendar.getInstance();

                        expiryCalendar.setTime(
                                parsedDate
                        );

                        expiryCalendar.set(
                                Calendar.HOUR_OF_DAY,
                                0
                        );
                        expiryCalendar.set(
                                Calendar.MINUTE,
                                0
                        );
                        expiryCalendar.set(
                                Calendar.SECOND,
                                0
                        );
                        expiryCalendar.set(
                                Calendar.MILLISECOND,
                                0
                        );

                        if (expiryCalendar.before(today)) {

                            expiredIngredients.add(
                                    name +
                                            " - expired on " +
                                            expiryDate
                            );

                        } else if (
                                !expiryCalendar.after(
                                        threeDaysFromNow
                                )
                        ) {

                            expiringSoonIngredients.add(
                                    name +
                                            " - expires on " +
                                            expiryDate
                            );
                        }
                    }

                } catch (ParseException e) {
                    // Ignore invalid expiry dates
                }

            } while (cursor.moveToNext());
        }

        cursor.close();

        if (!expiredIngredients.isEmpty() ||
                !expiringSoonIngredients.isEmpty()) {

            showExpiryAlert(
                    expiredIngredients,
                    expiringSoonIngredients
            );
        }
    }

    private void showExpiryAlert(
            List<String> expiredIngredients,
            List<String> expiringSoonIngredients) {

        StringBuilder message =
                new StringBuilder();

        if (!expiredIngredients.isEmpty()) {

            message.append("Already expired:\n\n");

            for (String ingredient :
                    expiredIngredients) {

                message.append("• ")
                        .append(ingredient)
                        .append("\n");
            }
        }

        if (!expiredIngredients.isEmpty() &&
                !expiringSoonIngredients.isEmpty()) {

            message.append("\n");
        }

        if (!expiringSoonIngredients.isEmpty()) {

            message.append(
                    "Expiring within 3 days:\n\n"
            );

            for (String ingredient :
                    expiringSoonIngredients) {

                message.append("• ")
                        .append(ingredient)
                        .append("\n");
            }
        }

        message.append(
                "\nPlease use these ingredients soon."
        );

        new AlertDialog.Builder(this)
                .setTitle("Expiry Alert")
                .setMessage(message.toString())
                .setPositiveButton(
                        "OK",
                        null
                )
                .show();
    }
}