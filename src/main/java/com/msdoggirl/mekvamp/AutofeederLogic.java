package com.msdoggirl.mekvamp;

import java.util.concurrent.atomic.AtomicInteger;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.vampire.IBloodStats;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.entity.vampire.DrinkBloodContext;
import mekanism.api.gear.IModule;
import mekanism.api.gear.IModuleHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;



public class AutofeederLogic {
    private static final int BLOOD_STEP = 100; // mB per feed step


    public static void tick(Player player) {
        // Server-side only
        if (player.level().isClientSide) return;

        // Only act on vampires
        if (VampirismAPI.factionRegistry().getFaction(player) != VReference.VAMPIRE_FACTION) return;

        CompoundTag data = player.getPersistentData();
        long currentTime = player.level().getGameTime();
        long lastFeed = data.getLong("mekvamp_last_feed");


        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD); // or whichever slot you want
        IModule<AutoFeedingModule> module = IModuleHelper.INSTANCE.load(helmet, Modules.FEEDING_MODULE);
        if (module == null || !module.isEnabled()) return;


        IVampirePlayer vampire = VampirismAPI.getVampirePlayer(player).orElse(null);
        if (vampire == null) return;

        IBloodStats stats = vampire.getBloodStats();
        if (stats == null) return;

        if (currentTime - lastFeed < 200) return;

        int current = stats.getBloodLevel();
        int max = stats.getMaxBlood();
        int missing = max - current;
        if (missing <= 0) return;

        int request = Math.min(BLOOD_STEP, missing) * 100;

        for (ItemStack stack : player.getInventory().items) {
            if (stack.isEmpty()) continue;
            int gained = drainBloodFromItem(stack, request);
            if (gained > 0) {
                vampire.drinkBlood(gained / 100, 0, new DrinkBloodContext(stack.copy()));
                data.putLong("mekvamp_last_feed", currentTime);
                System.out.println("Player is missing " + request + " units of blood.");
                return;
            }
        }
    }

    private static int drainBloodFromItem(ItemStack stack, int maxAmount) {
    
    if (stack.isEmpty()) return 0;

        AtomicInteger drainedAmount = new AtomicInteger(0);
        stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(handler -> {
            FluidStack wanted = new FluidStack(VReference.BLOOD.get(), maxAmount);
            // Simulate to verify fluid type and availability
            FluidStack sim = handler.drain(wanted, IFluidHandler.FluidAction.SIMULATE);
            if (!sim.isEmpty() && sim.getAmount() > 0 && sim.getFluid().isSame(VReference.BLOOD.get())) {
                FluidStack exec = handler.drain(wanted, IFluidHandler.FluidAction.EXECUTE);
                drainedAmount.set(exec.getAmount());
            }
        });

        return drainedAmount.get();
    }

}

