package cofh.core.init.data.providers;

import cofh.lib.init.data.BlockStateProviderCoFH;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;

import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;

public class CoreBlockStateProvider extends BlockStateProviderCoFH {

    public CoreBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {

        super(output, ID_COFH_CORE, existingFileHelper);
    }

    @Override
    public String getName() {

        return "CoFH Core: BlockStates";
    }

    @Override
    protected void registerStatesAndModels() {

    }

}
