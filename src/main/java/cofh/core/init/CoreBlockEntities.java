package cofh.core.init;

import cofh.core.common.block.entity.EnderAirBlockEntity;
import cofh.core.common.block.entity.GlowAirBlockEntity;
import cofh.core.common.block.entity.LightningAirBlockEntity;
import cofh.core.common.block.entity.SignalAirTile;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

import static cofh.core.CoFHCore.TILE_ENTITIES;
import static cofh.core.init.CoreBlocks.*;
import static cofh.core.util.references.CoreIDs.*;

public class CoreBlockEntities {

    private CoreBlockEntities() {

    }

    public static void register() {


    }

    public static final RegistryObject<BlockEntityType<SignalAirTile>> SIGNAL_AIR_TILE = TILE_ENTITIES.register(ID_SIGNAL_AIR, () -> BlockEntityType.Builder.of(SignalAirTile::new, SIGNAL_AIR.get()).build(null));
    public static final RegistryObject<BlockEntityType<GlowAirBlockEntity>> GLOW_AIR_TILE = TILE_ENTITIES.register(ID_GLOW_AIR, () -> BlockEntityType.Builder.of(GlowAirBlockEntity::new, GLOW_AIR.get()).build(null));
    public static final RegistryObject<BlockEntityType<EnderAirBlockEntity>> ENDER_AIR_TILE = TILE_ENTITIES.register(ID_ENDER_AIR, () -> BlockEntityType.Builder.of(EnderAirBlockEntity::new, ENDER_AIR.get()).build(null));
    public static final RegistryObject<BlockEntityType<LightningAirBlockEntity>> LIGHTNING_AIR_TILE = TILE_ENTITIES.register(ID_LIGHTNING_AIR, () -> BlockEntityType.Builder.of(LightningAirBlockEntity::new, LIGHTNING_AIR.get()).build(null));

}
