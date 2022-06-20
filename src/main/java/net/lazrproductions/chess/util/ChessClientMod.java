package net.lazrproductions.chess.util;

import net.lazrproductions.chess.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.api.ClientModInitializer;

public class ChessClientMod implements ClientModInitializer
{
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), new Block[] { ModBlocks.CHESS_PIECE });
    }
}