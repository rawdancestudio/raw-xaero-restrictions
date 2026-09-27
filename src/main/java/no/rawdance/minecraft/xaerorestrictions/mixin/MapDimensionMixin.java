package no.rawdance.minecraft.xaerorestrictions.mixin;

import no.rawdance.minecraft.xaerorestrictions.config.ServerConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "xaero.map.world.MapDimension", remap = false)
public abstract class MapDimensionMixin {
    @Inject(method = "getCaveModeType", at = @At("HEAD"), cancellable = true, remap = false)
    private void rawXaero$forceCaveModeType(CallbackInfoReturnable<Integer> cir) {
        if (ServerConfig.CONFIG.lockCaveModeType.get()) {
            cir.setReturnValue(ServerConfig.CONFIG.forcedCaveModeType.getAsInt());
        }
    }

    @Inject(method = "toggleCaveModeType", at = @At("HEAD"), cancellable = true, remap = false)
    private void rawXaero$blockCaveModeTypeToggle(boolean forward, CallbackInfo ci) {
        if (ServerConfig.CONFIG.lockCaveModeType.get()) {
            ci.cancel();
        }
    }
}
