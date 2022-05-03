package cofh.lib.effect;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public abstract class CustomParticleEffect extends EffectCoFH {

    public CustomParticleEffect(MobEffectCategory typeIn, int liquidColorIn) {

        super(typeIn, liquidColorIn);
    }

    @Override
    public void applyEffectTick(LivingEntity living, int amplifier) {

        if (living.level.isClientSide && living.level.random.nextInt(getChance()) == 0) {
            //((ServerWorld) living.level).sendParticles(getParticle(), living.getRandomX(1.0D), living.getRandomY(), living.getRandomZ(1.0D), 1, 0, 0, 0, 0);
            living.level.addParticle(getParticle(), living.getRandomX(1.0D), living.getRandomY(), living.getRandomZ(1.0D), 0.0D, 0.0D, 0.0D);
        }
    }

    public abstract ParticleOptions getParticle();

    public int getChance() {

        return 3;
    }

}
