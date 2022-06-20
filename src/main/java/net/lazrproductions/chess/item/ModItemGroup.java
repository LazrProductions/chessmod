package net.lazrproductions.chess.item;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.lazrproductions.chess.block.ModBlocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ModItemGroup {
    public static final ItemGroup CHESS_PIECE = FabricItemGroupBuilder.build(new Identifier("chessmod", "chess_piece"), () -> new ItemStack(ModBlocks.CHESS_PIECE.asItem()));

}
