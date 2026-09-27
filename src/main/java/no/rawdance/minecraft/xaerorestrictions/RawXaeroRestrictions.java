package no.rawdance.minecraft.xaerorestrictions;

import no.rawdance.minecraft.xaerorestrictions.command.AdminCommands;
import no.rawdance.minecraft.xaerorestrictions.config.ServerConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

@Mod(RawXaeroRestrictions.MOD_ID)
public final class RawXaeroRestrictions {
    public static final String MOD_ID = "raw_xaero_restrictions";

    public RawXaeroRestrictions(IEventBus modBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC, "raw-xaero-restrictions.toml");
        NeoForge.EVENT_BUS.addListener(AdminCommands::register);
    }
}
