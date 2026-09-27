package no.rawdance.minecraft.xaerorestrictions.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Locale;
import java.util.Set;

public final class ServerConfig {
    public static final ServerConfig CONFIG;
    public static final ModConfigSpec SPEC;

    private static final Set<String> TOP_Y_MODES = Set.of("AUTO", "PLAYER_Y", "PLAYER_Y_OFFSET");

    public final ModConfigSpec.BooleanValue lockCaveModeType;
    public final ModConfigSpec.IntValue forcedCaveModeType;
    public final ModConfigSpec.BooleanValue lockTopY;
    public final ModConfigSpec.ConfigValue<String> topYMode;
    public final ModConfigSpec.IntValue topYOffset;
    public final ModConfigSpec.BooleanValue disableTopYControls;

    public final ModConfigSpec.BooleanValue limitSurfaceWritingDistance;
    public final ModConfigSpec.IntValue surfaceWritingDistance;
    public final ModConfigSpec.BooleanValue limitCaveWritingDistance;
    public final ModConfigSpec.IntValue caveWritingDistance;

    private ServerConfig(ModConfigSpec.Builder builder) {
        builder.push("cave_mode");

        lockCaveModeType = builder
                .comment("Force Xaero's per-dimension Cave Mode Type. Recommended: true.")
                .define("lock_cave_mode_type", true);

        forcedCaveModeType = builder
                .comment("0 = Off, 1 = Layered, 2 = Full. Recommended: 1 (Layered).")
                .defineInRange("forced_cave_mode_type", 1, 0, 2);

        lockTopY = builder
                .comment("Prevent players from choosing Cave Mode Top Y manually.")
                .define("lock_top_y", true);

        topYMode = builder
                .comment("AUTO uses Xaero's own cave/roof detection. PLAYER_Y follows the player exactly. PLAYER_Y_OFFSET follows player Y + top_y_offset.")
                .define("top_y_mode", "AUTO", value -> value instanceof String s && TOP_Y_MODES.contains(s.toUpperCase(Locale.ROOT)));

        topYOffset = builder
                .comment("Offset used only by PLAYER_Y_OFFSET.")
                .defineInRange("top_y_offset", 4, -64, 64);

        disableTopYControls = builder
                .comment("Disable the Top Y slider/text field while Top Y is locked.")
                .define("disable_top_y_controls", true);

        builder.pop();
        builder.push("writing_distance");

        limitSurfaceWritingDistance = builder
                .comment("Apply a separate maximum Map Writing Distance to normal/surface maps.")
                .define("limit_surface", true);

        surfaceWritingDistance = builder
                .comment("Maximum surface writing distance in chunks. Xaero's own smaller limit still wins.")
                .defineInRange("surface_chunks", 12, 0, 32);

        limitCaveWritingDistance = builder
                .comment("Apply a separate maximum Map Writing Distance to cave layers.")
                .define("limit_cave", true);

        caveWritingDistance = builder
                .comment("Maximum cave writing distance in chunks. Xaero's own smaller limit still wins.")
                .defineInRange("cave_chunks", 2, 0, 32);

        builder.pop();
    }

    public static String normalizedTopYMode() {
        return CONFIG.topYMode.get().toUpperCase(Locale.ROOT);
    }

    static {
        Pair<ServerConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(ServerConfig::new);
        CONFIG = pair.getLeft();
        SPEC = pair.getRight();
    }

    private ServerConfig() {
        throw new UnsupportedOperationException();
    }
}
