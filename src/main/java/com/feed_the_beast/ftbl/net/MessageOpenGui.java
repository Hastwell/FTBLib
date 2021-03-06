package com.feed_the_beast.ftbl.net;

import com.feed_the_beast.ftbl.FTBLibMod;
import com.feed_the_beast.ftbl.api.client.gui.LMGuiHandler;
import com.feed_the_beast.ftbl.api.client.gui.LMGuiHandlerRegistry;
import com.feed_the_beast.ftbl.api.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.api.net.MessageToClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageOpenGui extends MessageToClient<MessageOpenGui>
{
    public String modID;
    public int guiID;
    public NBTTagCompound data;
    public int windowID;

    public MessageOpenGui()
    {
    }

    public MessageOpenGui(String mod, int id, NBTTagCompound tag, int wid)
    {
        modID = mod;
        guiID = id;
        data = tag;
        windowID = wid;
    }

    @Override
    public LMNetworkWrapper getWrapper()
    {
        return FTBLibNetHandler.NET;
    }

    @Override
    public void fromBytes(ByteBuf io)
    {
        modID = readString(io);
        guiID = io.readInt();
        data = readTag(io);
        windowID = io.readUnsignedByte();
    }

    @Override
    public void toBytes(ByteBuf io)
    {
        writeString(io, modID);
        io.writeInt(guiID);
        writeTag(io, data);
        io.writeByte(windowID);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onMessage(MessageOpenGui m, Minecraft mc)
    {
        LMGuiHandler h = LMGuiHandlerRegistry.get(m.modID);

        if(h != null && FTBLibMod.proxy.openClientGui(mc.thePlayer, m.modID, m.guiID, m.data))
        {
            mc.thePlayer.openContainer.windowId = m.windowID;
        }
    }
}