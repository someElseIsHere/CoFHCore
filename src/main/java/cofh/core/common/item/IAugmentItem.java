package cofh.core.common.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * This is a purely optional interface for use with Augments to help reduce NBT overhead.
 * <p>
 * It essentially allows for an NBT Tag to be cached on the singleton instance of the item,
 * rather than requiring one on every ItemStack.
 */
public interface IAugmentItem {

    @Nullable
    CompoundTag getAugmentData(ItemStack augment);

}
