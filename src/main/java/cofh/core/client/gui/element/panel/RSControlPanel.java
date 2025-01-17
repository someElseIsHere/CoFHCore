package cofh.core.client.gui.element.panel;

import cofh.core.client.gui.IGuiAccess;
import cofh.core.util.helpers.RenderHelper;
import cofh.lib.api.control.IRedstoneControllable;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;

import static cofh.core.util.helpers.GuiHelper.*;
import static cofh.lib.api.control.IRedstoneControllable.ControlMode.*;
import static cofh.lib.util.helpers.SoundHelper.playClickSound;
import static cofh.lib.util.helpers.StringHelper.localize;

public class RSControlPanel extends PanelBase {

    public static int defaultSide = RIGHT;
    public static int defaultHeaderColor = 0xe1c92f;
    public static int defaultSubHeaderColor = 0xaaafb8;
    public static int defaultTextColor = 0x101010;
    public static int defaultBackgroundColor = 0xd0230a;

    private final IRedstoneControllable myRSControllable;

    public RSControlPanel(IGuiAccess gui, IRedstoneControllable rsControllable) {

        this(gui, defaultSide, rsControllable);
    }

    protected RSControlPanel(IGuiAccess gui, int sideIn, IRedstoneControllable rsControllable) {

        super(gui, sideIn);

        headerColor = defaultHeaderColor;
        subheaderColor = defaultSubHeaderColor;
        textColor = defaultTextColor;
        backgroundColor = defaultBackgroundColor;

        maxHeight = 92;
        maxWidth = 112;
        myRSControllable = rsControllable;

        this.setVisible(myRSControllable::isControllable);
    }

    // TODO: Fully support new Redstone Control system.
    @Override
    protected void drawForeground(GuiGraphics pGuiGraphics) {

        drawPanelIcon(pGuiGraphics, ICON_REDSTONE_ON);
        if (!fullyOpen) {
            return;
        }
        pGuiGraphics.drawString(fontRenderer(), localize("info.cofh.redstone_control"), sideOffset() + 18, 6, headerColor, true);
        pGuiGraphics.drawString(fontRenderer(), localize("info.cofh.control_status") + ":", sideOffset() + 6, 42, subheaderColor, true);
        pGuiGraphics.drawString(fontRenderer(), localize("info.cofh.signal_required") + ":", sideOffset() + 6, 66, subheaderColor, true);

        gui.drawIcon(pGuiGraphics, ICON_BUTTON, 28, 20);
        gui.drawIcon(pGuiGraphics, ICON_BUTTON, 48, 20);
        gui.drawIcon(pGuiGraphics, ICON_BUTTON, 68, 20);

        switch (myRSControllable.getMode()) {
            case DISABLED -> {
                gui.drawIcon(pGuiGraphics, ICON_BUTTON_HIGHLIGHT, 28, 20);
                pGuiGraphics.drawString(fontRenderer(), localize("info.cofh.disabled"), sideOffset() + 14, 54, textColor, false);
                pGuiGraphics.drawString(fontRenderer(), localize("info.cofh.ignored"), sideOffset() + 14, 78, textColor, false);
            }
            case LOW -> {
                gui.drawIcon(pGuiGraphics, ICON_BUTTON_HIGHLIGHT, 48, 20);
                pGuiGraphics.drawString(fontRenderer(), localize("info.cofh.enabled"), sideOffset() + 14, 54, textColor, false);
                pGuiGraphics.drawString(fontRenderer(), localize("info.cofh.low"), sideOffset() + 14, 78, textColor, false);
            }
            case HIGH -> {
                gui.drawIcon(pGuiGraphics, ICON_BUTTON_HIGHLIGHT, 68, 20);
                pGuiGraphics.drawString(fontRenderer(), localize("info.cofh.enabled"), sideOffset() + 14, 54, textColor, false);
                pGuiGraphics.drawString(fontRenderer(), localize("info.cofh.high"), sideOffset() + 14, 78, textColor, false);
            }
            default -> {
            }
        }
        gui.drawIcon(pGuiGraphics, ICON_REDSTONE_OFF, 28, 20);
        gui.drawIcon(pGuiGraphics, ICON_RS_TORCH_OFF, 48, 20);
        gui.drawIcon(pGuiGraphics, ICON_RS_TORCH_ON, 68, 20);

        RenderHelper.resetShaderColor();
    }

    @Override
    protected void drawBackground(GuiGraphics pGuiGraphics) {

        super.drawBackground(pGuiGraphics);

        if (!fullyOpen) {
            return;
        }
        float colorR = (backgroundColor >> 16 & 255) / 255.0F * 0.6F;
        float colorG = (backgroundColor >> 8 & 255) / 255.0F * 0.6F;
        float colorB = (backgroundColor & 255) / 255.0F * 0.6F;
        RenderHelper.setPosTexShader();
        RenderSystem.setShaderColor(colorR, colorG, colorB, 1.0F);
        gui.drawTexturedModalRect(pGuiGraphics, 24, 16, 16, 20, 64, 24);
        RenderHelper.resetShaderColor();
    }

    @Override
    public void addTooltip(List<Component> tooltipList, int mouseX, int mouseY) {

        if (!fullyOpen) {
            tooltipList.add(Component.translatable("info.cofh.redstone_control"));

            switch (myRSControllable.getMode()) {
                case DISABLED ->
                        tooltipList.add(Component.translatable("info.cofh.disabled").withStyle(ChatFormatting.YELLOW));
                case LOW -> tooltipList.add(Component.translatable("info.cofh.low").withStyle(ChatFormatting.YELLOW));
                case HIGH -> tooltipList.add(Component.translatable("info.cofh.high").withStyle(ChatFormatting.YELLOW));
                default -> {
                }
            }
            tooltipList.add(Component.translatable("info.cofh.current_signal", myRSControllable.getPower())
                    .withStyle(myRSControllable.getMode() == DISABLED
                            ? ChatFormatting.YELLOW
                            : myRSControllable.getState()
                            ? ChatFormatting.GREEN
                            : ChatFormatting.RED));
            return;
        }
        int x = mouseX - this.posX();
        int y = mouseY - this.posY();

        if (28 <= x && x < 44 && 20 <= y && y < 36) {
            tooltipList.add(Component.translatable("info.cofh.ignored"));
        } else if (48 <= x && x < 64 && 20 <= y && y < 36) {
            tooltipList.add(Component.translatable("info.cofh.low"));
        } else if (68 <= x && x < 84 && 20 <= y && y < 36) {
            tooltipList.add(Component.translatable("info.cofh.high"));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {

        if (!fullyOpen) {
            return false;
        }
        double x = mouseX - this.posX();
        double y = mouseY - this.posY();

        if (x < 24 || x >= 88 || y < 16 || y >= 40) {
            return false;
        }
        if (28 <= x && x < 44 && 20 <= y && y < 36) {
            if (myRSControllable.getMode() != DISABLED) {
                myRSControllable.setControl(0, DISABLED);
                playClickSound(0.4F);
            }
        } else if (48 <= x && x < 64 && 20 <= y && y < 36) {
            if (myRSControllable.getMode() != LOW) {
                myRSControllable.setControl(0, LOW);
                playClickSound(0.6F);
            }
        } else if (68 <= x && x < 84 && 20 <= y && y < 36) {
            if (myRSControllable.getMode() != HIGH) {
                myRSControllable.setControl(0, HIGH);
                playClickSound(0.8F);
            }
        }
        return true;
    }

}
