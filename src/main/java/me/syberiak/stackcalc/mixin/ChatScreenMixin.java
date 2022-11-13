package me.syberiak.stackcalc.mixin;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.syberiak.stackcalc.StackCalc;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {

    @Shadow
    protected TextFieldWidget chatField;

    @Inject(at = @At("HEAD"), method = "keyPressed(III)Z")
    private void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<?> callbackInfo) {
        if (keyCode == 258) {
            String text = chatField.getText();

            if (text.endsWith("s") || text.endsWith("sb") || text.endsWith("dc")) {
				String call = text.substring(text.lastIndexOf(" ") + 1);
				text = text.substring(0, text.lastIndexOf(" ") + 1);
                String suffix;
                if (call.endsWith("s")) { 
                    suffix = "s";
                } else if (call.endsWith("sb")) { 
                    suffix = "sb";
                } else { 
                    suffix = "dc"; 
                }

                String str = call.substring(0, call.length() - suffix.length());
                if (!isNumeric(str)) { return; }
                if (str.length() > 9) { return; }

                String result = switch (suffix) {
                    case "s": yield StackCalc.calculateStacks(str);
                    case "sb": yield StackCalc.calculateShulkers(str);
                    case "dc": yield StackCalc.calculateDoubleChests(str);
                    default: yield "";
                };
                
                chatField.setText(text + result);
                StackCalc.LOGGER.info(String.format("Got call: %s, result: %s", call, result));
            }
        }
    }

    private static boolean isNumeric(String str) {
        return str.matches("^[0-9]+$");
    }
}