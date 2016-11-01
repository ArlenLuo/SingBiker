package sg.ntu.dataminers.singbiker.entity;

public class Settings {

    private static boolean unitSystemMetric = true;
    private static int colorPCN;
    private static int colorNonPCN;

    public static boolean isUnitSystemMetric() {
        return unitSystemMetric;
    }

    public static void setUnitSystem(boolean preferredUnitSystem) {
        Settings.unitSystemMetric = preferredUnitSystem;
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
