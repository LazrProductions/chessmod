package net.lazrproductions.chess;

import org.slf4j.LoggerFactory;
import net.lazrproductions.chess.block.ModBlocks;
import net.lazrproductions.chess.item.ModItems;
import org.slf4j.Logger;
import net.fabricmc.api.ModInitializer;

public class ChessMod implements ModInitializer
{
    public static final String MOD_ID = "chessmod";
    public static final Logger LOGGER = LoggerFactory.getLogger("modid");;
    
    @Override
    public void onInitialize() {
        ModItems.registerModItems();
        ModBlocks.registerModBlocks();
    }
}