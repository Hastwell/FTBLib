package com.feed_the_beast.ftbl.api.client.gui.guibuttons;

import com.feed_the_beast.ftbl.api.ForgePlayerSP;
import com.feed_the_beast.ftbl.api.ForgeWorldSP;
import com.feed_the_beast.ftbl.util.TextureCoords;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public abstract class SidebarButton extends ActionButton
{
    public SidebarButton(ResourceLocation id, int p, TextureCoords c, Boolean b)
    {
        super(id, p, c, b);
    }

    @Override
    protected ITextComponent getDisplayName()
    {
        return new TextComponentTranslation("sidebar_button." + getID());
    }

    @Override
    public boolean isVisibleFor(ForgePlayerSP player)
    {
        return ForgeWorldSP.inst == null || ForgeWorldSP.inst.clientPlayer == null || player.equalsPlayer(ForgeWorldSP.inst.clientPlayer);
    }
}