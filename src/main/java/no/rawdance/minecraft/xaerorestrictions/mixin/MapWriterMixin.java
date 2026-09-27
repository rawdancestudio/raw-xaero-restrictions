package no.rawdance.minecraft.xaerorestrictions.mixin;

import no.rawdance.minecraft.xaerorestrictions.config.ServerConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "xaero.map.MapWriter", remap = false)
public abstract class MapWriterMixin {
    @Shadow(remap = false)
    private int writingLayer;

    @Inject(method = "getWriteDistance", at = @At("RETURN"), cancellable = true, remap = false)
    private void rawXaero$splitWritingDistance(CallbackInfoReturnable<Integer> cir) {
        int original = cir.getReturnValue();
        boolean cave = this.writingLayer != Integer.MAX_VALUE;

        if (cave && ServerConfig.CONFIG.limitCaveWritingDistance.get()) {
            cir.setReturnValue(Math.min(original, ServerConfig.CONFIG.caveWritingDistance.getAsInt()));
        } else if (!cave && ServerConfig.CONFIG.limitSurfaceWritingDistance.get()) {
            cir.setReturnValue(Math.min(original, ServerConfig.CONFIG.surfaceWritingDistance.getAsInt()));
        }
    }
}
