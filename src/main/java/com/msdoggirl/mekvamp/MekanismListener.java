package com.msdoggirl.mekvamp;

import mekanism.api.MekanismIMC;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

public class MekanismListener {
    @SubscribeEvent
	public static void onInterModeEnqueue(InterModEnqueueEvent event)
	{
		String modid = "mekanism";
		InterModComms.sendTo(modid, MekanismIMC.ADD_MEKA_SUIT_HELMET_MODULES, Modules.SUNSCREEN_MODULE);
		InterModComms.sendTo(modid, MekanismIMC.ADD_MEKA_SUIT_HELMET_MODULES, Modules.FEEDING_MODULE);
	}
}
