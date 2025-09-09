package com.msdoggirl.mekvamp;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import mekanism.api.gear.IModule;
import mekanism.api.gear.IModuleHelper;
import mekanism.api.math.FloatingLong;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import mekanism.api.text.APILang;
import net.minecraft.world.level.Level;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(MekVamp.MODID)
public class MekVamp {
    
    public static final String MODID = "mekvamp";
    private static final Logger LOGGER = LogManager.getLogger();
    private static final FloatingLong ENERGY_COST = FloatingLong.createConst(50);

    public MekVamp() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        Items.ITEMS.register(modBus);
        Modules.MODULES.register(modBus);
        modBus.addListener(this::commonSetup);
        modBus.addListener(this::addToCreativeTabs);
        MinecraftForge.EVENT_BUS.register(this);
        modBus.register(MekanismListener.class);

        
    }

    private void commonSetup(FMLCommonSetupEvent event) { }

    private void addToCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(Items.SUNSCREEN_MODULE.get());
        }
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(Items.FEEDING_MODULE.get());
        }
    }


    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {

  

        if (event.phase != TickEvent.Phase.END) {
            return;
        }


        for (Player player : event.getServer().getPlayerList().getPlayers()) {
            // Only vampires

            Level world = player.getCommandSenderWorld();
            world.getProfiler().push("vampirism_vampirePlayer");
            AutofeederLogic.tick(player);

            // Daylight + open sky
            if (VampirismAPI.factionRegistry().getFaction(player) == VReference.VAMPIRE_FACTION &&
                VampirismAPI.sundamageRegistry().isGettingSundamage(player, world)) {

                ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
                IModule<SunscreenModule> module = IModuleHelper.INSTANCE.load(helmet, Modules.SUNSCREEN_MODULE);
                if (module != null && module.isEnabled()) {
                    FloatingLong stored = module.getContainerEnergy();
                    if (stored.greaterOrEqual(ENERGY_COST)) {
                        module.useEnergy(player, ENERGY_COST, false);
                        player.addEffect(new MobEffectInstance(ModEffects.SUNSCREEN.get(), 60, 5, false, false));
                    }
                }
            }

            
        }

    }
}