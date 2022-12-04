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

            if (text.endsWith("s") || text.endsWith("sb") || text.endsWith("dc") || 
            text.contains("s-") || text.contains("sb-") || text.contains("dc-")) {
				String call = text.substring(text.lastIndexOf(" ") + 1);
				text = text.substring(0, text.lastIndexOf(" ") + 1);
                String limit = "";

                String suffix;
                if (call.endsWith("s") || call.contains("s-")) { 
                    suffix = "s";
                } else if (call.endsWith("sb") || call.contains("sb-")) { 
                    suffix = "sb";
                } else { 
                    suffix = "dc"; 
                }

                if (call.contains("s-") || call.contains("sb-") || call.contains("dc-")) {
                    String afterSuffixStr = call.substring(call.lastIndexOf("-") + 1);
                    if (isNumeric(afterSuffixStr) && afterSuffixStr.length() <= 9) {
                        limit = afterSuffixStr;
                    }
                }

                String str;
                if (limit != "") {
                    str = call.substring(0, call.length() - (suffix + "-" + limit).length());
                } else {
                    str = call.substring(0, call.length() - suffix.length());
                    limit = "64";
                }

                if (!isNumeric(str)) return;
                if (str.length() > 9) return;

                String result = switch (suffix) {
                    case "s": yield StackCalc.calculateStacks(str, limit);
                    case "sb": yield StackCalc.calculateShulkerBoxes(str, limit);
                    case "dc": yield StackCalc.calculateDoubleChests(str, limit);
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