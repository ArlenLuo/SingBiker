package sg.ntu.dataminers.singbiker.entity;

import android.graphics.Color;

public class Settings {

    private static boolean unitSystemKM = true;
    private static int colorPCN = Color.argb(255, 255, 0, 0);
    private static int colorNonPCN = Color.argb(255, 125, 255, 255);

    public static boolean isUnitSystemKM() {
        return unitSystemKM;
    }

    public static void setUnitSystem(boolean preferredUnitSystem) {
        Settings.unitSystemKM = preferredUnitSystem;
    }

    public static int getColorPCN() {
        return colorPCN;
    }

    public static void setColorPCN(int colorPCN) {
        Settings.colorPCN = colorPCN;
    }

    public static int getColorNonPCN() {
        return colorNonPCN;
    }

    public static void setColorNonPCN(int colorNonPCN) {
        Settings.colorNonPCN = colorNonPCN;
    }
}
