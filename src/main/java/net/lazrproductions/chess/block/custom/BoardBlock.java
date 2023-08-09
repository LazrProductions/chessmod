package net.lazrproductions.chess.block.custom;

import javax.annotation.Nullable;

import net.lazrproductions.chess.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class BoardBlock extends Block {

      public BoardBlock(Properties p) {
        super(p);
    }

    public static final IntegerProperty BORDER = IntegerProperty.create("border", 0, 1);
    public static final IntegerProperty COLOR = IntegerProperty.create("dye", 0, 2);
    static final Item[] dyes = { Items.POTION, Items.BLACK_DYE, Items.WHITE_DYE };

    ///////////// Interaction
    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
            BlockHitResult hit) {
        if (!world.isClientSide && hand == InteractionHand.MAIN_HAND) {
            if (IsDye(player.getItemInHand(hand).getItem()) <= -1) {
                if (state.getValue(BORDER) == 0) {
                    if (player.getItemInHand(hand).getItem() == Items.GOLD_INGOT) {
                        world.setBlock(pos, state.setValue(BORDER, 1), 10);
                        if (!player.isCreative()) {
                            player.getItemInHand(hand).shrink(1);
                        }
                        world.playSound(null, pos, SoundEvents.LANTERN_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
                        return InteractionResult.SUCCESS;
                    }
                } else if (player.getItemInHand(hand).getItem().getClass() == AxeItem.class) {
                    world.setBlock(pos, state.setValue(BoardBlock.BORDER, 0), 10);
                    ItemEntity i = new ItemEntity(world, (double) (pos.getX() + 0.5f + hit.getDirection().getStepX()),
                            (double) (pos.getY() + 0.5f + hit.getDirection().getStepX()),
                            (double) (pos.getZ() + 0.5f + hit.getDirection().getStepZ()),
                            new ItemStack(Items.GOLD_NUGGET, (int) Math.round(Math.random() * 9.0)));
                    i.setDefaultPickUpDelay();
                    world.addFreshEntity(i);
                    world.playSound(null, pos, SoundEvents.LANTERN_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f);
                    return InteractionResult.SUCCESS;
                }
            } else if (IsDye(player.getItemInHand(hand).getItem()) != state.getValue(COLOR)) {
                world.setBlock(pos, state.setValue(COLOR, IsDye(player.getItemInHand(hand).getItem())), 10);

                if (IsDye(player.getItemInHand(hand).getItem()) > 0) {
                    world.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
                } else {
                    world.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
                }
                if (!player.isCreative()) {
                    if (IsDye(player.getItemInHand(hand).getItem()) > 0) {
                        player.getItemInHand(hand).shrink(1);
                    } else {
                        player.getItemInHand(hand).shrink(1);
                        ItemEntity i = new ItemEntity(world,
                                (double) (pos.getX() + 0.5f + hit.getDirection().getStepX()),
                                (double) (pos.getY() + 0.5f + hit.getDirection().getStepY()),
                                (double) (pos.getZ() + 0.5f + hit.getDirection().getStepZ()),
                                new ItemStack(Items.GLASS_BOTTLE, 1));
                        i.setDefaultPickUpDelay();
                        world.addFreshEntity(i);
                    }
                }

                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(BORDER, COLOR);
    }
    /////////////

    //////////////// Saving and Loading NBT
    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        if (!level.isClientSide()) {
            ItemStack itemStack = new ItemStack(ModBlocks.BOARD_BLOCK.get());
            CompoundTag nbt = new CompoundTag();
            nbt.putInt("border", (int) state.getValue(BORDER));
            nbt.putInt("dye", (int) state.getValue(COLOR));
            itemStack.setTag(nbt);
            itemStack.setHoverName(Component.literal(getPieceName(state.getValue(BORDER), state.getValue(COLOR))));
            ItemEntity itemEntity = new ItemEntity((Level) level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    itemStack);
            itemEntity.setDefaultPickUpDelay();
            level.addFreshEntity(itemEntity);
        }
        super.destroy(level, pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity,
            ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        if (nbt != null && nbt.get("dye") != null && nbt.get("border") != null) {
            level.setBlock(pos,
                    (state.setValue(COLOR, nbt.getInt("dye"))
                            .setValue(BORDER, nbt.getInt("border"))),
                    10);
        }
        super.setPlacedBy(level, pos, state, entity, stack);
    }
    ////////////////

    private int IsDye(Item item) {
        for (int i = 0; i < dyes.length; ++i) {
            if (item == dyes[i]) {
                return i;
            }
        }
        return -1;
    }

    private String getPieceName(int border, int color) {
        final String[] borderNames = { "Game board", "Gilded Game Board" };
        final String[] colorNames = { "", "Black", "White" };
        if (colorNames[color] == "") {
            return borderNames[border];
        }
        return colorNames[color] + " " + borderNames[border];
    }

}
