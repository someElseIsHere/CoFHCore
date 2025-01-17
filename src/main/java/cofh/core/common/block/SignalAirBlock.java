package cofh.core.common.block;

import cofh.core.common.block.entity.SignalAirTile;
import cofh.lib.api.block.entity.ITickableTile;
import cofh.lib.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import static cofh.core.init.CoreBlockEntities.SIGNAL_AIR_TILE;

public class SignalAirBlock extends AirBlock implements EntityBlock {

    public SignalAirBlock(Properties builder) {

        super(builder);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {

        return new SignalAirTile(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> actualType) {

        return ITickableTile.createTicker(level, actualType, SIGNAL_AIR_TILE.get(), SignalAirTile.class);
    }

    @Override
    public boolean isSignalSource(BlockState state) {

        return true;
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {

        return blockAccess.getBlockEntity(pos) instanceof SignalAirTile tile ? tile.getPower() : 0;
    }

    @Override
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand) {

        if (rand.nextInt(8) == 0) {
            Utils.spawnBlockParticlesClient(worldIn, DustParticleOptions.REDSTONE, pos, rand, 2);
        }
    }

}
