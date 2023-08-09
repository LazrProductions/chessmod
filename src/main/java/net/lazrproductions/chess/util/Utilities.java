package net.lazrproductions.chess.util;

import java.util.Iterator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;




public class Utilities {
    /**
    * Linearly interpolates between two points.
    *
    * @param a [Vec3] Position 1
    * @param b [Vec3] Position 2
    * @param t [Double] Value used to interpolate between a and b.
    * @return [Vec3] The position inbetween the position 'a' and position 'b' at 't', equal to a + (b - a) * t.
    */
    public static float Lerp(float a, float b, float t) {
        //x = (0, 1); // anything between 0 and 1
        //c = x * a + (1 - x) * b;
        float x = t * a + (1 - t) * b;

        return x;
    }

    /**
    * Linearly interpolates between two points.
    *
    * @param a [BlockPos] Position 1
    * @param b [BlockPos] Position 2
    * @param t [Double] Value used to interpolate between a and b.
    * @return [BlockPos] The position inbetween the position 'a' and position 'b' at 't', equal to a + (b - a) * t.
    */
    public static BlockPos Lerp(BlockPos a, BlockPos b, Float t) {
        double x = t * a.getX() + (1 - t) * b.getX();
        double y = t * a.getY() + (1 - t) * b.getY();
        double z = t * a.getZ() + (1 - t) * b.getZ();

        return new BlockPos((int)x,(int)y,(int)z);
    }


    public static Iterable<ItemStack> getIterableFromIterator(Iterator<ItemStack> iterator)
    {
        return new Iterable<ItemStack>() {
            @Override
            public Iterator<ItemStack> iterator()
            {
                return iterator;
            }
        };
    }

}
