package me.syberiak.stackcalc;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.text.Text;


public class StackCalc implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("StackCalc");
	public static final String SEPARATOR = " + ";
    public static final int DEFAULT_STACK_LIMIT = 64;

    @Override
    public void onInitializeClient() { LOGGER.info("Initialized successfully."); }
    
    public static Optional<String> processPrompt(String prompt) {
        if (!prompt.contains("s") && !prompt.contains("sb") && !prompt.contains("dc")) {
            return Optional.empty();
        }

        String call = prompt.substring(prompt.lastIndexOf(" ") + 1);
        prompt = prompt.substring(0, prompt.lastIndexOf(" ") + 1);

        String suffix;
        if (call.contains("dc")) {
            suffix = "dc";
        } else if (call.contains("sb")) {
            suffix = "sb";
        } else if (call.contains("s")) {
            suffix = "s";
        } else return Optional.empty();

        String limitStr = "";
        if (call.contains("-")) {
            String afterSuffixStr = call.substring(call.lastIndexOf("-") + 1);
            if (isNumeric(afterSuffixStr) && afterSuffixStr.length() <= 9) {
                limitStr = afterSuffixStr;
            }
        }

        String countStr;
        if (!limitStr.isEmpty()) {
            countStr = call.substring(0, call.length() - (suffix + "-" + limitStr).length());
        } else {
            countStr = call.substring(0, call.length() - suffix.length());
            limitStr = "%d".formatted(DEFAULT_STACK_LIMIT);
        }

        if (!isNumeric(countStr)) return Optional.empty();
        if (countStr.length() > 9) return Optional.empty();

        int count = Integer.parseInt(countStr);
        int limit = Integer.parseInt(limitStr);

        String result = switch (suffix) {
            case "s": yield StackCalc.calculateStacks(count, limit);
            case "sb": yield StackCalc.calculateShulkerBoxes(count, limit);
            case "dc": yield StackCalc.calculateDoubleChests(count, limit);
            default: yield "";
        };

        StackCalc.LOGGER.info("Got call: {}, result: {}", call, result);
        return (prompt + result).describeConstable();
    }

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
        ArrayList<String> result = new ArrayList<>();

        if (stacks > 0) {
            String stackStringKey = (stacks == 1) ? "stackcalc.stack" : "stackcalc.stacks";
            String stackString = Text.translatable(stackStringKey, stacks).getString();
            if (stackLimit != DEFAULT_STACK_LIMIT) stackString += "(%d)".formatted(stackLimit);
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
        List<String> result = new ArrayList<>();

        if (storages > 0) {
            String storageStringKey = (storages == 1) ? stringKey1 : stringKey2;
            String storageString = Text.translatable(storageStringKey, storages).toString();
            if (stackLimit != DEFAULT_STACK_LIMIT) storageString += "(%d)".formatted(stackLimit);
            result.add(storageString);
        }
        if (remainder > 0) result.add(StackCalc.calculateStacks(remainder, stackLimit));

        return String.join(SEPARATOR, result);
    }

    private static boolean isNumeric(String str) {
        return str.matches("^[0-9]+$");
    }
}