package com.ccindustrial;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

/**
 * CC: Industrial: Industrial Craft 2: Nostalgia blocks as CC: Tweaked peripherals. Both mods are
 * required dependencies (see neoforge.mods.toml).
 */
@Mod(CCIndustrial.MOD_ID)
public final class CCIndustrial {
    public static final String MOD_ID = "ccindustrial";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CCIndustrial(IEventBus modBus) {
        modBus.addListener(CCIndustrial::onCommonSetup);
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(com.ccindustrial.ic2.Ic2ComputerBridge::register);
    }
}
