package net.lazrproductions.chess.config;

import com.mojang.datafixers.util.Pair;

import net.lazrproductions.chess.ChessMod;


public class ModConfigs {
    public static SimpleConfig CONFIG;
    private static ModConfigProvider configs;

    public static int SELECTIONCOLOR1;
    public static int SELECTIONCOLOR2;
    public static int SELECTIONOPPONENTCOLOR;
    public static int SELECTIONMAXMOVEDIST;
    public static double SELECTIONMAXDISTTOMOVE;

    
    public static void registerConfigs() {
        configs = new ModConfigProvider();
        createConfigs();

        CONFIG = SimpleConfig.of(ChessMod.MOD_ID + "config").provider(configs).request();

        assignConfigs();
    }

    private static void createConfigs() {
        configs.addKeyValuePair(new Pair<>("chessmod.selection.color.1", 0xf8d800), "hexidecimal or integer (ex: 16777215 or 0xffffff)");
        configs.addKeyValuePair(new Pair<>("chessmod.selection.color.2", 0xffffff), "hexidecimal or integer (ex: 16777215 or 0xffffff)");
        configs.addKeyValuePair(new Pair<>("chessmod.selection.opponent_color", 0xc60000), "hexidecimal or integer (ex: 16777215 or 0xffffff)");
        configs.addKeyValuePair(new Pair<>("chessmod.selection.maximum_move_distance", 20), "int");
        configs.addKeyValuePair(new Pair<>("chessmod.selection.maximum_distance_from_move", 10d), "double");
    }

    private static void assignConfigs() {
        SELECTIONCOLOR1 = CONFIG.getOrDefault("chessmod.selection.color.1", 0xf8d800);
        SELECTIONCOLOR2 = CONFIG.getOrDefault("chessmod.selection.color.2", 0xffffff);
        SELECTIONOPPONENTCOLOR = CONFIG.getOrDefault("chessmod.selection.opponent_color", 0xc60000);
        SELECTIONMAXMOVEDIST = CONFIG.getOrDefault("chessmod.selection.maximum_move_distance", 20);
        SELECTIONMAXDISTTOMOVE = CONFIG.getOrDefault("chessmod.selection.maximum_distance_from_move", 10d);

        System.out.println("All " + configs.getConfigsList().size() + " have been set properly");
    }
}