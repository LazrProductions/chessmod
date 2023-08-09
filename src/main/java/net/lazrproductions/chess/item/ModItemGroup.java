package net.lazrproductions.chess.item;


import net.lazrproductions.chess.ChessMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;


public class ModItemGroup {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, ChessMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> CUFFED_TAB = CREATIVE_MODE_TABS.register("chessmod_tab",
            () -> CreativeModeTab.builder()
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .title(Component.translatable("itemGroup.chessmod"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.CHESS_PIECE_ITEM.get());
                        output.accept(ModItems.BOARD_BLOCK_ITEM.get());
                    }).build());

    public static void registerItemGroups(IEventBus bus) {
        CREATIVE_MODE_TABS.register(bus);
    }
}
