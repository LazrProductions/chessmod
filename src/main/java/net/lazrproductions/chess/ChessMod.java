package net.lazrproductions.chess;

import net.lazrproductions.chess.block.ModBlocks;
import net.lazrproductions.chess.item.ModItemGroup;
import net.lazrproductions.chess.item.ModItems;
import net.lazrproductions.chess.util.ChessClientMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


@Mod(ChessMod.MOD_ID)
public class ChessMod
{
    public static final String MOD_ID = "chessmod";
    public static final Logger LOGGER = LogManager.getLogger("chessmod");;
    
    public ChessMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);

        ModItemGroup.registerItemGroups(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("Running client setup for Chess Mod");
            MinecraftForge.EVENT_BUS.register(new ChessClientMod());
        }
    }
}