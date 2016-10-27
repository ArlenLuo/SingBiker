package sg.ntu.dataminers.singbiker.control;

import android.content.Context;
import android.content.SharedPreferences;

import sg.ntu.dataminers.singbiker.entity.Settings;

public class SettingsManager {

    private static final String FILE_NAME = "User App Settings";
    private static final String UNIT_SYSTEM = "unitSystem";
    private static final String COLOR_PCN = "colorPCN";
    private static final String COLOR_NON_PCN = "colorNonPCN";

    private static SharedPreferences sharedPref;

    public static void saveSettings(Context context) {
        sharedPref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putBoolean(UNIT_SYSTEM, Settings.isUnitSystemKM());
        editor.putString(COLOR_PCN, Settings.getColorPCN());
        editor.putString(COLOR_NON_PCN, Settings.getColorNonPCN());

        editor.apply();
    }

    public static void loadSettings(Context context) {
        sharedPref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);

        Settings.setUnitSystem(sharedPref.getBoolean(UNIT_SYSTEM, false));
        Settings.setColorPCN(sharedPref.getString(COLOR_PCN, null));
        Settings.setColorNonPCN(sharedPref.getString(COLOR_NON_PCN, null));
    }
}
