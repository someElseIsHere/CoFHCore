package cofh.core.init.data.providers;

import cofh.core.init.data.tables.CoreBlockLootTables;
import cofh.lib.init.data.LootTableProviderCoFH;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;

public class CoreLootTableProvider extends LootTableProviderCoFH {

    public CoreLootTableProvider(PackOutput output) {

        super(output, List.of(
                new SubProviderEntry(CoreBlockLootTables::new, LootContextParamSets.BLOCK)
        ));
    }

}


