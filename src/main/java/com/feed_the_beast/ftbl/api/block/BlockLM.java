package com.feed_the_beast.ftbl.api.block;

import com.feed_the_beast.ftbl.api.tile.TileLM;
import com.feed_the_beast.ftbl.util.BlockStateSerializer;
import com.feed_the_beast.ftbl.util.LMMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class BlockLM extends Block implements IBlockLM
{
    public BlockLM(Material m)
    {
        super(m);
        setCreativeTab(CreativeTabs.MISC);
        setHardness(1.8F);
        setResistance(3F);
    }

    public abstract LMMod getMod();

    @Override
    public ItemBlock createItemBlock()
    {
        return new ItemBlockLM(this);
    }

    @Nonnull
    @Override
    @Deprecated
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    @Nonnull
    @Override
    public final String getID()
    {
        return getRegistryName().toString();
    }

    @Nonnull
    @Override
    public String getUnlocalizedName()
    {
        return getMod().getBlockName(getRegistryName().getResourcePath());
    }

    @Override
    public void onPostLoaded()
    {
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void loadModels()
    {
        ModelLoader.setCustomModelResourceLocation(getItem(), 0, new ModelResourceLocation(getRegistryName(), BlockStateSerializer.getString(getDefaultState())));
    }

    @Override
    public void loadTiles()
    {
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return getMetaFromState(state);
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return super.hasTileEntity(state);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(@Nonnull Item item, CreativeTabs c, List<ItemStack> l)
    {
        l.add(new ItemStack(item, 1, 0));
    }

    @Override
    public void onBlockPlacedBy(World w, BlockPos pos, IBlockState state, EntityLivingBase el, ItemStack is)
    {
        super.onBlockPlacedBy(w, pos, state, el, is);

        if(hasTileEntity(state) && el instanceof EntityPlayer)
        {
            TileLM tile = getTile(w, pos);
            if(tile != null)
            {
                tile.onPlacedBy((EntityPlayer) el, is, state);
            }
        }
    }

    @Override
    public float getExplosionResistance(World w, BlockPos pos, @Nonnull Entity e, Explosion ex)
    {
        if(hasTileEntity(w.getBlockState(pos)))
        {
            TileLM tile = getTile(w, pos);
            if(tile != null && tile.isExplosionResistant())
            {
                return Float.MAX_VALUE;
            }
        }

        return super.getExplosionResistance(w, pos, e, ex);
    }

    @Override
    public void breakBlock(@Nonnull World w, @Nonnull BlockPos pos, @Nonnull IBlockState state)
    {
        if(!w.isRemote && hasTileEntity(state))
        {
            TileLM tile = getTile(w, pos);
            if(tile != null)
            {
                tile.onBroken(state);
            }
        }
        super.breakBlock(w, pos, state);
    }

    @Override
    public boolean onBlockActivated(World w, BlockPos pos, IBlockState state, EntityPlayer ep, EnumHand hand, ItemStack item, EnumFacing s, float x1, float y1, float z1)
    {
        if(!hasTileEntity(state))
        {
            return false;
        }
        TileLM tile = getTile(w, pos);
        return (tile != null) && tile.onRightClick(ep, item, s, hand, x1, y1, z1);
    }

    @Override
    @Deprecated
    public boolean eventReceived(IBlockState state, World w, BlockPos pos, int eventID, int param)
    {
        if(hasTileEntity(state))
        {
            TileLM t = getTile(w, pos);
            if(t != null)
            {
                return t.receiveClientEvent(eventID, param);
            }
        }

        return false;
    }

    @Override
    public void loadRecipes()
    {
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack is, EntityPlayer ep, List<String> l, boolean b)
    {
    }

    @Override
    public void onNeighborChange(IBlockAccess w, BlockPos pos, BlockPos neighbor)
    {
        if(hasTileEntity(w.getBlockState(pos)))
        {
            TileLM t = getTile(w, pos);
            if(t != null)
            {
                t.onNeighborBlockChange(neighbor);
            }
        }
    }

    @Override
    public final Item getItem()
    {
        return Item.getItemFromBlock(this);
    }

    @Nonnull
    @Override
    @Deprecated
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return FULL_BLOCK_AABB;
    }

    @Nonnull
    @Override
    @Deprecated
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return getStateFromMeta(meta);
    }

    public TileLM getTile(IBlockAccess w, BlockPos pos)
    {
        TileEntity te = w.getTileEntity(pos);
        if(te != null && !te.isInvalid() && te instanceof TileLM)
        {
            return ((TileLM) te);
        }
        return null;
    }

    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.SOLID;
    }
}