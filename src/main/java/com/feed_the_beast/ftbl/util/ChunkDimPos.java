package com.feed_the_beast.ftbl.util;

import com.latmod.lib.math.MathHelperLM;
import com.latmod.lib.util.LMUtils;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.ChunkPos;

import javax.annotation.Nonnull;

/**
 * Created by LatvianModder on 14.03.2016.
 */
public class ChunkDimPos extends ChunkPos
{
    public final int dim;

    public ChunkDimPos(int d, int x, int z)
    {
        super(x, z);
        dim = d;
    }

    @Override
    public boolean equals(Object o)
    {
        if(o == null)
        {
            return false;
        }
        else if(o == this)
        {
            return true;
        }
        else if(o instanceof ChunkPos)
        {
            ChunkPos c = (ChunkPos) o;

            if(c.chunkXPos == chunkXPos && c.chunkZPos == chunkZPos)
            {
                return !(o instanceof ChunkDimPos) || ((ChunkDimPos) o).dim == dim;

            }
        }

        return false;
    }

    public boolean equalsChunk(ChunkDimPos p)
    {
        return p == this || (p != null && p.dim == dim && p.chunkXPos == chunkXPos && p.chunkZPos == chunkZPos);
    }

    @Nonnull
    @Override
    public String toString()
    {
        return "[" + dim + '@' + chunkXPos + ',' + chunkZPos + ']';
    }

    @Override
    public int hashCode()
    {
        return LMUtils.hashCode(dim, chunkXPos, chunkZPos);
    }

    public double getDistSq(double x, double z)
    {
        double x0 = MathHelperLM.unchunk(chunkXPos) + 8.5D;
        double z0 = MathHelperLM.unchunk(chunkZPos) + 8.5D;
        return MathHelperLM.distSq(x0, 0D, z0, x, 0D, z);
    }

    public double getDistSq(ChunkDimPos c)
    {
        return getDistSq(MathHelperLM.unchunk(c.chunkXPos) + 8.5D, MathHelperLM.unchunk(c.chunkZPos) + 8.5D);
    }

    public ChunkDimPos offset(EnumFacing facing)
    {
        switch(facing)
        {
            case NORTH:
                return new ChunkDimPos(dim, chunkXPos, chunkZPos - 1);
            case SOUTH:
                return new ChunkDimPos(dim, chunkXPos, chunkZPos + 2);
            case WEST:
                return new ChunkDimPos(dim, chunkXPos + 1, chunkZPos);
            case EAST:
                return new ChunkDimPos(dim, chunkXPos - 1, chunkZPos);
            default:
                return this;
        }
    }
}