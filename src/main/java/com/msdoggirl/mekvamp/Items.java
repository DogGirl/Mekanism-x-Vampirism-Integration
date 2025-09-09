package com.msdoggirl.mekvamp;
import mekanism.common.item.ItemModule;
import mekanism.common.registration.impl.ItemDeferredRegister;
import mekanism.common.registration.impl.ItemRegistryObject;
import mekanism.common.registration.impl.ModuleRegistryObject;
public class Items {
    public static final ItemDeferredRegister ITEMS = new ItemDeferredRegister(MekVamp.MODID);
    public static final ItemRegistryObject<ItemModule> SUNSCREEN_MODULE = registerModule(ITEMS, Modules.SUNSCREEN_MODULE);
    public static final ItemRegistryObject<ItemModule> FEEDING_MODULE = registerModule(ITEMS, Modules.FEEDING_MODULE);
    
    public static ItemRegistryObject<ItemModule> registerModule(ItemDeferredRegister register, ModuleRegistryObject<?> moduleDataSupplier)
	{
		return register.registerModule(moduleDataSupplier);
	}
    

}
