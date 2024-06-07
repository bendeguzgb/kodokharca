package hu.bendeguz.kodokharca.model;

public enum Color {
    BLACK, WHITE, GREEN;
    
    
    public static Color fromChar(String c) {
        String value;
        switch (c.toLowerCase()) {
            case "h":
                value = "WHITE";
                break;
            case "k":
                value = "BLACK";
                break;
            case "z":
                value = "GREEN";
                break;
            default:
                throw new IllegalArgumentException(String.format("Could not parse color character: '%s'", c));
        }
        return Color.valueOf(value);
    }
}
