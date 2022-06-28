package net.lazrproductions.chess;

import net.lazrproductions.chess.block.ModBlocks;
import net.lazrproductions.chess.config.ModConfigs;
import net.lazrproductions.chess.item.ModItems;



import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import net.fabricmc.api.ModInitializer;

public class ChessMod implements ModInitializer
{
    public static final String MOD_ID = "chessmod";
    public static final Logger LOGGER = LogManager.getLogger("modid");;
    
    @Override
    public void onInitialize() {
        //Register the config files and pull the values
        ModConfigs.registerConfigs();

        //Register Blocks and Items
        ModItems.registerModItems();
        ModBlocks.registerModBlocks();
    }
}