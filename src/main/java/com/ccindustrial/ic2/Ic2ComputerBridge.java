package com.ccindustrial.ic2;

import com.ccindustrial.CCIndustrial;
import dan200.computercraft.api.ComputerCraftAPI;

/**
 * Industrial Craft 2 + CC: Tweaked. Registers generic peripheral sources, so IC2 blocks become
 * peripherals on their own: place a computer (or a wired modem) next to a machine, a BatBox or the
 * reactor and call {@code peripheral.wrap(side)}. Nothing in IC2 is changed.
 *
 * <p>Only loaded when both mods are installed (see {@link CCIndustrial}).
 */
public final class Ic2ComputerBridge {
    private Ic2ComputerBridge() {}

    public static void register() {
        ComputerCraftAPI.registerGenericSource(new Ic2EnergyMethods());
        ComputerCraftAPI.registerGenericSource(new Ic2StorageMethods());
        ComputerCraftAPI.registerGenericSource(new Ic2MachineMethods());
        ComputerCraftAPI.registerGenericSource(new Ic2GeneratorMethods());
        ComputerCraftAPI.registerGenericSource(new Ic2ReactorMethods());
        CCIndustrial.LOGGER.info("CC: Industrial: IC2 blocks are now CC: Tweaked peripherals");
    }
}
