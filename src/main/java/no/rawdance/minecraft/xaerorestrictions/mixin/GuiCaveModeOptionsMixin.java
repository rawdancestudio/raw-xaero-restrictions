package no.rawdance.minecraft.xaerorestrictions.mixin;

import no.rawdance.minecraft.xaerorestrictions.config.ServerConfig;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "xaero.map.gui.GuiCaveModeOptions", remap = false)
public abstract class GuiCaveModeOptionsMixin {
    @Inject(method = "setCaveStart", at = @At("HEAD"), cancellable = true, remap = false)
    private void rawXaero$blockTopYChange(int y, CallbackInfo ci) {
        if (ServerConfig.CONFIG.lockTopY.get()) {
            ci.cancel();
        }
    }

    @Inject(method = "createSlider", at = @At("RETURN"), cancellable = false, remap = false)
    private void rawXaero$disableTopYSlider(CallbackInfoReturnable<AbstractWidget> cir) {
        if (ServerConfig.CONFIG.lockTopY.get() && ServerConfig.CONFIG.disableTopYControls.get()) {
            AbstractWidget widget = cir.getReturnValue();
            if (widget != null) {
                widget.active = false;
            }
        }
    }

    @Inject(method = "createField", at = @At("RETURN"), cancellable = false, remap = false)
    private void rawXaero$disableTopYField(CallbackInfoReturnable<EditBox> cir) {
        if (ServerConfig.CONFIG.lockTopY.get() && ServerConfig.CONFIG.disableTopYControls.get()) {
            EditBox field = cir.getReturnValue();
            if (field != null) {
                field.setEditable(false);
                field.active = false;
            }
        }
    }

    @Inject(method = "onCaveModeTypeButton", at = @At("HEAD"), cancellable = true, remap = false)
    private void rawXaero$blockCaveModeTypeButton(CallbackInfo ci) {
        if (ServerConfig.CONFIG.lockCaveModeType.get()) {
            ci.cancel();
        }
    }
}
