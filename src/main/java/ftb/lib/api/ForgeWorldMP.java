package ftb.lib.api;

import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import ftb.lib.*;
import ftb.lib.mod.FTBLibEventHandler;
import latmod.lib.*;
import latmod.lib.util.Phase;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.relauncher.*;

import java.io.File;
import java.util.*;

/**
 * Created by LatvianModder on 09.02.2016.
 */
public final class ForgeWorldMP extends ForgeWorld
{
	public static ForgeWorldMP inst = null;
	public final File latmodFolder;
	
	public ForgeWorldMP(File f)
	{
		super(Side.SERVER);
		latmodFolder = f;
		currentMode = GameModes.instance().defaultMode;
	}
	
	public void init()
	{
		super.init();
		
		for(ForgeWorldData d : customData.values())
		{
			if(d instanceof IWorldTick)
			{
				FTBLibEventHandler.instance.ticking.add((IWorldTick) d);
			}
		}
	}
	
	public World getMCWorld()
	{ return MinecraftServer.getServer().getEntityWorld(); }
	
	public ForgeWorldMP toWorldMP()
	{ return this; }
	
	@SideOnly(Side.CLIENT)
	public ForgeWorldSP toWorldSP()
	{ return null; }
	
	public ForgePlayerMP getPlayer(Object o)
	{
		if(o instanceof FakePlayer) return new ForgePlayerFake((FakePlayer) o);
		ForgePlayer p = super.getPlayer(o);
		return (p == null) ? null : p.toPlayerMP();
	}
	
	public void load() throws Exception
	{
		JsonElement worldData = LMJsonUtils.fromJson(new File(latmodFolder.getParent(), "world_data.json"));
		
		if(worldData.isJsonObject())
		{
			JsonObject group = worldData.getAsJsonObject();
			worldID = group.has("world_id") ? LMUtils.fromString(group.get("world_id").getAsString()) : null;
			getID();
			
			currentMode = group.has("mode") ? GameModes.instance().get(group.get("mode").getAsString()) : GameModes.instance().defaultMode;
		}
		
		NBTTagCompound nbt = LMNBTUtils.readTag(new File(latmodFolder, "LMWorld.dat"));
		Set<Map.Entry<String, NBTBase>> customDataSet = null;
		
		playerMap.clear();
		
		if(nbt != null)
		{
			customDataSet = LMNBTUtils.entrySet(nbt.getCompoundTag("Custom"));
			
			if(!customData.isEmpty() && !customDataSet.isEmpty())
			{
				for(Map.Entry<String, NBTBase> entry : customDataSet)
				{
					ForgeWorldData d = customData.get(entry.getKey());
					
					if(d != null)
					{
						d.loadData((NBTTagCompound) entry.getValue(), Phase.PRE);
					}
				}
			}
		}
		
		for(Map.Entry<String, NBTBase> entry : LMNBTUtils.entrySet(LMNBTUtils.readTag(new File(latmodFolder, "LMPlayers.dat"))))
		{
			NBTTagCompound tag = (NBTTagCompound) entry.getValue();
			UUID id = LMUtils.fromString(entry.getKey());
			
			if(id == null)
			{
				id = LMUtils.fromString(tag.getString("UUID"));
			}
			
			if(id != null)
			{
				ForgePlayerMP p = new ForgePlayerMP(new GameProfile(id, tag.getString("Name")));
				p.readFromServer(tag);
				playerMap.put(id, p);
			}
		}
		
		if(nbt != null && customDataSet != null)
		{
			if(!customData.isEmpty() && !customDataSet.isEmpty())
			{
				for(Map.Entry<String, NBTBase> entry : customDataSet)
				{
					ForgeWorldData d = customData.get(entry.getKey());
					
					if(d != null)
					{
						d.loadData((NBTTagCompound) entry.getValue(), Phase.POST);
					}
				}
			}
		}
	}
	
	public void save() throws Exception
	{
		JsonObject group = new JsonObject();
		group.add("world_id", new JsonPrimitive(LMUtils.fromUUID(getID())));
		group.add("mode", new JsonPrimitive(currentMode.getID()));
		LMJsonUtils.toJson(new File(latmodFolder.getParent(), "world_data.json"), group);
		
		FTBLib.dev_logger.info("ForgeWorldMP Saved: " + group);
		
		/*
		
		if(!customData.isEmpty())
		{
			JsonObject customGroup = new JsonObject();
			JsonObject group1;
			
			for(ForgeWorldData d : customData.values())
			{
				group1 = new JsonObject();
				d.saveData(group1);
				
				if(!group1.entrySet().isEmpty())
				{
					customGroup.add(d.getID(), group1);
				}
			}
			
			if(!customGroup.entrySet().isEmpty())
			{
				group.add("custom", customGroup);
			}
		}
		
		JsonObject group = new JsonObject();
		ForgeWorldMP.inst.save(group);
		LMJsonUtils.toJson(new File(ForgeWorldMP.inst.latmodFolder, "world.json"), group);
		
		NBTTagCompound tag = new NBTTagCompound();
		
		for(ForgePlayer p : LMMapUtils.values(ForgeWorldMP.inst.playerMap, null))
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			p.toPlayerMP().writeToServer(tag1);
			tag1.setString("Name", p.getProfile().getName());
			tag.setTag(p.getStringUUID(), tag1);
		}
		
		LMNBTUtils.writeTag(new File(ForgeWorldMP.inst.latmodFolder, "LMPlayers.dat"), tag);
		
		// Export player list //
		
		try
		{
			ArrayList<String> l = new ArrayList<>();
			ArrayList<ForgePlayer> players1 = new ArrayList<>();
			players1.addAll(ForgeWorldMP.inst.playerMap.values());
			Collections.sort(players1);
			
			for(ForgePlayer p : players1)
			{
				l.add(p.getStringUUID() + " :: " + p.getProfile().getName());
			}
			
			LMFileUtils.save(new File(ForgeWorldMP.inst.latmodFolder, "LMPlayers.txt"), l);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		*/
	}
	
	public void writeDataToNet(NBTTagCompound tag, ForgePlayerMP self, boolean login)
	{
		tag.setLong("IDM", getID().getMostSignificantBits());
		tag.setLong("IDL", getID().getLeastSignificantBits());
		tag.setString("M", currentMode.getID());
		
		NBTTagCompound tag1, tag2;
		
		if(login)
		{
			tag1 = new NBTTagCompound();
			
			List<ForgePlayerMP> onlinePlayers = new ArrayList<>();
			for(ForgePlayer p : playerMap.values())
			{
				tag1.setString(p.getStringUUID(), p.getProfile().getName());
				if(p.isOnline() && !p.equalsPlayer(self)) onlinePlayers.add(p.toPlayerMP());
			}
			
			if(!tag1.hasNoTags())
			{
				tag.setTag("PM", tag1);
			}
			
			tag1 = new NBTTagCompound();
			
			for(ForgePlayerMP p : onlinePlayers)
			{
				tag2 = new NBTTagCompound();
				p.writeToNet(tag2, false);
				tag1.setTag(p.getStringUUID(), tag2);
			}
			
			tag2 = new NBTTagCompound();
			self.writeToNet(tag2, true);
			tag1.setTag(self.getStringUUID(), tag2);
			
			if(!tag1.hasNoTags())
			{
				tag.setTag("PMD", tag1);
			}
		}
		
		if(!customData.isEmpty())
		{
			tag1 = new NBTTagCompound();
			
			for(ForgeWorldData d : customData.values())
			{
				if(d.syncID())
				{
					tag2 = new NBTTagCompound();
					d.writeToNet(tag2, self, login);
					tag1.setTag(d.getID(), tag2);
				}
			}
			
			if(!tag1.hasNoTags())
			{
				tag.setTag("SFWD", tag1);
			}
		}
	}
	
	public List<ForgePlayerMP> getServerPlayers()
	{
		List<ForgePlayerMP> list = new ArrayList<>();
		for(ForgePlayer p : playerMap.values())
			list.add(p.toPlayerMP());
		return list;
	}
}