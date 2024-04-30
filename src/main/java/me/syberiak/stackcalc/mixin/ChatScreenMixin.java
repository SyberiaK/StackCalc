package me.syberiak.stackcalc.mixin;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.syberiak.stackcalc.StackCalc;

import java.util.NoSuchElementException;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {

    @Unique
    private static final int TAB_KEYCODE = 258;

    @Shadow
    protected TextFieldWidget chatField;

    @Inject(at = @At("HEAD"), method = "keyPressed(III)Z")
    private void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<?> callbackInfo) {
        if (keyCode != TAB_KEYCODE) {
            return;
        }

        String text = chatField.getText().trim();

        if (!text.contains("s") && !text.contains("sb") && !text.contains("dc")) return;

        try {
            String result = StackCalc.processPrompt(text).orElseThrow();
            chatField.setText(result);
        } catch (NoSuchElementException ignored) {}
    }
}