package cofh.core.common.block.entity;

import cofh.core.util.helpers.XpHelper;
import cofh.lib.api.block.entity.ITileLocation;
import cofh.lib.common.xp.EmptyXpStorage;
import cofh.lib.common.xp.XpStorage;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface ITileXpHandler extends ITileLocation {

    default boolean claimXP(Player player) {

        if (!getXpStorage().isEmpty()) {
            int xp = getXpStorage().getStored();
            XpHelper.addXPToPlayer(player, xp);
            getXpStorage().clear();
            return true;
        }
        return false;
    }

    default void spawnXpOrbs(Level world, int xp, Vec3 pos) {

        if (world == null) {
            return;
        }
        while (xp > 0) {
            int orbAmount = ExperienceOrb.getExperienceValue(xp);
            xp -= orbAmount;
            world.addFreshEntity(new ExperienceOrb(world, pos.x, pos.y, pos.z, orbAmount));
        }
    }

    default XpStorage getXpStorage() {

        return EmptyXpStorage.INSTANCE;
    }

}
