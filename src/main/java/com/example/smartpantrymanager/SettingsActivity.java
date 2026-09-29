package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchExpiryAlerts;

    private SharedPreferences preferences;

    private static final String PREF_NAME = "SmartPantrySettings";
    private static final String EXPIRY_ALERTS = "expiry_alerts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_settings);

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

        switchExpiryAlerts =
                findViewById(R.id.switchExpiryAlerts);

        preferences = getSharedPreferences(
                PREF_NAME,
                MODE_PRIVATE
        );

        boolean expiryAlertsEnabled =
                preferences.getBoolean(
                        EXPIRY_ALERTS,
                        false
                );

        switchExpiryAlerts.setChecked(
                expiryAlertsEnabled
        );

        switchExpiryAlerts.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    preferences.edit()
                            .putBoolean(
                                    EXPIRY_ALERTS,
                                    isChecked
                            )
                            .apply();
                }
        );
    }
}
