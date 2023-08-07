package net.lazrproductions.chess.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.lazrproductions.chess.ChessMod;
import net.lazrproductions.chess.block.custom.BoardBlock;
import net.lazrproductions.chess.block.custom.ChessPieceBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;


public class ModBlocks {
    public static final Block CHESS_PIECE = registerBlock("chess_piece",
            new ChessPieceBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).strength(0.2f).nonOpaque()));

    public static final Block BOARD_BLOCK = registerBlock("board_block",
            new BoardBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).strength(0.5f).requiresTool()));




    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(ChessMod.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(ChessMod.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }

    public static void registerModBlocks() {
        ChessMod.LOGGER.info("Registering ModBlocks for " + ChessMod.MOD_ID);
    }
}