package vue.theme;

import java.awt.Color;
import java.util.List;

public final class Themes {
    public static final Theme CLASSIC = new Theme(
            "Classic",
            new Color(238, 238, 210),
            new Color(118, 150, 86),
            new Color(255, 211, 192),
            "classic",
            "%s-%s.png"
    );

    public static final Theme DARK = new Theme(
            "Dark",
            new Color(140, 140, 140),
            new Color(87, 87, 87),
            new Color(191, 166, 111),
            "crazy",
            "%.1s%.2S.png"
    );

    public static final Theme WOOD = new Theme(
            "Wooden",
            new Color(182, 141, 95),
            new Color(141, 100, 78),
            new Color(255, 171, 171),
            "wooden",
            "%s-%s.png"
    );

    private static final List<Theme> ALL = List.of(CLASSIC, DARK, WOOD);

    private Themes() {
    }

    public static List<Theme> all() {
        return ALL;
    }

    public static Theme defaultTheme() {
        return CLASSIC;
    }
}

