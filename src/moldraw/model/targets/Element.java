/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.model.targets;

import javafx.scene.paint.Color;

/**
 *
 * @author prem
 */
public enum Element {

    HYDROGEN(1), HELIUM(2), LITHIUM(1), BERYLLIUM(2), BORON(3), CARBON(4), NITROGEN(5),
    OXYGEN(6), FLUORINE(7), NEON(8), SODIUM(1), MAGNESIUM(2), ALUMINUM(3), SILICON(4),
    PHOSPHORUS(5), SULFUR(6), CHLORINE(7), ARGON(8), POTASSIUM(1), CALCIUM(2), SCANDIUM(2, true),
    TITANIUM(2, true), VANADIUM(2, true), CHROMIUM(1, true), MANGANESE(2, true),
    IRON(2, true), COBALT(2, true), NICKEL(0, true), COPPER(1, true), ZINC(2, true),
    GALLIUM(3), GERMANIUM(4), ARSENIC(5), SELENIUM(6), BROMINE(7), KRYPTON(8),
    RUBIDIUM(1), STRONTIUM(2), YTTRIUM(2, true), ZIRCONIUM(2, true), NIOBIUM(2, true),
    MOLYBDENUM(2, true), TECHNETIUM(2, true), RUTHENIUM(1, true), RHODIUM(2, true),
    PALLADIUM(2, true), SILVER(1, true), CADMIUM(2, true), INDIUM(3), TIN(4),
    ANTIMONY(5), TELLURIUM(6), IODINE(7), XENON(8), CESIUM(1), BARIUM(2);

    private final boolean transitionMetal;
    private final boolean fShell;

    private final int valenceShell;

    Element(int valenceShell) {
        this(valenceShell, false, false);
    }

    Element(int valenceShell, boolean transitionMetal) {
        this(valenceShell, transitionMetal, false);
    }

    Element(int valenceShell, boolean transitionMetal, boolean fShell) {
        this.transitionMetal = transitionMetal;
        this.fShell = fShell;
        this.valenceShell = valenceShell;
    }

    public boolean isTransitionMetal() {
        return transitionMetal;
    }

    public boolean isFShell() {
        return fShell;
    }

    public int getValenceShell() {
        return valenceShell;
    }

    public static String getAbbrev(Element e) {
        switch (e) {
            case HYDROGEN:
            case BORON:
            case CARBON:
            case NITROGEN:
            case OXYGEN:
            case FLUORINE:
            case PHOSPHORUS:
            case SULFUR:
            case VANADIUM:
            case YTTRIUM:
            case IODINE:
                return format(e.toString().substring(0, 1));
            case SODIUM:
                return "Na";
            case MAGNESIUM:
                return "Mg";
            case CHLORINE:
                return "Cl";
            case POTASSIUM:
                return "K";
            case CHROMIUM:
                return "Cr";
            case MANGANESE:
                return "Mn";
            case IRON:
                return "Fe";
            case COPPER:
                return "Cu";
            case ZINC:
                return "Zn";
            case ARSENIC:
                return "As";
            case RUBIDIUM:
                return "Rb";
            case STRONTIUM:
                return "Sr";
            case ZIRCONIUM:
                return "Zr";
            case NIOBIUM:
                return "Nb";
            case TECHNETIUM:
                return "Tc";
            case PALLADIUM:
                return "Pd";
            case SILVER:
                return "Ag";
            case CADMIUM:
                return "Cd";
            case TIN:
                return "Sn";
            case ANTIMONY:
                return "Sb";
            case CESIUM:
                return "Cs";
            default:
                return format(e.toString());
        }
    }

    private static String format(String s) {
        return Character.toUpperCase(s.charAt(0)) + (s.length() > 1 ? Character.toString(s.charAt(1)).toUpperCase() : "");
    }

    public static Color getColor(Element e) {
        switch (e) {
            case HYDROGEN:
                return Color.LIGHTGRAY;
            case HELIUM:
                return Color.PEACHPUFF;
            case BORON:
                return Color.BROWN;
            case CARBON:
                return Color.DARKGRAY.darker().darker().darker();
            case NITROGEN:
                return Color.CORNFLOWERBLUE;
            case OXYGEN:
                return Color.RED.darker();
            case FLUORINE:
                return Color.YELLOWGREEN;
            case NEON:
                return Color.ORANGERED;
            case SILICON:
                return Color.PAPAYAWHIP;
            case PHOSPHORUS:
                return Color.VIOLET;
            case SULFUR:
                return Color.YELLOW;
            case CHLORINE:
                return Color.GREEN;
            case ARGON:
                return Color.PURPLE;
            case TITANIUM:
                return Color.WHITE;
            case COBALT:
                return Color.STEELBLUE;
            case COPPER:
                return Color.ORANGERED.darker().darker();
            case ARSENIC:
                return Color.VIOLET.darker();
            case SELENIUM:
                return Color.YELLOW.darker();
            case BROMINE:
                return Color.CHOCOLATE;
            case KRYPTON:
                return Color.WHITESMOKE;
            case INDIUM:
                return Color.INDIGO;
            case IODINE:
                return Color.PURPLE;
            case XENON:
                return Color.LIGHTBLUE;
            default:
                return Color.SILVER;
        }
    }

    public static double getElementRadiusPm(Element e) {
        switch (e) {
            case HYDROGEN:
                return 38;
            case HELIUM:
                return 32;
            case LITHIUM:
                return 134;
            case BERYLLIUM:
                return 90;
            case BORON:
                return 82;
            case CARBON:
                return 77;
            case NITROGEN:
                return 75;
            case OXYGEN:
                return 73;
            case FLUORINE:
                return 75;
            case NEON:
                return 69;
            case SODIUM:
                return 154;
            case MAGNESIUM:
                return 130;
            case ALUMINUM:
                return 118;
            case SILICON:
                return 111;
            case PHOSPHORUS:
                return 106;
            case SULFUR:
                return 102;
            case CHLORINE:
                return 99;
            case ARGON:
                return 97;
            case POTASSIUM:
                return 196;
            case CALCIUM:
                return 174;
            case SCANDIUM:
                return 144;
            case TITANIUM:
                return 136;
            case VANADIUM:
                return 125;
            case CHROMIUM:
                return 127;
            case MANGANESE:
                return 139;
            case IRON:
                return 125;
            case COBALT:
                return 126;
            case NICKEL:
                return 121;
            case COPPER:
                return 138;
            case ZINC:
                return 131;
            case GALLIUM:
                return 126;
            case GERMANIUM:
                return 122;
            case ARSENIC:
                return 119;
            case SELENIUM:
                return 116;
            case BROMINE:
                return 114;
            case KRYPTON:
                return 110;
            case RUBIDIUM:
                return 211;
            case STRONTIUM:
                return 192;
            case YTTRIUM:
                return 162;
            case ZIRCONIUM:
                return 148;
            case NIOBIUM:
                return 137;
            case MOLYBDENUM:
                return 145;
            case TECHNETIUM:
                return 156;
            case RUTHENIUM:
                return 126;
            case RHODIUM:
                return 135;
            case PALLADIUM:
                return 131;
            case SILVER:
                return 153;
            case CADMIUM:
                return 148;
            case INDIUM:
                return 144;
            case TIN:
                return 141;
            case ANTIMONY:
                return 138;
            case TELLURIUM:
                return 135;
            case IODINE:
                return 133;
            case XENON:
                return 130;
            case CESIUM:
                return 225;
            case BARIUM:
                return 198;
            default:
                return Double.NaN;
        }
    }
}
