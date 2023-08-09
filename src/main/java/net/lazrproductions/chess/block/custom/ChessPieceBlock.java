package net.lazrproductions.chess.block.custom;

import javax.annotation.Nullable;

import net.lazrproductions.chess.block.ModBlocks;
import net.lazrproductions.chess.util.ChessClientMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ChessPieceBlock extends Block {
    // 0 == Pawn
    // 1 == Knight
    // 2 == Bishop
    // 4 == Rook
    // 5 == King
    // 6 == Queen
    // 7 == Checkers
    // 8 == Checkers King
    public static final IntegerProperty PIECE = IntegerProperty.create("piece", 0, 7);
    public static final IntegerProperty COLOR = IntegerProperty.create("dye", 0, 16);
    public static final IntegerProperty FACING = IntegerProperty.create("facing", 0, 3);

    static final Item[] dyes = new Item[] { Items.POTION, Items.WHITE_DYE, Items.LIGHT_GRAY_DYE, Items.GRAY_DYE,
            Items.BLACK_DYE, Items.BROWN_DYE, Items.RED_DYE, Items.ORANGE_DYE, Items.YELLOW_DYE, Items.LIME_DYE,
            Items.GREEN_DYE, Items.CYAN_DYE, Items.LIGHT_BLUE_DYE, Items.BLUE_DYE, Items.PURPLE_DYE, Items.MAGENTA_DYE,
            Items.PINK_DYE };
    static final VoxelShape pawnShape = Block.box(4.0, 0.0, 4.0, 12.0, 13.0, 12.0);;
    static final VoxelShape rookShape = Block.box(3.0, 0.0, 3.0, 13.0, 17.0, 13.0);
    static final VoxelShape bishopShape = Block.box(3.0, 0.0, 3.0, 13.0, 23.0, 13.0);
    static final VoxelShape knightShape = Block.box(2.0, 0.0, 2.0, 14.0, 19.0, 14.0);
    static final VoxelShape kingShape = Block.box(2.0, 0.0, 2.0, 14.0, 29.0, 14.0);
    static final VoxelShape queenShape = Block.box(2.0, 0.0, 2.0, 14.0, 26.0, 14.0);
    static final VoxelShape checkersShape = Block.box(2.0, 0.0, 2.0, 14.0, 2.0, 14.0);

    public ChessPieceBlock(Properties settings) {
        super(settings);
    }

    //////////////// Collision and outline

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos,
            CollisionContext context) {
        if (state.getValue(PIECE) == 0) {
            return pawnShape;
        }
        if (state.getValue(PIECE) == 1) {
            return knightShape;
        }
        if (state.getValue(PIECE) == 2) {
            return bishopShape;
        }
        if (state.getValue(PIECE) == 3) {
            return rookShape;
        }
        if (state.getValue(PIECE) == 4) {
            return kingShape;
        }
        if (state.getValue(PIECE) == 5) {
            return queenShape;
        }
        if (state.getValue(PIECE) >= 6) {
            return checkersShape;
        }
        return Block.box(0, 0, 0, 16, 16, 16);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (state.getValue(PIECE) == 0) {
            return pawnShape;
        }
        if (state.getValue(PIECE) == 1) {
            return knightShape;
        }
        if (state.getValue(PIECE) == 2) {
            return bishopShape;
        }
        if (state.getValue(PIECE) == 3) {
            return rookShape;
        }
        if (state.getValue(PIECE) == 4) {
            return kingShape;
        }
        if (state.getValue(PIECE) == 5) {
            return queenShape;
        }
        if (state.getValue(PIECE) >= 6) {
            return checkersShape;
        }
        return Block.box(0, 0, 0, 16, 16, 16);
    }
    ////////////////

    //////////////// Interactions
    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
            BlockHitResult hit) {
        if (!world.isClientSide && hand == InteractionHand.MAIN_HAND) {
            if (!(player.getItemInHand(hand).getItem() == null || player.getItemInHand(hand).getItem() == Items.AIR)) {
                if (IsDye(player.getItemInHand(hand).getItem()) <= -1
                        && player.getItemInHand(hand).getItem() == Items.STICK) {
                    // Is using a stick (the tool to change pieces)
                    int currentPiece = state.getValue(PIECE);
                    int nextPiece = currentPiece + 1;
                    if (nextPiece > 7) {
                        nextPiece = 0;
                    }
                    world.setBlock(pos, state.setValue(PIECE, nextPiece), 10);

                    world.playSound(null, pos, SoundEvents.WOOD_HIT, SoundSource.BLOCKS, 1.0f, 1.0f);
                    return InteractionResult.SUCCESS;
                }
                if (IsDye(player.getItemInHand(hand).getItem()) > -1
                        && IsDye(player.getItemInHand(hand).getItem()) != state.getValue(COLOR)) {
                    // is using dye
                    world.setBlock(pos, state.setValue(COLOR, IsDye(player.getItemInHand(hand).getItem())), 10);
                    if (IsDye(player.getItemInHand(hand).getItem()) > 0) {
                        // play the dye use sound
                        world.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
                    } else {
                        // play the bottle empty sound
                        world.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
                    }
                    if (!player.isCreative()) {
                        if (IsDye(player.getItemInHand(hand).getItem()) > 0) {
                            // decrement the dye
                            player.getItemInHand(hand).shrink(1);
                        } else {
                            // Spawn a bottle
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
        }
        if (world.isClientSide() && hand == InteractionHand.MAIN_HAND) {
            if (!ChessClientMod.instance.pieceIsSelected(pos)) {
                // Select Piece
                ChessClientMod.instance.selectPiece(pos, state.getValue(PIECE), state.getValue(COLOR), state.getValue(FACING));
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (IsDye(player.getInventory().getItem(player.getInventory().selected).getItem()) <= -1
                && player.getInventory().getItem(player.getInventory().selected).getItem() == Items.STICK) {
            int currentPiece = state.getValue(PIECE);
            int nextPiece = currentPiece - 1;
            if (nextPiece < 0) {
                nextPiece = 7;
            }
            level.setBlock(pos, state.setValue(PIECE, nextPiece), 10);
            level.playSound(null, pos, SoundEvents.WOOD_HIT, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
        super.playerWillDestroy(level, pos, state, player);
    }
    ////////////////

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(PIECE, COLOR, FACING);
    }

    @Override
    public boolean isOcclusionShapeFullBlock(BlockState p_222959_, BlockGetter p_222960_, BlockPos p_222961_) {
        return false;
    }

    //////////////// Saving and Loading NBT
    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        if (!level.isClientSide()) {
            ItemStack itemStack = new ItemStack(ModBlocks.CHESS_PIECE.get());
            CompoundTag nbt = new CompoundTag();
            itemStack.setTag(nbt);
            nbt.putInt("piece", (int) state.getValue(PIECE));
            nbt.putInt("dye", (int) state.getValue(COLOR));
            itemStack.setHoverName(Component.literal(getPieceName(state.getValue(PIECE), state.getValue(COLOR))));
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
        if (nbt != null && nbt.get("dye") != null && nbt.get("piece") != null) {
            if (entity != null) {
                level.setBlock(pos,
                        (state.setValue(COLOR, nbt.getInt("dye")).setValue(PIECE, nbt.getInt("piece")).setValue(FACING,
                                getFacingDirection(new BlockPos((int) entity.getLookAngle().x,
                                        (int) entity.getLookAngle().y, (int) entity.getLookAngle().z)))),
                        10);
            } else {
                level.setBlock(pos,
                        (state.setValue(COLOR, nbt.getInt("dye")).setValue(PIECE, nbt.getInt("piece")).setValue(FACING,
                                getFacingDirection(new BlockPos(0, 0, 0)))),
                        10);
            }
        } else {
            if(entity != null) {
                level.setBlock(pos,
                        (state.setValue(FACING,
                                getFacingDirection(new BlockPos((int) entity.getLookAngle().x,
                                        (int) entity.getLookAngle().y, (int) entity.getLookAngle().z)))),
                        10);
            } else {
                level.setBlock(pos,
                        (state.setValue(FACING,
                                getFacingDirection(new BlockPos(0,0,0)))),
                        10);
            }
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

    private String getPieceName(int piece, int color) {
        final String[] pieceNames = { "Pawn", "Knight", "Bishop", "Rook", "King", "Queen", "Checkers Piece",
                "Checkers King Piece" };
        final String[] colorNames = { "", "White", "Light Gray", "Gray", "Black", "Brown", "Red", "Orange", "Yellow",
                "Lime", "Green", "Cyan", "Light Blue", "Blue", "Purple", "Magenta", "Pink" };
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
        // ( 0 == north 1 == south 2 == west 3 == east )
        if (direction.equals(new Vec3i(0, 0, -1)))
            return 0;
        else if (direction.equals(new Vec3i(0, 0, 1)))
            return 1;
        else if (direction.equals(new Vec3i(-1, 0, 0)))
            return 2;
        else if (direction.equals(new Vec3i(1, 0, 0)))
            return 3;

        return 0;
    }
}