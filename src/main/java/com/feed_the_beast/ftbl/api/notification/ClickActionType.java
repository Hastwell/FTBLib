package com.feed_the_beast.ftbl.api.notification;

import com.feed_the_beast.ftbl.api.ForgeWorldSP;
import com.feed_the_beast.ftbl.api.MouseButton;
import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbl.api.client.gui.GuiLM;
import com.feed_the_beast.ftbl.api.client.gui.GuiScreenRegistry;
import com.feed_the_beast.ftbl.api.client.gui.guibuttons.ActionButton;
import com.feed_the_beast.ftbl.api.client.gui.guibuttons.ActionButtonRegistry;
import com.feed_the_beast.ftbl.gui.info.GuiInfo;
import com.google.gson.JsonElement;
import com.latmod.lib.FinalIDObject;
import com.latmod.lib.util.LMUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.io.File;
import java.net.URI;

public abstract class ClickActionType extends FinalIDObject
{
    public static final ClickActionType SIDEBAR_BUTTON = ClickActionRegistry.register(new ClickActionType("sidebar_button")
    {
        @Override
        @SideOnly(Side.CLIENT)
        public void onClicked(@Nonnull JsonElement data, @Nonnull MouseButton button)
        {
            ActionButton a = ActionButtonRegistry.get(new ResourceLocation(data.getAsString()));

            if(a != null && a.isVisibleFor(ForgeWorldSP.inst.clientPlayer))
            {
                a.onClicked(ForgeWorldSP.inst.clientPlayer);
            }
        }
    });

    public static final ClickActionType CMD = ClickActionRegistry.register(new ClickActionType("cmd")
    {
        @Override
        @SideOnly(Side.CLIENT)
        public void onClicked(@Nonnull JsonElement data, @Nonnull MouseButton button)
        {
            FTBLibClient.execClientCommand("/" + data.getAsString(), false);
        }
    });

    // Static //
    public static final ClickActionType SHOW_CMD = ClickActionRegistry.register(new ClickActionType("show_cmd")
    {
        @Override
        @SideOnly(Side.CLIENT)
        public void onClicked(@Nonnull JsonElement data, @Nonnull MouseButton button)
        {
            Minecraft.getMinecraft().displayGuiScreen(new GuiChat(data.getAsString()));
        }
    });

    public static final ClickActionType URL = ClickActionRegistry.register(new ClickActionType("url")
    {
        @Override
        @SideOnly(Side.CLIENT)
        public void onClicked(@Nonnull JsonElement data, @Nonnull MouseButton button)
        {
            try
            {
                LMUtils.openURI(new URI(data.getAsString()));
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
    });

    public static final ClickActionType FILE = ClickActionRegistry.register(new ClickActionType("file")
    {
        @Override
        @SideOnly(Side.CLIENT)
        public void onClicked(@Nonnull JsonElement data, @Nonnull MouseButton button)
        {
            try
            {
                LMUtils.openURI(new File(data.getAsString()).toURI());
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
    });

    public static final ClickActionType GUI = ClickActionRegistry.register(new ClickActionType("gui")
    {
        @Override
        @SideOnly(Side.CLIENT)
        public void onClicked(@Nonnull JsonElement data, @Nonnull MouseButton button)
        {
            GuiScreen gui = GuiScreenRegistry.openGui(new ResourceLocation(data.getAsString()));

            if(gui != null)
            {
                Minecraft.getMinecraft().displayGuiScreen(gui);
            }
        }
    });

    public static final ClickActionType CHANGE_INFO_PAGE = ClickActionRegistry.register(new ClickActionType("change_page")
    {
        @Override
        @SideOnly(Side.CLIENT)
        public void onClicked(@Nonnull JsonElement data, @Nonnull MouseButton button)
        {
            GuiInfo gui = GuiLM.getWrappedGui(Minecraft.getMinecraft().currentScreen, GuiInfo.class);

            if(gui != null)
            {
                //FIXME change current info page
            }
        }
    });

    public ClickActionType(String id)
    {
        super(id);
    }

    public static void init()
    {
    }

    @SideOnly(Side.CLIENT)
    public abstract void onClicked(@Nonnull JsonElement data, @Nonnull MouseButton button);
}