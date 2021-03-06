package com.feed_the_beast.ftbl.gui.info;

import com.feed_the_beast.ftbl.api.MouseButton;
import com.feed_the_beast.ftbl.api.client.gui.GuiLM;
import com.feed_the_beast.ftbl.api.info.InfoExtendedTextLine;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LatvianModder on 04.03.2016.
 */
@SideOnly(Side.CLIENT)
public class ButtonInfoExtendedTextLine extends ButtonInfoTextLine
{
    public final InfoExtendedTextLine line;
    public List<String> hover;

    public ButtonInfoExtendedTextLine(GuiInfo g, InfoExtendedTextLine l)
    {
        super(g, l);
        line = l;

        if(l != null)
        {
            List<ITextComponent> h = l.getHover();

            if(h != null)
            {
                hover = new ArrayList<>();

                for(ITextComponent c1 : h)
                {
                    hover.add(c1.getFormattedText());
                }

                if(hover.isEmpty())
                {
                    hover = null;
                }
            }
            else
            {
                hover = null;
            }
        }
    }

    @Override
    public void addMouseOverText(GuiLM gui, List<String> l)
    {
        if(hover != null)
        {
            l.addAll(hover);
        }
    }

    @Override
    public void onClicked(@Nonnull GuiLM gui, @Nonnull MouseButton button)
    {
        if(line != null)
        {
            line.onClicked(button);
        }
    }

    @Override
    public void renderWidget(GuiLM gui)
    {
        int ay = (int) getAY();
        int ax = (int) getAX();

        if(text != null && !text.isEmpty())
        {
            for(int i = 0; i < text.size(); i++)
            {
                guiInfo.font.drawString(text.get(i), ax, ay + i * 10 + 1, guiInfo.colorText);
            }
        }
    }
}
