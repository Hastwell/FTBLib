package com.feed_the_beast.ftbl.api.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemBlockLM extends ItemBlock
{
    public final IBlockLM blockLM;

    public ItemBlockLM(IBlockLM b)
    {
        super((Block) b);
        blockLM = b;
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public int getMetadata(int m)
    {
        return m;
    }

    @Nonnull
    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return super.getUnlocalizedName(stack);
    }
}