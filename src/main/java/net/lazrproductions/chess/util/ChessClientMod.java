package net.lazrproductions.chess.util;

import net.lazrproductions.chess.block.ModBlocks;
import net.lazrproductions.chess.block.custom.BoardBlock;
import net.lazrproductions.chess.block.custom.ChessPieceBlock;
import net.lazrproductions.chess.config.ModConfigs;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.render.RenderLayer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;

import java.util.ArrayList;

import net.fabricmc.api.ClientModInitializer;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.World;

public class ChessClientMod implements ClientModInitializer {
    public static ChessClientMod instance;

    private int offsetX = 0;
    private int offsetZ = 0;
    private boolean showGrid = true;
    private boolean isBlocks = true;

    private int maximumMoveDistance = 20;
    private double maxDistFromMove = 10d;

    private float[] blockColor = colorToRgb(0xf8d800);
    private float[] blockColor2 = colorToRgb(0xffffff);
    private float[] opponentColor = colorToRgb(0xc60000);
    private float blockColorSpeed = 0.02f;

    private boolean dump = true;
    private long lastDumpTime, thisDumpTime;

    public GamePiece selectedPiece = null;

    public Moveset[] pieceMoves = new Moveset[] {
            new Moveset(new String[] {
                    "     ", // Pawn
                    " !?! ", //
                    "  O  ", // Can only move forward (by one) and diagonally forward when there is an
                             // opponent.
                    "     ", //
                    "     " //
            }),
            new Moveset(new String[] {
                    " 1 1 ", // Knight
                    "1   1", //
                    "  O  ", // Can move in L shapes in all directions.
                    "1   1", //
                    " 1 1 " //
            }),
            new Moveset(new String[] {
                    "     ", // Bishop
                    " * * ", //
                    "  O  ", // Can move diagonally.
                    " * * ", //
                    "     " //
            }),
            new Moveset(new String[] {
                    "     ", // Rook
                    "  *  ", //
                    " *O* ", // Can move up, down, left, and right.
                    "  *  ", //
                    "     " //
            }),
            new Moveset(new String[] {
                    "     ", // King
                    " 111 ", //
                    " 1O1 ", // Can up, down, left, and right (by one block).
                    " 111 ", //
                    "     " //
            }),
            new Moveset(new String[] {
                    "     ", // Queen
                    " *** ", //
                    " *O* ", // Can move up, down, left, right, and like a knight (in an L shape).
                    " *** ", //
                    "     " //
            }),
            new Moveset(new String[] {
                    "     ", // Checkers Piece
                    " 1 1 ", //
                    "  O  ", // Can move diagonally forward (by one block)
                    "     ", //
                    "     " //
            }),
            new Moveset(new String[] {
                    "     ", // Checkers Piece King
                    " 1 1 ", //
                    "  O  ", // Can move diagonally (by one block)
                    " 1 1 ", //
                    "     " //
            })
    };

    /////////////// Classes
    public class Moveset {
        public String[] movements = new String[] {
                "     ", // Top down view
                "  1  ", // ^ North (z-1) > East (x-1)
                " 1O1 ", // (* represents infinity) (! represents a requirement for an opposing piece to
                         // be in that position) (? represents a space that cannot be moved into if ANY
                         // piece is in it)
                "  1  ", // (use integers to limit the distance in a direction) (O represents the piece's
                         // position before moving)
                "     " // V South (z+1) < West (x+1)
        };

        public Moveset(String[] movements) {
            this.movements = movements;
        }
    }

    public class GamePiece {
        public int piece;
        public BlockPos pos;
        public int color;
        public int facing;

        // constructor
        public GamePiece(BlockPos pos, int piece, int color, int facing) {
            this.piece = piece;
            this.pos = pos;
            this.color = color;
            this.facing = facing;
        }
    }

    public class GameBoard {
        public GamePiece piece;
        public BlockPos pos;

        // Constructor
        public GameBoard(BlockPos pos, GamePiece piece) {
            this.piece = piece;
            this.pos = pos;
        }
    }

    class MovesetDirection {
        public BlockPos direction; // The direction of movement
        public boolean infinite; // Is an infinite direction
        public boolean mustDefeat; // Must defeat an opponent piece to move here.
        public boolean cannotDefeat; // Cannot defeant an opponent piece when moving here.

        public MovesetDirection(BlockPos p, boolean inf, boolean mustDefeat, boolean cannotDefeat) {
            this.direction = p;
            this.infinite = inf;
            this.mustDefeat = mustDefeat;
            this.cannotDefeat = cannotDefeat;
        }
    }

    class Selection {
        public BlockPos pos; // The direction of movement
        public float[] color; // Is an infinite direction

        public Selection(BlockPos p, float[] color) {
            this.pos = p;
            this.color = color;
        }
    }
    ///////////////


    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), new Block[] { ModBlocks.CHESS_PIECE });

        instance = this;

        blockColor = colorToRgb(ModConfigs.SELECTIONCOLOR1);
        blockColor2 = colorToRgb(ModConfigs.SELECTIONCOLOR2);
        opponentColor = colorToRgb(ModConfigs.SELECTIONOPPONENTCOLOR);
        maximumMoveDistance = ModConfigs.SELECTIONMAXMOVEDIST;
        maxDistFromMove = ModConfigs.SELECTIONMAXDISTTOMOVE;
    }

    private float[] colorToRgb(int color) {
        float[] result = new float[3];
        result[0] = ((color >> 16) & 0xff) / 255f;
        result[1] = ((color >> 8) & 0xff) / 255f;
        result[2] = ((color >> 0) & 0xff) / 255f;
        return result;
    }


    /////////////// ChessPieces and Board Block Utilities
    public void clearSelectedPiece() {
        selectedPiece = null;
    }

    public boolean selectPiece(GamePiece piece) {
        if (selectedPiece != null)
            return false;

        selectedPiece = piece;
        return true;
    }

    public boolean selectPiece(BlockPos pos, int piece, int color, int facing) {
        if (selectedPiece != null && selectedPiece.pos == pos) {
            return false;
        }

        selectedPiece = new GamePiece(pos, piece, color, facing);
        return true;
    }

    public boolean deselectPiece(GamePiece piece) {
        if (selectedPiece == piece) {
            selectedPiece = null;
            return true;
        }

        return false;
    }

    public boolean deselectPiece(BlockPos pos) {
        if (selectedPiece.pos == pos) {
            selectedPiece = null;
            return true;
        }

        return false;
    }

    public boolean deselectPiece() {
        selectedPiece = null;
        return true;
    }

    public boolean pieceIsSelected(GamePiece piece) {
        return selectedPiece == piece;
    }

    public boolean pieceIsSelected(BlockPos pos) {
        if (selectedPiece != null && selectedPiece.pos == pos) {
            return true;
        }

        return false;
    }

    public boolean changeSelectedPiece(BlockPos pos, int piece) {

        if (selectedPiece != null && selectedPiece.pos == pos) {
            selectedPiece.piece = piece;
            return true;
        }

        return false;
    }

    public boolean changeSelectedPieceColor(BlockPos pos, int color) {

        if (selectedPiece != null && selectedPiece.pos == pos) {
            selectedPiece.color = color;
            return true;
        }

        return false;
    }
    ///////////////


    /////////////// Rendering
    float t = 0;

    public void renderOverlay(float partialTicks, MatrixStack stack, VertexConsumer consumer, double cameraX, double cameraY, double cameraZ) {

        if (!showGrid)
            return;
        
        stack.push();
        stack.translate(-cameraX, -cameraY, -cameraZ);

        float ta = t;
        if(t>1) ta = (t-2)/-1;
        float curBlockColor[] = { 
            Utilities.Lerp(blockColor[0], blockColor2[0], ta),
            Utilities.Lerp(blockColor[1], blockColor2[1], ta),
            Utilities.Lerp(blockColor[2], blockColor2[2], ta) 
        };

        t+=blockColorSpeed;
        if(t>2) t=0;

        thisDumpTime=System.currentTimeMillis();
        dump=false;
        if (thisDumpTime > lastDumpTime + 50000) {
            dump=false;         // set this to true to get line info from time to time
            lastDumpTime=thisDumpTime;
        }
        
        if (showGrid) {                
            stack.push();
            stack.translate(offsetX, 0, offsetZ);
            if (isBlocks) {
                if(selectedPiece != null) {
                    World world = MinecraftClient.getInstance().getCameraEntity().getWorld();
                    MinecraftClient inst = MinecraftClient.getInstance();
                    

                    ArrayList<MovesetDirection> set = movesetToDirections(world, pieceMoves[selectedPiece.piece].movements, selectedPiece.facing);
                    
                    ArrayList<Selection> selections = new ArrayList<Selection>(0);

                    for(int i = 0; i < set.size(); i++) {
                        if(set.get(i).infinite) {
                            for(int d = 1; d < maximumMoveDistance; d++) {
                                BlockPos p = new BlockPos((set.get(i).direction.getX() * d) + selectedPiece.pos.getX(), selectedPiece.pos.getY(), (set.get(i).direction.getZ() * d) + selectedPiece.pos.getZ());
                                Selection s = new Selection(p, curBlockColor);
                                if(world.getBlockState(p.add(0,-1,0)).getBlock().getClass() == BoardBlock.class) {

                                if(world.getBlockState(p).getBlock() != Blocks.AIR) {
                                    //There is a block in the way so do not continue past this
                                    if(world.getBlockState(p).getBlock().getClass() == ChessPieceBlock.class && world.getBlockState(p).get(ChessPieceBlock.COLOR) != selectedPiece.color) {
                                        // The piece is a different color, so display a selection
                                        s.color = opponentColor;
                                        selections.add(s);
                                    }
                                    break;
                                }
                                selections.add(s);
                            }
                            }
                        } else {
                            if(set.get(i).mustDefeat) {
                                //can move here no matter what.
                                BlockPos p = new BlockPos(set.get(i).direction.getX() + selectedPiece.pos.getX(), selectedPiece.pos.getY(), set.get(i).direction.getZ() + selectedPiece.pos.getZ());

                                if(world.getBlockState(p.add(0,-1,0)).getBlock().getClass() == BoardBlock.class) {

                                if(world.getBlockState(p).getBlock() != Blocks.AIR) {
                                    //There is a block in the way so do not continue past this
                                    if(world.getBlockState(p).getBlock().getClass() == ChessPieceBlock.class && world.getBlockState(p).get(ChessPieceBlock.COLOR) != selectedPiece.color) {
                                        // The piece is a different color, so display a selection
                                        selections.add(new Selection(p, opponentColor));
                                    }
                                }
                            }
                            } else if(set.get(i).cannotDefeat) {
                                //Can move here if there is no piece
                                BlockPos p = new BlockPos(set.get(i).direction.getX() + selectedPiece.pos.getX(), selectedPiece.pos.getY(), set.get(i).direction.getZ() + selectedPiece.pos.getZ());
                                
                                if(world.getBlockState(p.add(0,-1,0)).getBlock().getClass() == BoardBlock.class) {

                                if(world.getBlockState(p).getBlock() == Blocks.AIR) {
                                    selections.add(new Selection(p, curBlockColor));
                                }
                            }
                            } else {
                                //can move here no matter what.
                                BlockPos p = new BlockPos(set.get(i).direction.getX() + selectedPiece.pos.getX(), selectedPiece.pos.getY(), set.get(i).direction.getZ() + selectedPiece.pos.getZ());
                                Selection s = new Selection(p, curBlockColor);

                                if(world.getBlockState(p.add(0,-1,0)).getBlock().getClass() == BoardBlock.class) {
                                if(world.getBlockState(p).getBlock() != Blocks.AIR) {
                                    //There is a block in the way so do not continue past this
                                    if(world.getBlockState(p).getBlock().getClass() == ChessPieceBlock.class && world.getBlockState(p).get(ChessPieceBlock.COLOR) != selectedPiece.color) {
                                        // The piece is a different color, so display a selection
                                        s.color = opponentColor;
                                        selections.add(s);
                                    }
                                } else {
                                    selections.add(s);
                                }
                            }
                            }
                        }
                    }

                    for(int s = 0; s < selections.size(); s++) {
                        drawSquare(consumer, stack, selections.get(s).pos.getX(), selections.get(s).pos.getY(), selections.get(s).pos.getZ(), selections.get(s).color[0], selections.get(s).color[1], selections.get(s).color[2], 0.0625f, 0.9375f);
                        drawSquare(consumer, stack, selections.get(s).pos.getX(), selections.get(s).pos.getY(), selections.get(s).pos.getZ(), selections.get(s).color[0], selections.get(s).color[1], selections.get(s).color[2], 0f, 1f);
                    }


                    if(world.getBlockState(selectedPiece.pos).getBlock() == Blocks.AIR) {
                        selectedPiece = null;
                    }

                    if(inst.mouse.wasRightButtonClicked()) {
                       //inst.cameraEntity.
                    }

                }
            }
            stack.pop();
        }

        stack.pop();
    }
    ///////////////

    
    /////////////// Drawing
    private void drawSquare(VertexConsumer consumer, MatrixStack stack, float x, float y, float z, float r, float g,
            float b, float from, float to) {
        drawLine(consumer, stack, x + from, x + to, y, y, z + from, z + from, r, g, b);
        drawLine(consumer, stack, x + to, x + to, y, y, z + from, z + to, r, g, b);
        drawLine(consumer, stack, x + to, x + from, y, y, z + to, z + to, r, g, b);
        drawLine(consumer, stack, x + from, x + from, y, y, z + to, z + from, r, g, b);
    }

    private void drawLine(VertexConsumer consumer, MatrixStack stack, float x1, float x2, float y1, float y2, float z1,
            float z2, float red, float green, float blue) {
        if (dump) {
            System.out.println("line from " + x1 + "," + y1 + "," + z1 + " to " + x2 + "," + y2 + "," + z2);
        }
        Matrix4f model = new Matrix4f(stack.peek().getPositionMatrix());
        if (model != null) {
            consumer.vertex(model, x1, y1, z1).color(red, green, blue, 1.0f).light(0).normal(0, 1, 0).texture(0, 0)
                    .next();
            consumer.vertex(model, x2, y2, z2).color(red, green, blue, 1.0f).light(0).normal(0, 1, 0).texture(0, 0)
                    .next();
        }
    }
    ///////////////

    public ArrayList<MovesetDirection> movesetToDirections(World world, String[] move, int facing) {
        ArrayList<MovesetDirection> positions = new ArrayList<MovesetDirection>(0);

        for (int i = 0; i < 5; i++) {
            for (int p = 0; p < 5; p++) {
                if (facing == 0) {
                    //Facing North
                    if (move[i].charAt(p) == '1') {
                        positions.add(new MovesetDirection(new BlockPos(p - 2, 0, i - 2), false, false, false));
                    } else if (move[i].charAt(p) == '*') {
                        positions.add(new MovesetDirection(new BlockPos(p - 2, 0, i - 2), true, false, false));
                    } else if (move[i].charAt(p) == '!') {
                        positions.add(new MovesetDirection(new BlockPos(p - 2, 0, i - 2), false, true, false));
                    } else if (move[i].charAt(p) == '?') {
                        positions.add(new MovesetDirection(new BlockPos(p - 2, 0, i - 2), false, false, true));
                    }
                } else if(facing == 1) {
                    //Facing South
                    if (move[i].charAt(p) == '1') {
                        positions.add(new MovesetDirection(new BlockPos(-p + 2, 0, -i + 2), false, false, false));
                    } else if (move[i].charAt(p) == '*') {
                        positions.add(new MovesetDirection(new BlockPos(-p + 2, 0, -i + 2), true, false, false));
                    } else if (move[i].charAt(p) == '!') {
                        positions.add(new MovesetDirection(new BlockPos(-p + 2, 0, -i + 2), false, true, false));
                    } else if (move[i].charAt(p) == '?') {
                        positions.add(new MovesetDirection(new BlockPos(-p + 2, 0, -i + 2), false, false, true));
                    }
                } else if(facing == 2) {
                    //Facing West
                    if (move[i].charAt(p) == '1') {
                        positions.add(new MovesetDirection(new BlockPos(i - 2, 0, p - 2), false, false, false));
                    } else if (move[i].charAt(p) == '*') {
                        positions.add(new MovesetDirection(new BlockPos(i - 2, 0, p - 2), true, false, false));
                    } else if (move[i].charAt(p) == '!') {
                        positions.add(new MovesetDirection(new BlockPos(i - 2, 0, p - 2), false, true, false));
                    } else if (move[i].charAt(p) == '?') {
                        positions.add(new MovesetDirection(new BlockPos(i - 2, 0, p - 2), false, false, true));
                    }
                } else if(facing == 3) {
                    //Facing West
                    if (move[i].charAt(p) == '1') {
                        positions.add(new MovesetDirection(new BlockPos(-i + 2, 0, -p + 2), false, false, false));
                    } else if (move[i].charAt(p) == '*') {
                        positions.add(new MovesetDirection(new BlockPos(-i + 2, 0, -p + 2), true, false, false));
                    } else if (move[i].charAt(p) == '!') {
                        positions.add(new MovesetDirection(new BlockPos(-i + 2, 0, -p + 2), false, true, false));
                    } else if (move[i].charAt(p) == '?') {
                        positions.add(new MovesetDirection(new BlockPos(-i + 2, 0, -p + 2), false, false, true));
                    }
                }
            }
        }

        return positions;
    }
}