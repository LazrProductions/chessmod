package net.lazrproductions.chess.block.custom;

import net.lazrproductions.chess.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BoardBlock extends Block {

    public BoardBlock(Settings settings) {
        super(settings);
    }


    public static final IntProperty BORDER = IntProperty.of("border", 0, 1);
    public static final IntProperty COLOR = IntProperty.of("dye", 0, 2);
    static final Item[] dyes = { Items.POTION, Items.BLACK_DYE, Items.WHITE_DYE };


    ///////////// Interaction
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {
        if (!world.isClient && hand == Hand.MAIN_HAND) {
            if (IsDye(player.getStackInHand(hand).getItem()) <= -1) {
                if (state.get(BORDER) == 0) {
                    if (player.getStackInHand(hand).getItem() == Items.GOLD_INGOT) {
                        world.setBlockState(pos, state.with(BORDER, 1));
                        if (!player.isCreative()) {
                            player.getStackInHand(hand).decrement(1);
                        }
                        world.playSound(null, pos, SoundEvents.BLOCK_LANTERN_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        return ActionResult.SUCCESS;
                    }
                } else if (player.getStackInHand(hand).getItem().getClass() == AxeItem.class) {
                    world.setBlockState(pos, state.with(BoardBlock.BORDER, 0));
                    ItemEntity i = new ItemEntity(world, (double) (pos.getX() + 0.5f + hit.getSide().getOffsetX()),
                            (double) (pos.getY() + 0.5f + hit.getSide().getOffsetY()),
                            (double) (pos.getZ() + 0.5f + hit.getSide().getOffsetZ()),
                            new ItemStack(Items.GOLD_NUGGET, (int) Math.round(Math.random() * 9.0)));
                    i.resetPickupDelay();
                    world.spawnEntity(i);
                    world.playSound(null, pos, SoundEvents.BLOCK_LANTERN_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    return ActionResult.SUCCESS;
                }
            } else if (IsDye(player.getStackInHand(hand).getItem()) != state.get(COLOR)) {
                world.setBlockState(pos, state.with(COLOR, IsDye(player.getStackInHand(hand).getItem())));
                
                if (IsDye(player.getStackInHand(hand).getItem()) > 0) {
                    world.playSound(null, pos, SoundEvents.ITEM_DYE_USE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                } else {
                    world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
                }
                if (!player.isCreative()) {
                    if (IsDye(player.getStackInHand(hand).getItem()) > 0) {
                        player.getInventory().getStack(player.getInventory().selectedSlot).decrement(1);
                    } else {
                        player.getInventory().getStack(player.getInventory().selectedSlot).decrement(1);
                        ItemEntity i = new ItemEntity(world, (double) (pos.getX() + 0.5f + hit.getSide().getOffsetX()),
                                (double) (pos.getY() + 0.5f + hit.getSide().getOffsetY()),
                                (double) (pos.getZ() + 0.5f + hit.getSide().getOffsetZ()),
                                new ItemStack(Items.GLASS_BOTTLE, 1));
                        i.resetPickupDelay();
                        world.spawnEntity(i);
                    }
                }

                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.FAIL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(BORDER, COLOR);
    }
    /////////////


    //////////////// Saving and Loading NBT
    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient() && !player.isCreative()) {
            ItemStack itemStack = new ItemStack(ModBlocks.BOARD_BLOCK.asItem());
            NbtCompound nbt = new NbtCompound();
            itemStack.setNbt(nbt);
            itemStack.setSubNbt("border", NbtInt.of((int)state.get(BORDER)));
            itemStack.setSubNbt("dye", NbtInt.of((int)state.get(COLOR)));
            itemStack.setCustomName(Text.of(getPieceName(state.get(BORDER), state.get(COLOR))));
            ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, itemStack);
            itemEntity.resetPickupDelay();
            world.spawnEntity(itemEntity);
        }
        super.onBreak(world, pos, state, player);
    }
    
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        NbtCompound nbt = itemStack.getNbt();
        if (nbt != null && nbt.get("dye") != null && nbt.get("border") != null) {
            world.setBlockState(pos, (state.with(COLOR, Integer.parseInt(nbt.get("dye").asString()))).with(BORDER, Integer.parseInt(nbt.get("border").asString())));
        }
        super.onPlaced(world, pos, state, placer, itemStack);
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
