package no.rawdance.minecraft.xaerorestrictions.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import no.rawdance.minecraft.xaerorestrictions.config.ServerConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class AdminCommands {
    private AdminCommands() {}

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("rawxaero")
                        .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                        .executes(AdminCommands::status)
                        .then(Commands.literal("status").executes(AdminCommands::status))
                        .then(Commands.literal("lockCaveType")
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(ctx -> setBoolean(ctx, "lockCaveType", BoolArgumentType.getBool(ctx, "value")))))
                        .then(Commands.literal("caveType")
                                .then(Commands.literal("off").executes(ctx -> setCaveType(ctx, 0)))
                                .then(Commands.literal("layered").executes(ctx -> setCaveType(ctx, 1)))
                                .then(Commands.literal("full").executes(ctx -> setCaveType(ctx, 2))))
                        .then(Commands.literal("lockTopY")
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(ctx -> setBoolean(ctx, "lockTopY", BoolArgumentType.getBool(ctx, "value")))))
                        .then(Commands.literal("topYMode")
                                .then(Commands.literal("auto").executes(ctx -> setTopYMode(ctx, "AUTO")))
                                .then(Commands.literal("playerY").executes(ctx -> setTopYMode(ctx, "PLAYER_Y")))
                                .then(Commands.literal("playerYOffset").executes(ctx -> setTopYMode(ctx, "PLAYER_Y_OFFSET"))))
                        .then(Commands.literal("topYOffset")
                                .then(Commands.argument("value", IntegerArgumentType.integer(-64, 64))
                                        .executes(ctx -> setInt(ctx, "topYOffset", IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(Commands.literal("limitSurface")
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(ctx -> setBoolean(ctx, "limitSurface", BoolArgumentType.getBool(ctx, "value")))))
                        .then(Commands.literal("surfaceDistance")
                                .then(Commands.argument("chunks", IntegerArgumentType.integer(0, 32))
                                        .executes(ctx -> setInt(ctx, "surfaceDistance", IntegerArgumentType.getInteger(ctx, "chunks")))))
                        .then(Commands.literal("limitCave")
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(ctx -> setBoolean(ctx, "limitCave", BoolArgumentType.getBool(ctx, "value")))))
                        .then(Commands.literal("caveDistance")
                                .then(Commands.argument("chunks", IntegerArgumentType.integer(0, 32))
                                        .executes(ctx -> setInt(ctx, "caveDistance", IntegerArgumentType.getInteger(ctx, "chunks")))))
        );
    }

    private static int status(CommandContext<CommandSourceStack> ctx) {
        var c = ServerConfig.CONFIG;
        send(ctx, "RAW Xaero Restrictions:");
        send(ctx, " lockCaveType=" + c.lockCaveModeType.get() + ", caveType=" + caveTypeName(c.forcedCaveModeType.getAsInt()));
        send(ctx, " lockTopY=" + c.lockTopY.get() + ", topYMode=" + ServerConfig.normalizedTopYMode() + ", offset=" + c.topYOffset.getAsInt());
        send(ctx, " surfaceLimit=" + c.limitSurfaceWritingDistance.get() + ", surfaceDistance=" + c.surfaceWritingDistance.getAsInt());
        send(ctx, " caveLimit=" + c.limitCaveWritingDistance.get() + ", caveDistance=" + c.caveWritingDistance.getAsInt());
        return 1;
    }

    private static int setBoolean(CommandContext<CommandSourceStack> ctx, String key, boolean value) {
        switch (key) {
            case "lockCaveType" -> ServerConfig.CONFIG.lockCaveModeType.set(value);
            case "lockTopY" -> ServerConfig.CONFIG.lockTopY.set(value);
            case "limitSurface" -> ServerConfig.CONFIG.limitSurfaceWritingDistance.set(value);
            case "limitCave" -> ServerConfig.CONFIG.limitCaveWritingDistance.set(value);
            default -> throw new IllegalArgumentException(key);
        }
        return saved(ctx, key + " = " + value);
    }

    private static int setInt(CommandContext<CommandSourceStack> ctx, String key, int value) {
        switch (key) {
            case "topYOffset" -> ServerConfig.CONFIG.topYOffset.set(value);
            case "surfaceDistance" -> ServerConfig.CONFIG.surfaceWritingDistance.set(value);
            case "caveDistance" -> ServerConfig.CONFIG.caveWritingDistance.set(value);
            default -> throw new IllegalArgumentException(key);
        }
        return saved(ctx, key + " = " + value);
    }

    private static int setCaveType(CommandContext<CommandSourceStack> ctx, int type) {
        ServerConfig.CONFIG.forcedCaveModeType.set(type);
        return saved(ctx, "caveType = " + caveTypeName(type));
    }

    private static int setTopYMode(CommandContext<CommandSourceStack> ctx, String mode) {
        ServerConfig.CONFIG.topYMode.set(mode);
        return saved(ctx, "topYMode = " + mode);
    }

    private static int saved(CommandContext<CommandSourceStack> ctx, String text) {
        ServerConfig.SPEC.save();
        send(ctx, text + " (lagret)");
        send(ctx, "Tilkoblede spillere bør koble til på nytt for å få den oppdaterte SERVER-configen.");
        return 1;
    }

    private static void send(CommandContext<CommandSourceStack> ctx, String text) {
        ctx.getSource().sendSuccess(() -> Component.literal(text), false);
    }

    private static String caveTypeName(int type) {
        return switch (type) {
            case 0 -> "OFF";
            case 2 -> "FULL";
            default -> "LAYERED";
        };
    }
}
