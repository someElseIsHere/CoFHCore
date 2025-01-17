package cofh.core.compat.jei;

import cofh.core.client.gui.ContainerScreenCoFH;
import cofh.core.common.network.packet.server.GhostItemPacket;
import cofh.lib.common.inventory.SlotFalseCopy;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

import static cofh.core.util.helpers.ItemHelper.cloneStack;

@SuppressWarnings ({"rawtypes", "unchecked"})
public class FalseCopySlotGhostHandler implements IGhostIngredientHandler<ContainerScreenCoFH> {

    @Override
    public <I> List<Target<I>> getTargetsTyped(ContainerScreenCoFH gui, ITypedIngredient<I> ingredient, boolean doStart) {

        ItemStack ingStack = ItemStack.EMPTY;
        if (ingredient instanceof FluidStack fluid && fluid.getFluid().getBucket() != Items.AIR) {
            ingStack = cloneStack(fluid.getFluid().getBucket());
        } else if (ingredient.getIngredient() instanceof ItemStack item) {
            ingStack = cloneStack(item);
        }
        List<Target<I>> targets = new ArrayList<>();
        for (int i = 0; i < gui.getMenu().slots.size(); ++i) {
            Slot slot = gui.getMenu().getSlot(i);

            if (slot instanceof SlotFalseCopy && !ingStack.isEmpty() && slot.mayPlace(ingStack)) {
                Rect2i bounds = new Rect2i(gui.getGuiLeft() + slot.x, gui.getGuiTop() + slot.y, 16, 16);
                ItemStack finalStack = ingStack;
                targets.add(new Target<>() {

                    @Override
                    public Rect2i getArea() {

                        return bounds;
                    }

                    @Override
                    public void accept(I ingredient) {

                        slot.set(finalStack);
                        GhostItemPacket.sendToServer(slot.slot, finalStack);
                    }
                });
            }
        }
        return targets;
    }

    @Override
    public void onComplete() {

    }

}
