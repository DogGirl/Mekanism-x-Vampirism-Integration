package com.msdoggirl.mekvamp;

import mekanism.common.registration.impl.ModuleDeferredRegister;
import mekanism.common.registration.impl.ModuleRegistryObject;
import net.minecraft.world.item.Rarity;

public class Modules {
    public static final ModuleDeferredRegister MODULES = new ModuleDeferredRegister(MekVamp.MODID);
    public static final ModuleRegistryObject<SunscreenModule> SUNSCREEN_MODULE = MODULES.register("sunscreen_unit", SunscreenModule::new, () -> Items.SUNSCREEN_MODULE.get(), m -> m.rarity(Rarity.RARE));
    public static final ModuleRegistryObject<AutoFeedingModule> FEEDING_MODULE = MODULES.register("auto_feeding_unit", AutoFeedingModule::new, () -> Items.FEEDING_MODULE.get(), m -> m.rarity(Rarity.RARE));
}
