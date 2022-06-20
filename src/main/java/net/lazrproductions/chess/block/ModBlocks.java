package net.lazrproductions.chess.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.lazrproductions.chess.ChessMod;
import net.lazrproductions.chess.block.custom.BoardBlock;
import net.lazrproductions.chess.block.custom.ChessPieceBlock;
import net.lazrproductions.chess.item.ModItemGroup;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModBlocks {
    public static final Block CHESS_PIECE = registerBlock("chess_piece",
            new ChessPieceBlock(FabricBlockSettings.of(Material.WOOD).strength(0.2f).nonOpaque()), ModItemGroup.CHESS_PIECE);

    public static final Block BOARD_BLOCK = registerBlock("board_block",
            new BoardBlock(FabricBlockSettings.of(Material.WOOD).strength(0.5f).requiresTool()), ModItemGroup.CHESS_PIECE);




    private static Block registerBlock(String name, Block block, ItemGroup group) {
        registerBlockItem(name, block, group);
        return Registry.register(Registry.BLOCK, new Identifier(ChessMod.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block, ItemGroup group) {
        return Registry.register(Registry.ITEM, new Identifier(ChessMod.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings().group(group)));
    }

    public static void registerModBlocks() {
        ChessMod.LOGGER.info("Registering ModBlocks for " + ChessMod.MOD_ID);
    }
}