package sg.ntu.dataminers.singbiker.control;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import sg.ntu.dataminers.singbiker.entity.Settings;

public class SettingsManager {

    private static final boolean DEFAULT_UNIT_SYSTEM = true;
    private static final int DEFAULT_COLOR_PCN = Color.argb(255, 0, 0, 255);
    private static final int DEFAULT_COLOR_NON_PCN = Color.argb(255, 0, 255, 255);

    private static final String FILE_NAME = "User App Settings";
    private static final String UNIT_SYSTEM = "unitSystem";
    private static final String COLOR_PCN = "colorPCN";
    private static final String COLOR_NON_PCN = "colorNonPCN";

    private static SharedPreferences sharedPref;

    public static void saveSettings(Context context) {
        sharedPref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putBoolean(UNIT_SYSTEM, Settings.isUnitSystemKM());
        editor.putInt(COLOR_PCN, Settings.getColorPCN());
        editor.putInt(COLOR_NON_PCN, Settings.getColorNonPCN());

        editor.apply();
    }

    public static void loadSettings(Context context) {
        sharedPref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);

        Settings.setUnitSystem(sharedPref.getBoolean(UNIT_SYSTEM, DEFAULT_UNIT_SYSTEM));
        Settings.setColorPCN(sharedPref.getInt(COLOR_PCN, DEFAULT_COLOR_PCN));
        Settings.setColorNonPCN(sharedPref.getInt(COLOR_NON_PCN, DEFAULT_COLOR_NON_PCN));
    }
}
