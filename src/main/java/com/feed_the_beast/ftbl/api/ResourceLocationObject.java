package com.feed_the_beast.ftbl.api;

import com.feed_the_beast.ftbl.util.FTBLib;
import com.latmod.lib.IIDObject;
import com.latmod.lib.util.LMUtils;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Comparator;

/**
 * Created by LatvianModder on 14.05.2016.
 */
public class ResourceLocationObject implements IIDObject
{
    public static final Comparator<Object> COMPARATOR = (o1, o2) -> FTBLib.RESOURCE_LOCATION_COMPARATOR.compare(getResourceLocation(o1), getResourceLocation(o2));
    private final ResourceLocation ID;

    public ResourceLocationObject(ResourceLocation id)
    {
        if(id == null)
        {
            throw new NullPointerException("ID can't be null!");
        }

        ID = id;
    }

    public static ResourceLocation getResourceLocation(Object o)
    {
        if(o == null)
        {
            return null;
        }
        else if(o instanceof ResourceLocation)
        {
            return (ResourceLocation) o;
        }
        else if(o instanceof ResourceLocationObject)
        {
            return ((ResourceLocationObject) o).getResourceLocation();
        }

        return null;
    }

    public final ResourceLocation getResourceLocation()
    {
        return ID;
    }

    @Nonnull
    @Override
    public final String getID()
    {
        return ID.toString();
    }

    @Override
    public String toString()
    {
        return getID();
    }

    @Override
    public final boolean equals(Object o)
    {
        if(o == null)
        {
            return false;
        }
        else if(o == this)
        {
            return true;
        }
        else if(o instanceof ResourceLocationObject)
        {
            return ((ResourceLocationObject) o).getResourceLocation().equals(ID);
        }
        else if(o instanceof ResourceLocation)
        {
            return o.equals(ID);
        }
        else
        {
            return LMUtils.getID(o).equals(getID());
        }
    }

    @Override
    public final int hashCode()
    {
        return ID.hashCode();
    }
}