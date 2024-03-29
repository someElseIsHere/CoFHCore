package cofh.lib.common.effect;

import cofh.core.common.network.packet.client.EffectAddedPacket;
import cofh.core.common.network.packet.client.EffectRemovedPacket;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public abstract class CustomParticleMobEffect extends MobEffectCoFH {

    public CustomParticleMobEffect(MobEffectCategory typeIn, int liquidColorIn) {

        super(typeIn, liquidColorIn);
    }

    @Override
    public void onApply(LivingEntity entity, MobEffectInstance instance) {

        EffectAddedPacket.sendToClient(entity, instance);
    }

    @Override
    public void onTrack(LivingEntity entity, MobEffectInstance instance, Player tracker) {

        EffectAddedPacket.sendToClient(entity, instance, tracker);
    }

    @Override
    public void onRemove(LivingEntity entity, MobEffectInstance instance) {

        EffectRemovedPacket.sendToClient(entity, instance);
    }

    @Override
    public void applyEffectTick(LivingEntity living, int amplifier) {

        if (living.level.isClientSide && living.level.random.nextInt(getChance()) == 0) {
            living.level.addParticle(getParticle(), living.getRandomX(1.0D), living.getRandomY(), living.getRandomZ(1.0D), 0.0D, 0.0D, 0.0D);
        }
    }

    public abstract ParticleOptions getParticle();

    public int getChance() {

        return 3;
    }

}
