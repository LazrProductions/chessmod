package net.lazrproductions.chess.item;

import net.lazrproductions.chess.ChessMod;
import net.lazrproductions.chess.block.ModBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            ChessMod.MOD_ID);

    public static final RegistryObject<Item> CHESS_PIECE_ITEM = ITEMS.register("chess_piece",
            () -> new BlockItem(ModBlocks.CHESS_PIECE.get(), new Item.Properties()));
    public static final RegistryObject<Item> BOARD_BLOCK_ITEM = ITEMS.register("board_block",
            () -> new BlockItem(ModBlocks.BOARD_BLOCK.get(), new Item.Properties()));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}