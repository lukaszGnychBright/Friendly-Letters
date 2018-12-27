package com.pg.mikszo.friendlyletters.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;

public class SettingsManager {
    private Context context;
    private final String settingsFile = "settings/settings.json";
    private final String sharedPreferencesPackage = "com.pg.mikszo.friendlyletters";
    private final String sharedPreferencesSourceFile = "friendlyletters";
    private final String sharedPreferencesDifficultyLevel = "difficultyLevel";
    private final String sharedPreferencesTimeLimit = "timeLimit";
    private final String sharedPreferencesNumberOfLevels = "numberOfLevels";
    private final String sharedPreferencesNumberOfRepetitions = "numberOfRepetitions";
    private final String sharedPreferencesTrackColor = "trackColor";
    private final String sharedPreferencesAvailableShapes = "availableShapes";

    public SettingsManager(Context context) {
        this.context = context;
    }

    public String getJSONFromSettings(Settings settings) {
        return new Gson().toJson(settings);
    }

    public Settings getSettingsFromJSON() {
        Settings settings = null;
        String json = readSettingsFromAsset();
        settings = new Gson().fromJson(json, Settings.class);
        return settings;
    }

    public Settings getAppSettings() {
        Settings settings = null;
        Settings defaultSettings = getSettingsFromJSON();
        try {
            Context packageContext = context.createPackageContext(sharedPreferencesPackage, Context.MODE_PRIVATE);
            SharedPreferences sharedPreferences = packageContext.getSharedPreferences(sharedPreferencesSourceFile, Context.MODE_PRIVATE);
            if (!areSettingsAdded(sharedPreferences)) {
                addSettingsToSharedPreferences(sharedPreferences, defaultSettings);
            }

            settings = new Settings();
            settings.difficultyLevel = sharedPreferences.getInt(
                    sharedPreferencesDifficultyLevel,
                    defaultSettings.difficultyLevel);
            settings.timeLimit = sharedPreferences.getInt(
                    sharedPreferencesTimeLimit,
                    defaultSettings.timeLimit);
            settings.numberOfLevels = sharedPreferences.getInt(
                    sharedPreferencesNumberOfLevels,
                    defaultSettings.numberOfLevels);
            settings.numberOfRepetitions = sharedPreferences.getInt(
                    sharedPreferencesNumberOfRepetitions,
                    defaultSettings.numberOfRepetitions);
            settings.trackColor = sharedPreferences.getString(
                    sharedPreferencesTrackColor,
                    defaultSettings.trackColor);
            settings.availableShapes = sharedPreferences.getString(
                    sharedPreferencesAvailableShapes,
                    convertAvailableShapesArrayToString(defaultSettings.availableShapes)).split(";");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return settings;
    }

    public void saveSettings(Settings settings) {
        try {
            Context packageContext = context.createPackageContext(sharedPreferencesPackage, Context.MODE_PRIVATE);
            SharedPreferences sharedPreferences = packageContext.getSharedPreferences(sharedPreferencesSourceFile, Context.MODE_PRIVATE);
            addSettingsToSharedPreferences(sharedPreferences, settings);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean areSettingsAdded(SharedPreferences sharedPreferences) {
        if (!sharedPreferences.contains(sharedPreferencesDifficultyLevel)) {
            return false;
        }
        if (!sharedPreferences.contains(sharedPreferencesTimeLimit)) {
            return false;
        }
        if (!sharedPreferences.contains(sharedPreferencesNumberOfLevels)) {
            return false;
        }
        if (!sharedPreferences.contains(sharedPreferencesNumberOfRepetitions)) {
            return false;
        }
        if (!sharedPreferences.contains(sharedPreferencesTrackColor)) {
            return false;
        }
        if (!sharedPreferences.contains(sharedPreferencesAvailableShapes)) {
            return false;
        }

        return true;
    }

    private void addSettingsToSharedPreferences(SharedPreferences sharedPreferences, Settings settings) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(sharedPreferencesDifficultyLevel, settings.difficultyLevel);
        editor.putInt(sharedPreferencesTimeLimit, settings.timeLimit);
        editor.putInt(sharedPreferencesNumberOfLevels, settings.numberOfLevels);
        editor.putInt(sharedPreferencesNumberOfRepetitions, settings.numberOfRepetitions);
        editor.putString(sharedPreferencesTrackColor, settings.trackColor);
        editor.putString(sharedPreferencesAvailableShapes, convertAvailableShapesArrayToString(settings.availableShapes));
        editor.apply();
    }

    private String readSettingsFromAsset() {
        String json = null;
        try {
            InputStream is = context.getAssets().open(settingsFile);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }

    private String convertAvailableShapesArrayToString(String[] availableShapes) {
        StringBuilder result = new StringBuilder();
        for(String shape : availableShapes) {
            result.append(shape).append(";");
        }
        return result.toString();
    }
}