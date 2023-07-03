package me.syberiak.stackcalc;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.text.Text;


public class StackCalc implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("StackCalc");
	public static final String SEPARATOR = " + ";
    public static final int TAB_KEYCODE = 258;

    @Override
    public void onInitializeClient() { LOGGER.info("Initialized successfully."); }

    public static String calculateShulkerBoxes(int count, int stackLimit) { 
        return calculateStorages(count, 27, stackLimit, "stackcalc.shulkerbox", "stackcalc.shulkerboxes");
    }

    public static String calculateDoubleChests(int count, int stackLimit) { 
        return calculateStorages(count, 54, stackLimit, "stackcalc.doublechest", "stackcalc.doublechests");
    }

    /*
     * Calculates groups (used for stacks)
     */
    public static String calculateStacks(int count, int stackLimit) {
        if (count == 0 || stackLimit == 0) return "0";

        int stacks = count / stackLimit;
        int items = count % stackLimit;
        ArrayList<String> result = new ArrayList<String>();

        if (stacks > 0) {
            String stackStringKey = (stacks == 1) ? "stackcalc.stack" : "stackcalc.stacks";
            String stackString = Text.translatable(stackStringKey, stacks).getString();
            if (stackLimit != 64) stackString += String.format("(%d)", stackLimit);
            result.add(stackString);
        }
        if (items > 0) {
            String itemStringKey = (items == 1) ? "stackcalc.item" : "stackcalc.items";
            result.add(Text.translatable(itemStringKey, items).getString());
        }

        return String.join(SEPARATOR, result);
    }

    /*
     * Calculates storages (used for shulker boxes and double chests)
     */
    public static String calculateStorages(int count, int slotsCount, int stackLimit, String stringKey1, String stringKey2) {
        if (count == 0 || slotsCount == 0 || stackLimit == 0) return "0";

        int storages = count / (stackLimit * slotsCount);
        int remainder = count % (stackLimit * slotsCount);
        ArrayList<String> result = new ArrayList<String>();

        if (storages > 0) {
            String storageStringKey = (storages == 1) ? stringKey1 : stringKey2;
            String storageString = Text.translatable(storageStringKey, storages).getString();
            if (stackLimit != 64) storageString += String.format("(%d)", stackLimit);
            result.add(storageString);
        }
        if (remainder > 0) result.add(StackCalc.calculateStacks(remainder, stackLimit));

        return String.join(SEPARATOR, result);
    }
}