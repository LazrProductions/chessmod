package net.lazrproductions.chess.block;

import net.lazrproductions.chess.ChessMod;
import net.lazrproductions.chess.block.custom.BoardBlock;
import net.lazrproductions.chess.block.custom.ChessPieceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
            ChessMod.MOD_ID);

    public static final RegistryObject<Block> CHESS_PIECE = BLOCKS.register("chess_piece",
            () -> new ChessPieceBlock(BlockBehaviour.Properties.of()
                            .strength(0.2f).noOcclusion().sound(SoundType.WOOD)));


    public static final RegistryObject<Block> BOARD_BLOCK = BLOCKS.register("board_block",
            () -> new BoardBlock(BlockBehaviour.Properties.of()
                            .strength(0.2f).noOcclusion().sound(SoundType.WOOD).mapColor(MapColor.WOOD)));


    public static void register(IEventBus bus) {
        ChessMod.LOGGER.info("Registering ModBlocks for " + ChessMod.MOD_ID);
        BLOCKS.register(bus);
    }
}