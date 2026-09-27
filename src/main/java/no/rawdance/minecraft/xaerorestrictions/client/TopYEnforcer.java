package no.rawdance.minecraft.xaerorestrictions.client;

import no.rawdance.minecraft.xaerorestrictions.RawXaeroRestrictions;
import no.rawdance.minecraft.xaerorestrictions.config.ServerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import xaero.map.WorldMap;
import xaero.map.config.primary.option.WorldMapPrimaryClientConfigOptions;

@EventBusSubscriber(modid = RawXaeroRestrictions.MOD_ID, value = Dist.CLIENT)
public final class TopYEnforcer {
    private static Integer originalCaveStart;
    private static boolean enforcing;

    private TopYEnforcer() {}

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || !ServerConfig.CONFIG.lockTopY.get()) {
            restoreOriginal();
            return;
        }

        try {
            var config = WorldMap.INSTANCE.getConfigs().getPrimaryClientConfigManager().getConfig();
            int current = (Integer) config.get(WorldMapPrimaryClientConfigOptions.CAVE_MODE_START);

            if (!enforcing) {
                originalCaveStart = current;
                enforcing = true;
            }

            int desired = switch (ServerConfig.normalizedTopYMode()) {
                case "PLAYER_Y" -> Mth.floor(mc.player.getY());
                case "PLAYER_Y_OFFSET" -> Mth.floor(mc.player.getY()) + ServerConfig.CONFIG.topYOffset.getAsInt();
                default -> Integer.MAX_VALUE; // Xaero Auto
            };

            if (current != desired) {
                // Deliberately do not save. This is a server-enforced runtime override,
                // not a permanent change to the player's local Xaero settings.
                config.set(WorldMapPrimaryClientConfigOptions.CAVE_MODE_START, desired);
            }
        } catch (Throwable ignored) {
            // Xaero may not be fully initialized during the first client ticks.
        }
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        restoreOriginal();
    }

    private static void restoreOriginal() {
        if (!enforcing || originalCaveStart == null) {
            enforcing = false;
            originalCaveStart = null;
            return;
        }

        try {
            var config = WorldMap.INSTANCE.getConfigs().getPrimaryClientConfigManager().getConfig();
            config.set(WorldMapPrimaryClientConfigOptions.CAVE_MODE_START, originalCaveStart);
        } catch (Throwable ignored) {
        } finally {
            enforcing = false;
            originalCaveStart = null;
        }
    }
}
