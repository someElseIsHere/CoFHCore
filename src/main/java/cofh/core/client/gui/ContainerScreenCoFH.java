package cofh.core.client.gui;

import cofh.core.client.gui.element.ElementBase;
import cofh.core.client.gui.element.panel.InfoPanel;
import cofh.core.client.gui.element.panel.PanelBase;
import cofh.core.client.gui.element.panel.PanelTracker;
import cofh.core.util.helpers.RenderHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

import java.util.*;

import static cofh.core.util.helpers.GuiHelper.SLOT_SIZE_INNER;
import static cofh.lib.util.helpers.StringHelper.localize;

public class ContainerScreenCoFH<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> implements IGuiAccess {

    protected int mX;
    protected int mY;

    protected String name = "";
    protected String info = "";
    protected ResourceLocation texture;
    protected Player player;

    protected boolean drawTitle = true;
    protected boolean drawInventory = true;
    protected boolean showTooltips = true;

    private final ArrayList<PanelBase> panels = new ArrayList<>();
    private final ArrayList<ElementBase> elements = new ArrayList<>();
    private final List<Component> tooltip = new LinkedList<>();

    public ContainerScreenCoFH(T container, Inventory inv, Component titleIn) {

        super(container, inv, titleIn);
        player = inv.player;
    }

    @Override
    public void init() {

        super.init();
        panels.clear();
        elements.clear();

        if (info != null && !info.isEmpty()) {
            addPanel(new InfoPanel(this, info));
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

        mX = pMouseX - leftPos;
        mY = pMouseY - topPos;

        updatePanels();
        updateElements();

        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);

        if (showTooltips && this.menu.getCarried().isEmpty()) {
            drawTooltip(pGuiGraphics);
        }
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {

        RenderHelper.setPosTexShader();
        RenderHelper.resetShaderColor();
        RenderHelper.setShaderTexture0(texture);

        drawTexturedModalRect(pGuiGraphics, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(leftPos, topPos, 0.0F);

        drawPanels(pGuiGraphics, false);
        drawElements(pGuiGraphics, false);

        pGuiGraphics.pose().popPose();
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int mouseX, int mouseY) {

        if (drawTitle & title != null) {
            pGuiGraphics.drawString(font, localize(title.getString()), getCenteredOffset(localize(title.getString())), 6, 0x404040, false);
        }
        if (drawInventory) {
            pGuiGraphics.drawString(font, localize("container.inventory"), 8, imageHeight - 96 + 3, 0x404040, false);
        }
        drawPanels(pGuiGraphics, true);
        drawElements(pGuiGraphics, true);
    }

    protected void renderSlotGradient(GuiGraphics pGuiGraphics, Slot slot, int color1, int color2) {

        int x = guiLeft() + slot.x;
        int y = guiTop() + slot.y;
        pGuiGraphics.fillGradient(x, y, x + SLOT_SIZE_INNER, y + SLOT_SIZE_INNER, color1, color2);
    }

    // region ELEMENTS
    public void drawTooltip(GuiGraphics pGuiGraphics) {

        PanelBase panel = getPanelAtPosition(mX, mY);

        if (panel != null) {
            panel.addTooltip(tooltip, mX, mY);
        }
        ElementBase element = getElementAtPosition(mX, mY);

        if (element != null && element.visible()) {
            element.addTooltip(tooltip, mX, mY);
        }
        pGuiGraphics.renderTooltip(font, tooltip, Optional.empty(), mX + leftPos, mY + topPos);
        tooltip.clear();
    }

    /**
     * Draws the Elements for this GUI.
     */
    protected void drawElements(GuiGraphics pGuiGraphics, boolean foreground) {

        if (foreground) {
            for (ElementBase c : elements) {
                if (c.visible()) {
                    c.drawForeground(pGuiGraphics, mX, mY);
                }
            }
        } else {
            for (ElementBase c : elements) {
                if (c.visible()) {
                    c.drawBackground(pGuiGraphics, mX, mY);
                }
            }
        }
    }

    /**
     * Draws the Panels for this GUI. Open / close animation is part of this.
     */
    protected void drawPanels(GuiGraphics pGuiGraphics, boolean foreground) {

        int yPosRight = 4;
        int yPosLeft = 4;

        if (foreground) {
            for (PanelBase panel : panels) {
                panel.updateSize();
                if (!panel.visible()) {
                    continue;
                }
                if (panel.side == PanelBase.LEFT) {
                    panel.drawForeground(pGuiGraphics, mX, mY);
                    yPosLeft += panel.height();
                } else {
                    panel.drawForeground(pGuiGraphics, mX, mY);
                    yPosRight += panel.height();
                }
            }
        } else {
            for (PanelBase panel : panels) {
                panel.updateSize();
                if (!panel.visible()) {
                    continue;
                }
                if (panel.side == PanelBase.LEFT) {
                    panel.setPosition(0, yPosLeft);
                    panel.drawBackground(pGuiGraphics, mX, mY);
                    yPosLeft += panel.height();
                } else {
                    panel.setPosition(imageWidth, yPosRight);
                    panel.drawBackground(pGuiGraphics, mX, mY);
                    yPosRight += panel.height();
                }
            }
        }
    }

    @SuppressWarnings ("unchecked")
    protected <T> T addElement(ElementBase element) {

        elements.add(element);
        return (T) element;
    }

    public final void addElements(ElementBase... c) {

        elements.addAll(Arrays.asList(c));
    }

    @SuppressWarnings ("unchecked")
    protected <T> T addPanel(PanelBase panel) {

        int yOffset = 4;
        for (PanelBase panel1 : panels) {
            if (panel1.side == panel.side && panel1.visible()) {
                yOffset += panel1.height();
            }
        }
        panel.setPosition(panel.side == PanelBase.LEFT ? 0 : imageWidth, yOffset);
        panels.add(panel);

        if (PanelTracker.getOpenedLeft() != null && panel.getClass().equals(PanelTracker.getOpenedLeft())) {
            panel.setFullyOpen();
        } else if (PanelTracker.getOpenedRight() != null && panel.getClass().equals(PanelTracker.getOpenedRight())) {
            panel.setFullyOpen();
        }
        return (T) panel;
    }

    private ElementBase getElementAtPosition(int mouseX, int mouseY) {

        for (int i = elements.size(); i-- > 0; ) {
            ElementBase element = elements.get(i);
            if (element.intersectsWith(mouseX, mouseY) && element.visible()) {
                return element;
            }
        }
        return null;
    }

    private PanelBase getPanelAtPosition(double mouseX, double mouseY) {

        int xShift = 0;
        int yShift = 4;

        // LEFT SIDE
        for (PanelBase panel : panels) {
            if (!panel.visible() || panel.side == PanelBase.RIGHT) {
                continue;
            }
            panel.setShift(xShift, yShift);
            if (panel.intersectsWith(mouseX, mouseY, xShift, yShift)) {
                return panel;
            }
            yShift += panel.height();
        }

        xShift = imageWidth;
        yShift = 4;
        // RIGHT SIDE
        for (PanelBase panel : panels) {
            if (!panel.visible() || panel.side == PanelBase.LEFT) {
                continue;
            }
            panel.setShift(xShift, yShift);
            if (panel.intersectsWith(mouseX, mouseY, xShift, yShift)) {
                return panel;
            }
            yShift += panel.height();
        }
        return null;
    }

    private void updateElements() {

        for (int i = elements.size(); i-- > 0; ) {
            ElementBase c = elements.get(i);
            if (c.visible() && c.enabled()) {
                c.update(mX, mY);
            }
        }
    }

    private void updatePanels() {

        for (int i = panels.size(); i-- > 0; ) {
            PanelBase c = panels.get(i);
            if (c.visible() && c.enabled()) {
                c.update(mX, mY);
            }
        }
    }

    public List<Rect2i> getPanelBounds() {

        List<Rect2i> panelBounds = new ArrayList<>();

        for (PanelBase c : panels) {
            panelBounds.add(c.getBoundsOnScreen());
        }
        return panelBounds;
    }
    // endregion

    // region CALLBACKS
    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeftIn, int guiTopIn, int mouseButton) {

        return super.hasClickedOutside(mouseX, mouseY, guiLeftIn, guiTopIn, mouseButton) && getPanelAtPosition(mouseX - guiLeftIn, mouseY - guiTopIn) == null;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {

        mouseX -= leftPos;
        mouseY -= topPos;

        for (int i = elements.size(); i-- > 0; ) {
            ElementBase c = elements.get(i);
            if (!c.visible() || !c.enabled() || !c.intersectsWith(mouseX, mouseY)) {
                continue;
            }
            if (c.mouseClicked(mouseX, mouseY, mouseButton)) {
                return true;
            }
        }
        PanelBase panel = getPanelAtPosition(mouseX, mouseY);
        if (panel != null) {
            if (!panel.mouseClicked(mouseX, mouseY, mouseButton)) {
                for (int i = panels.size(); i-- > 0; ) {
                    PanelBase other = panels.get(i);
                    if (other != panel && other.open && other.side == panel.side) {
                        other.toggleOpen();
                    }
                }
                panel.toggleOpen();
                return true;
            }
        }

        // If a panel is open, expand GUI size to support slot actions.
        if (panel != null) {
            switch (panel.side) {
                case PanelBase.LEFT:
                    leftPos -= panel.width();
                    break;
                case PanelBase.RIGHT:
                    imageWidth += panel.width();
                    break;
            }
        }
        mouseX += leftPos;
        mouseY += topPos;

        boolean ret = super.mouseClicked(mouseX, mouseY, mouseButton);

        // Re-adjust GUI size after click has happened.
        if (panel != null) {
            switch (panel.side) {
                case PanelBase.LEFT:
                    leftPos += panel.width();
                    break;
                case PanelBase.RIGHT:
                    imageWidth -= panel.width();
                    break;
            }
        }
        return ret;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {

        mouseX -= leftPos;
        mouseY -= topPos;

        if (mouseButton >= 0 && mouseButton <= 2) { // 0:left, 1:right, 2: middle
            for (int i = elements.size(); i-- > 0; ) {
                ElementBase c = elements.get(i);
                if (!c.visible() || !c.enabled()) { // no bounds checking on mouseUp events
                    continue;
                }
                c.mouseReleased(mouseX, mouseY);
            }
        }
        mouseX += leftPos;
        mouseY += topPos;

        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double movement) {

        if (movement != 0) {
            for (int i = elements.size(); i-- > 0; ) {
                ElementBase c = elements.get(i);
                if (!c.visible() || !c.enabled() || !c.intersectsWith(mX, mY)) {
                    continue;
                }
                if (c.mouseWheel(mX, mY, movement)) {
                    return true;
                }
            }
            PanelBase panel = getPanelAtPosition(mX, mY);
            if (panel != null && panel.mouseWheel(mX, mY, movement)) {
                return true;
            }
            return mouseWheel(mX, mY, movement);
        }
        return false;
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {

        for (int i = elements.size(); i-- > 0; ) {
            ElementBase c = elements.get(i);
            if (!c.visible() || !c.enabled()) {
                continue;
            }
            if (c.keyTyped(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
                return true;
            }
        }
        return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
    }
    // endregion

    // region HELPERS
    protected boolean mouseWheel(double mouseX, double mouseY, double movement) {

        return false;
    }

    protected int getCenteredOffset(String string) {

        return this.getCenteredOffset(string, imageWidth / 2);
    }

    protected int getCenteredOffset(String string, int xPos) {

        return ((xPos * 2) - font.width(string)) / 2;
    }
    // endregion

    // region IGuiAccess
    @Override
    public int guiTop() {

        return topPos;
    }

    @Override
    public int guiLeft() {

        return leftPos;
    }

    @Override
    public final Font fontRenderer() {

        return font;
    }

    @Override
    public final Player player() {

        return player;
    }

    @Override
    public int blitOffset() {

        return 0;
    }
    // endregion
}
