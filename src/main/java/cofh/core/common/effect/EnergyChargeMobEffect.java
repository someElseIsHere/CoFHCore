package cofh.core.common.effect;

import cofh.core.common.capability.CapabilityRedstoneFlux;
import cofh.lib.common.effect.MobEffectCoFH;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class EnergyChargeMobEffect extends MobEffectCoFH {

    private final int amount;

    public EnergyChargeMobEffect(MobEffectCategory typeIn, int liquidColorIn, int amount) {

        super(typeIn, liquidColorIn);
        this.amount = amount;
    }

    @Override
    public void applyEffectTick(LivingEntity entityLivingBaseIn, int amplifier) {

        if (entityLivingBaseIn instanceof ServerPlayer player) {

            if (amount <= 0) {
                drainForgeEnergy(player, amount);
                drainRedstoneFlux(player, amount);
            } else {
                chargeForgeEnergy(player, amount);
                chargeRedstoneFlux(player, amount);
            }
        }
    }

    // region HELPERS
    private void chargeForgeEnergy(ServerPlayer player, final int chargeAmount) {

        // Main Inventory
        for (ItemStack stack : player.getInventory().items) {
            stack.getCapability(ForgeCapabilities.ENERGY, null)
                    .ifPresent(c -> c.receiveEnergy(chargeAmount, false));
        }
        // Armor Inventory
        for (ItemStack stack : player.getInventory().armor) {
            stack.getCapability(ForgeCapabilities.ENERGY, null)
                    .ifPresent(c -> c.receiveEnergy(chargeAmount, false));
        }
        // Offhand
        for (ItemStack stack : player.getInventory().offhand) {
            stack.getCapability(ForgeCapabilities.ENERGY, null)
                    .ifPresent(c -> c.receiveEnergy(chargeAmount, false));
        }
    }

    private void chargeRedstoneFlux(ServerPlayer player, final int chargeAmount) {

        // Main Inventory
        for (ItemStack stack : player.getInventory().items) {
            stack.getCapability(CapabilityRedstoneFlux.RF_ENERGY, null)
                    .ifPresent(c -> c.receiveEnergy(chargeAmount, false));
        }
        // Armor Inventory
        for (ItemStack stack : player.getInventory().armor) {
            stack.getCapability(CapabilityRedstoneFlux.RF_ENERGY, null)
                    .ifPresent(c -> c.receiveEnergy(chargeAmount, false));
        }
        // Offhand
        for (ItemStack stack : player.getInventory().offhand) {
            stack.getCapability(CapabilityRedstoneFlux.RF_ENERGY, null)
                    .ifPresent(c -> c.receiveEnergy(chargeAmount, false));
        }
    }

    private void drainForgeEnergy(ServerPlayer player, final int drainAmount) {

        // Main Inventory
        for (ItemStack stack : player.getInventory().items) {
            stack.getCapability(ForgeCapabilities.ENERGY, null)
                    .ifPresent(c -> c.extractEnergy(drainAmount, false));
        }
        // Armor Inventory
        for (ItemStack stack : player.getInventory().armor) {
            stack.getCapability(ForgeCapabilities.ENERGY, null)
                    .ifPresent(c -> c.extractEnergy(drainAmount, false));
        }
        // Offhand
        for (ItemStack stack : player.getInventory().offhand) {
            stack.getCapability(ForgeCapabilities.ENERGY, null)
                    .ifPresent(c -> c.extractEnergy(drainAmount, false));
        }
    }

    private void drainRedstoneFlux(ServerPlayer player, final int drainAmount) {

        // Main Inventory
        for (ItemStack stack : player.getInventory().items) {
            stack.getCapability(CapabilityRedstoneFlux.RF_ENERGY, null)
                    .ifPresent(c -> c.extractEnergy(drainAmount, false));
        }
        // Armor Inventory
        for (ItemStack stack : player.getInventory().armor) {
            stack.getCapability(CapabilityRedstoneFlux.RF_ENERGY, null)
                    .ifPresent(c -> c.extractEnergy(drainAmount, false));
        }
        // Offhand
        for (ItemStack stack : player.getInventory().offhand) {
            stack.getCapability(CapabilityRedstoneFlux.RF_ENERGY, null)
                    .ifPresent(c -> c.extractEnergy(drainAmount, false));
        }
    }
    // endregion
}
