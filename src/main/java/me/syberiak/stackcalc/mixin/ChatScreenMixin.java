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
        if (keyCode == StackCalc.TAB_KEYCODE) {
            String text = chatField.getText().trim();

            if (text.contains("s") || text.contains("sb") || text.contains("dc")) {
				String call = text.substring(text.lastIndexOf(" ") + 1);
				text = text.substring(0, text.lastIndexOf(" ") + 1);

                String suffix;
                if (call.contains("dc")) { 
                    suffix = "dc";
                } else if (call.contains("sb")) { 
                    suffix = "sb";
                } else if (call.contains("s")) { 
                    suffix = "s";
                } else return;
                
                String limitStr = "";
                if (call.contains("-")) {
                    String afterSuffixStr = call.substring(call.lastIndexOf("-") + 1);
                    if (isNumeric(afterSuffixStr) && afterSuffixStr.length() <= 9) {
                        limitStr = afterSuffixStr;
                    }
                }
                
                String countStr;
                if (limitStr != "") {
                    countStr = call.substring(0, call.length() - (suffix + "-" + limitStr).length());
                } else {
                    countStr = call.substring(0, call.length() - suffix.length());
                    limitStr = "64";
                }

                if (!isNumeric(countStr)) return;
                if (countStr.length() > 9) return;

                int count = Integer.parseInt(countStr);
                int limit = Integer.parseInt(limitStr);

                String result = switch (suffix) {
                    case "s": yield StackCalc.calculateStacks(count, limit);
                    case "sb": yield StackCalc.calculateShulkerBoxes(count, limit);
                    case "dc": yield StackCalc.calculateDoubleChests(count, limit);
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