package com.feed_the_beast.ftbl.api.config;

import com.feed_the_beast.ftbl.api.MouseButton;
import com.feed_the_beast.ftbl.api.client.gui.IClickable;
import com.feed_the_beast.ftbl.api.net.MessageLM;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public final class ConfigEntryEnum<E extends Enum<E>> extends ConfigEntry implements IClickable // EnumTypeAdapterFactory
{
    public final int defValue;
    public final EnumNameMap<E> nameMap;
    private int index;

    public ConfigEntryEnum(E def, EnumNameMap<E> map)
    {
        nameMap = map;
        defValue = nameMap.getIndex(def);
        index = defValue;
    }

    @Override
    public ConfigEntryType getConfigType()
    {
        return ConfigEntryType.ENUM;
    }

    @Override
    public int getColor()
    {
        return 0x0094FF;
    }

    public void set(Object o)
    {
        index = nameMap.getIndex(o);
    }

    public E get()
    {
        return nameMap.getFromIndex(index);
    }

    public int getIndex()
    {
        return index;
    }

    public void setIndex(int idx)
    {
        index = idx;

        if(index < 0 || index >= nameMap.size)
        {
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    @Override
    public void fromJson(@Nonnull JsonElement o)
    {
        set(nameMap.get(o.getAsString()));
    }

    @Nonnull
    @Override
    public JsonElement getSerializableElement()
    {
        return new JsonPrimitive(EnumNameMap.getEnumName(get()));
    }

    @Override
    public ConfigEntry copy()
    {
        ConfigEntryEnum<E> entry = new ConfigEntryEnum<E>(nameMap.getFromIndex(getIndex()), nameMap);
        entry.setIndex(getIndex());
        return entry;
    }

    @Override
    public void writeData(ByteBuf io, boolean extended)
    {
        super.writeData(io, extended);

        if(extended)
        {
            io.writeByte(nameMap.size);
            
            for(int i = 0; i < nameMap.size; i++)
            {
                for(E e : nameMap.getValues())
                {
                    MessageLM.writeString(io, EnumNameMap.getEnumName(e));
                }
            }

            io.writeShort(defValue);
        }

        MessageLM.writeString(io, EnumNameMap.getEnumName(get()));
    }

    @Override
    public void readData(ByteBuf io, boolean extended)
    {
        super.readData(io, extended);
        index = nameMap.getStringIndex(MessageLM.readString(io));
    }

    @Override
    public void onClicked(@Nonnull MouseButton button)
    {
        if(button.isLeft())
        {
            index = (index + 1) % nameMap.size;
        }
        else
        {
            if(--index < 0)
            {
                index = nameMap.size - 1;
            }
        }
    }

    @Override
    public String getAsString()
    {
        return EnumNameMap.getEnumName(get());
    }

    @Override
    public boolean getAsBoolean()
    {
        return get() != null;
    }

    @Override
    public int getAsInt()
    {
        return getIndex();
    }

    @Override
    public String getDefValueString()
    {
        return EnumNameMap.getEnumName(nameMap.getFromIndex(defValue));
    }

    public boolean isDefault()
    {
        return index == defValue;
    }

    @Override
    public List<String> getVariants()
    {
        List<String> list = new ArrayList<>(nameMap.size);
        list.addAll(nameMap.getKeys());
        return list;
    }
}