package net.lazrproductions.chess.block.custom;


import net.lazrproductions.chess.block.ModBlocks;
import net.lazrproductions.chess.util.ChessClientMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ChessPieceBlock extends Block
{
    //0 == Pawn
    //1 == Knight
    //2 == Bishop
    //4 == Rook
    //5 == King
    //6 == Queen
    //7 == Checkers
    //8 == Checkers King
    public static final IntProperty PIECE = IntProperty.of("piece", 0, 7);
    public static final IntProperty COLOR = IntProperty.of("dye", 0, 16);
    public static final IntProperty FACING = IntProperty.of("facing", 0, 3);

    static final Item[] dyes = new Item[] { Items.POTION, Items.WHITE_DYE, Items.LIGHT_GRAY_DYE, Items.GRAY_DYE, Items.BLACK_DYE, Items.BROWN_DYE, Items.RED_DYE, Items.ORANGE_DYE, Items.YELLOW_DYE, Items.LIME_DYE, Items.GREEN_DYE, Items.CYAN_DYE, Items.LIGHT_BLUE_DYE, Items.BLUE_DYE, Items.PURPLE_DYE, Items.MAGENTA_DYE, Items.PINK_DYE };
    static final VoxelShape pawnShape = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 13.0, 12.0);;
    static final VoxelShape rookShape = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 17.0, 13.0);
    static final VoxelShape bishopShape = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 23.0, 13.0);
    static final VoxelShape knightShape = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 19.0, 14.0);
    static final VoxelShape kingShape = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 29.0, 14.0);
    static final VoxelShape queenShape = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 26.0, 14.0);
    static final VoxelShape checkersShape = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 2.0, 14.0);
    
    public ChessPieceBlock(Settings settings) {
        super(settings);
    }
    
    //////////////// Collision and outline
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
            if (state.get(PIECE) == 0) {
                return pawnShape;
            }
            if (state.get(PIECE) == 1) {
                return knightShape;
            }
            if (state.get(PIECE) == 2) {
                return bishopShape;
            }
            if (state.get(PIECE) == 3) {
                return rookShape;
            }
            if (state.get(PIECE) == 4) {
                return kingShape;
            }
            if (state.get(PIECE) == 5) {
                return queenShape;
            }
            if (state.get(PIECE) >= 6) {
                return checkersShape;
            }
            return Block.createCuboidShape(0, 0, 0, 16, 16, 16);
        }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
            if (state.get(PIECE) == 0) {
                return pawnShape;
            }
            if (state.get(PIECE) == 1) {
                return knightShape;
            }
            if (state.get(PIECE) == 2) {
                return bishopShape;
            }
            if (state.get(PIECE) == 3) {
                return rookShape;
            }
            if (state.get(PIECE) == 4) {
                return kingShape;
            }
            if (state.get(PIECE) == 5) {
                return queenShape;
            }
            if (state.get(PIECE) >= 6) {
                return checkersShape;
            }
            return Block.createCuboidShape(0, 0, 0, 16, 16, 16);
        }
    ////////////////

    //////////////// Interactions 
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient && hand == Hand.MAIN_HAND) {
            if (!(player.getStackInHand(hand).getItem() == null || player.getStackInHand(hand).getItem() == Items.AIR)) {
                if (IsDye(player.getStackInHand(hand).getItem()) <= -1 && player.getStackInHand(hand).getItem() == Items.STICK) {
                    // Is using a stick (the tool to change pieces)
                    int currentPiece = state.get(PIECE);
                    int nextPiece = currentPiece + 1;
                    if (nextPiece > 7) {
                        nextPiece = 0;
                    }
                    world.setBlockState(pos, state.with(PIECE, nextPiece), Block.NOTIFY_ALL);

                    world.playSound(null, pos, SoundEvents.BLOCK_WOOD_HIT, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    return ActionResult.SUCCESS;
                }
                if (IsDye(player.getStackInHand(hand).getItem()) > -1 && IsDye(player.getStackInHand(hand).getItem()) != state.get(COLOR)) {
                    // is using dye
                    world.setBlockState(pos, state.with(COLOR, IsDye(player.getStackInHand(hand).getItem())));
                    if (IsDye(player.getStackInHand(hand).getItem()) > 0) {
                        // play the dye use sound
                        world.playSound(null, pos, SoundEvents.ITEM_DYE_USE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    } else {
                        // play the bottle empty sound
                        world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    }
                    if (!player.isCreative()) {
                        if (IsDye(player.getStackInHand(hand).getItem()) > 0) {
                            // decrement the dye
                            player.getInventory().getStack(player.getInventory().selectedSlot).decrement(1);
                        } else {
                            // Spawn a bottle
                            player.getInventory().getStack(player.getInventory().selectedSlot).decrement(1);
                            ItemEntity i = new ItemEntity(world,
                                    (double) (pos.getX() + 0.5f + hit.getSide().getOffsetX()),
                                    (double) (pos.getY() + 0.5f + hit.getSide().getOffsetY()),
                                    (double) (pos.getZ() + 0.5f + hit.getSide().getOffsetZ()),
                                    new ItemStack(Items.GLASS_BOTTLE, 1));
                            world.spawnEntity(i);
                        }
                    }
                    return ActionResult.SUCCESS;
                }

            }
        }
        if(world.isClient() && hand == Hand.MAIN_HAND) {
            if(!ChessClientMod.instance.pieceIsSelected(pos)) {
                //Select Piece
                ChessClientMod.instance.selectPiece(pos, state.get(PIECE), state.get(COLOR), state.get(FACING));
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.FAIL;
    }

    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if (IsDye(player.getInventory().getStack(player.getInventory().selectedSlot).getItem()) <= -1 && player.getInventory().getStack(player.getInventory().selectedSlot).getItem() == Items.STICK) {
            int currentPiece = state.get(PIECE);
            int nextPiece = currentPiece - 1;
            if (nextPiece < 0) {
                nextPiece = 7;
            }
            world.setBlockState(pos, state.with(PIECE, nextPiece), Block.NOTIFY_ALL);
            world.playSound(null, pos, SoundEvents.BLOCK_WOOD_HIT, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
        //super.onBlockBreakStart(state, world, pos, player);
    }
    ////////////////

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(PIECE, COLOR, FACING);
    }
    
    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    //////////////// Saving and Loading NBT
    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient() && !player.isCreative()) {
            ItemStack itemStack = new ItemStack(ModBlocks.CHESS_PIECE.asItem());
            NbtCompound nbt = new NbtCompound();
            itemStack.setNbt(nbt);
            itemStack.setSubNbt("piece", NbtInt.of((int)state.get(PIECE)));
            itemStack.setSubNbt("dye", NbtInt.of((int)state.get(COLOR)));
            itemStack.setCustomName(Text.of(getPieceName(state.get(PIECE), state.get(COLOR))));
            ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, itemStack);
            itemEntity.resetPickupDelay();
            world.spawnEntity(itemEntity);
        }

        super.onBreak(world, pos, state, player);
    }
    
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {

        NbtCompound nbt = itemStack.getNbt();
        if (nbt != null && nbt.get("dye") != null && nbt.get("piece") != null) {
            world.setBlockState(pos, (state.with(COLOR, Integer.parseInt(nbt.get("dye").asString()))).with(PIECE, Integer.parseInt(nbt.get("piece").asString())).with(FACING,getFacingDirection(new BlockPos(placer.getHorizontalFacing().getOffsetX(), placer.getHorizontalFacing().getOffsetY(), placer.getHorizontalFacing().getOffsetZ()))));
        } else {
            world.setBlockState(pos, (state.with(FACING,getFacingDirection(new BlockPos(placer.getHorizontalFacing().getOffsetX(), placer.getHorizontalFacing().getOffsetY(), placer.getHorizontalFacing().getOffsetZ())))));
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
    
    private String getPieceName(int piece, int color) {
        final String[] pieceNames = { "Pawn", "Knight", "Bishop", "Rook", "King", "Queen", "Checkers Piece", "Checkers King Piece" };
        final String[] colorNames = { "", "White", "Light Gray", "Gray", "Black", "Brown", "Red", "Orange", "Yellow", "Lime", "Green", "Cyan", "Light Blue", "Blue", "Purple", "Magenta", "Pink" };
        if (colorNames[color] == "") {
            return pieceNames[piece];
        }
        return colorNames[color] + " " + pieceNames[piece];
    }

    /**
     * Translates a BlockPos direction into an integer.
     * 
     * @param direction [BlockPos] the direction to translate.
     * @return [int] an interger value in the range of 0 to 3
     */
    int getFacingDirection(BlockPos direction) {
        //( 0 == north   1 == south   2 == west   3 == east )
        if(direction.equals(new Vec3i(0,0,-1))) return 0;
        else if(direction.equals(new Vec3i(0,0,1))) return 1;
        else if(direction.equals(new Vec3i(-1,0,0))) return 2;
        else if(direction.equals(new Vec3i(1,0,0))) return 3;

        return 0;
    }
}