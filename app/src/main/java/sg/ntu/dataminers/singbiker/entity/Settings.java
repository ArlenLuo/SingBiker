package sg.ntu.dataminers.singbiker.entity;

public class Settings {

    private static boolean unitSystemKM = true;
    private static String colorPCN = "#0000FF";
    private static String colorNonPCN = "#00FFFF";

    public static boolean isUnitSystemKM() {
        return unitSystemKM;
    }

    public static void setUnitSystem(boolean preferredUnitSystem) {
        Settings.unitSystemKM = preferredUnitSystem;
    }

    public static String getColorPCN() {
        return colorPCN;
    }

    public static void setColorPCN(String colorPCN) {
        Settings.colorPCN = colorPCN;
    }

    public static String getColorNonPCN() {
        return colorNonPCN;
    }

    public static void setColorNonPCN(String colorNonPCN) {
        Settings.colorNonPCN = colorNonPCN;
    }
}
