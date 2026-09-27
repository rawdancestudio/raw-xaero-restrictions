# RAW Xaero Restrictions 0.1.0

Server-side restrictions for Xaero’s World Map mod in Minecraft, including locked cave-map behavior and separate surface/cave map-writing limits. Designed for NeoForge servers that want Xaero’s mapping features without the more cheat-like options.

Target:
- Minecraft 26.2
- NeoForge 26.2.0.88
- Java 25
- Xaero's World Map 1.46.1 NeoForge

## What this first build does

- Forces Cave Mode Type (default: Layered).
- Prevents players from changing Cave Mode Type while locked.
- Locks Cave Mode Top Y.
- Supports Top Y modes:
  - `AUTO`: Xaero's own cave/roof detection (recommended).
  - `PLAYER_Y`: exact player Y.
  - `PLAYER_Y_OFFSET`: player Y + configurable offset.
- Disables the Top Y slider/text field when locked.
- Splits Map Writing Distance into two maximum caps:
  - surface/normal map (default 12 chunks)
  - cave layers (default 2 chunks)
- Uses the lower of Xaero's own Writing Distance and this mod's cap.
- Stores policy as a NeoForge `SERVER` config, which NeoForge syncs to clients when they connect.
- Adds OP/gamemaster-only `/rawxaero` commands.

## Important implementation note

This is deliberately pinned to Xaero World Map **1.46.1**. The mixins target methods we inspected in that exact version. Do not update Xaero before rebuilding/testing this companion mod.

## Build

This project intentionally does not bundle Gradle's wrapper binary. Run `setup-wrapper.ps1` once on Windows, then:

```powershell
.\gradlew.bat build
```

The JAR will be under:

```text
build\libs\raw_xaero_restrictions-0.1.0.jar
```

Install the same JAR in both the server `mods` directory and every client modpack.

## Server config

NeoForge creates the SERVER config in the world's `serverconfig` directory, normally something like:

```text
<server>\<world>\serverconfig\raw-xaero-restrictions.toml
```

Defaults:

```text
[cave_mode]
lock_cave_mode_type = true
forced_cave_mode_type = 1
lock_top_y = true
top_y_mode = "AUTO"
top_y_offset = 4
disable_top_y_controls = true

[writing_distance]
limit_surface = true
surface_chunks = 12
limit_cave = true
cave_chunks = 2
```

## OP-only commands

```text
/rawxaero status
/rawxaero lockCaveType true|false
/rawxaero caveType off|layered|full
/rawxaero lockTopY true|false
/rawxaero topYMode auto|playerY|playerYOffset
/rawxaero topYOffset -64..64
/rawxaero limitSurface true|false
/rawxaero surfaceDistance 0..32
/rawxaero limitCave true|false
/rawxaero caveDistance 0..32
```

The command permission is Minecraft's `COMMANDS_GAMEMASTER`, i.e. normal OP/gamemaster access.

### Current limitation

NeoForge syncs SERVER config during connection. The commands persist immediately on the server, but already connected clients should reconnect after an admin changes policy. Live re-sync is a sensible v0.2 feature after the first build is verified.

## First test checklist

1. Start a test server with Xaero World Map 1.46.1 and this mod.
2. Start a client with the same two mods.
3. `/rawxaero status` as OP.
4. Open World Map -> Cave Mode controls.
5. Cave Mode Type should effectively remain Layered.
6. Top Y slider/text field should be disabled.
7. Move underground and verify `AUTO` follows Xaero's cave detection.
8. Set `/rawxaero caveDistance 2`, reconnect, and verify cave writing is capped much closer than surface writing.
9. Verify normal surface map still writes out to the configured surface cap.

