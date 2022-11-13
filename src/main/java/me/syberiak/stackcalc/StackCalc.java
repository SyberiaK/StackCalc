package me.syberiak.stackcalc;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.text.Text;


public class StackCalc implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("StackCalc");
	public static final String SEPARATOR = " + ";

    @Override
    public void onInitializeClient() { LOGGER.info("Initialized successfully."); }

    public static String calculateStacks(String count) { return calculateStacks(Integer.parseInt(count)); }
    public static String calculateShulkers(String count) { return calculateShulkers(Integer.parseInt(count)); }
    public static String calculateDoubleChests(String count) { return calculateDoubleChests(Integer.parseInt(count)); }

    public static String calculateStacks(int count) { // Calculates stacks
        if (count == 0) { return "0";}

        int stackCount = count / 64;
        int items = count % 64;
        ArrayList<String> result = new ArrayList<String>();

        if (stackCount > 0) {
            String stackStringKey = (stackCount == 1) ? "stackcalc.stack" : "stackcalc.stacks";
            result.add(Text.translatable(stackStringKey, stackCount).getString());
        }
        if (items > 0) {
            String itemStringKey = (items == 1) ? "stackcalc.item" : "stackcalc.items";
            result.add(Text.translatable(itemStringKey, items).getString());
        }

        return String.join(SEPARATOR, result);
    }
     
    public static String calculateShulkers(int count) { // Calculates shulker boxes
        if (count == 0) { return "0";}

        int shulkerCount = count / 1728;
        int remainder = count % 1728;
        ArrayList<String> result = new ArrayList<String>();

        if (shulkerCount > 0) {
            String shulkerStringKey = (shulkerCount == 1) ? "stackcalc.shulkerbox" : "stackcalc.shulkerboxes";
            result.add(Text.translatable(shulkerStringKey, shulkerCount).getString());
        }
        if (remainder > 0) {
            result.add(StackCalc.calculateStacks(remainder));
        }

        return String.join(SEPARATOR, result);
    }

    public static String calculateDoubleChests(int count) { // Calculates double chests
        if (count == 0) { return "0";}

        int doubleCount = count / 3456;
        int remainder = count % 3456;
        ArrayList<String> result = new ArrayList<String>();

        if (doubleCount > 0) {
            String doubleStringKey = (doubleCount == 1) ? "stackcalc.doublechest" : "stackcalc.doublechests";
            result.add(Text.translatable(doubleStringKey, doubleCount).getString());
        }
        if (remainder > 0) {
            result.add(StackCalc.calculateStacks(remainder));
        }

        return String.join(SEPARATOR, result);
    }
}