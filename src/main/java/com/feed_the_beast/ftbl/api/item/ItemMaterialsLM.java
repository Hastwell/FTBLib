package com.feed_the_beast.ftbl.api.item;

import com.latmod.lib.util.LMUtils;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ItemMaterialsLM extends ItemLM
{
    public final Map<Integer, MaterialItem> materials;

    public ItemMaterialsLM()
    {
        materials = new HashMap<>();
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    public String getFolder()
    {
        return null;
    }

    public final void add(MaterialItem m)
    {
        m.setItem(this);
        materials.put(m.damage, m);
    }

    public final void addAll(Class<?> c)
    {
        try
        {
            LMUtils.getObjects(MaterialItem.class, c, null).forEach(this::add);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Nonnull
    @Override
    public String getUnlocalizedName(ItemStack is)
    {
        MaterialItem m = materials.get(is.getMetadata());

        if(m != null)
        {
            String s = getFolder();
            return (s == null || s.isEmpty()) ? getMod().getItemName(m.getID()) : getMod().getItemName(s + '.' + m.getID());
        }

        return "unknown";
    }

    @Override
    public void onPostLoaded()
    {
    }

    @Override
    public void loadRecipes()
    {
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void loadModels()
    {
        for(MaterialItem i : materials.values())
        {
            ModelLoader.setCustomModelResourceLocation(this, i.damage, new ModelResourceLocation(getRegistryName(), "variant=" + i.getID()));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(@Nonnull Item item, CreativeTabs c, List<ItemStack> l)
    {
        for(MaterialItem m : materials.values())
        {
            l.add(m.getStack(1));
        }
    }
}
