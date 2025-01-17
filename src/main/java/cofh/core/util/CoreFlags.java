package cofh.core.util;

import cofh.lib.util.flags.FlagManager;

import java.util.function.Supplier;

import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;

public class CoreFlags {

    private CoreFlags() {

    }

    private static final FlagManager FLAG_MANAGER = new FlagManager(ID_COFH_CORE);

    public static FlagManager manager() {

        return FLAG_MANAGER;
    }

    public static void setFlag(String flag, boolean enable) {

        FLAG_MANAGER.setFlag(flag, enable);
    }

    public static void setFlag(String flag, Supplier<Boolean> condition) {

        FLAG_MANAGER.setFlag(flag, condition);
    }

    public static Supplier<Boolean> getFlag(String flag) {

        return FLAG_MANAGER.getFlag(flag);
    }

}
