package net.lazrproductions.chess.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.lazrproductions.chess.ChessMod;
import net.lazrproductions.chess.block.ModBlocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroup {
    public static final ItemGroup CHESS_PIECE = Registry.register(Registries.ITEM_GROUP, 
        new Identifier("chessmod", "chess_piece"), 
        FabricItemGroup.builder().displayName(Text.translatable("itemGroup.chessmod.chess_piece"))
        .icon(() -> new ItemStack(ModBlocks.CHESS_PIECE.asItem())).entries((displayContext, entries) -> {
                        entries.add(ModBlocks.BOARD_BLOCK);
                        entries.add(ModBlocks.CHESS_PIECE);
                    }).build());

    public static void registerItemGroups() {
        ChessMod.LOGGER.info("Registering Item Groups for " + ChessMod.MOD_ID);
    }
}
