package me.gabytm.placeholderapi.armor;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.HashMap;
import java.util.Map;

public class ArmorExpansion extends PlaceholderExpansion {
    private final Map<ArmorSlot, ItemStack> armor = new HashMap<>(4);

    @Override
    public String getIdentifier() {
        return "armor";
    }

    @Override
    public String getAuthor() {
        return "GabyTM";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        Player p = (Player) player;
        loadItems(p.getInventory());

        if (!params.contains("_")) {
            return null;
        }

        final String[] args = params.split("_");

        if (args.length < 2) {
            return null;
        }

        ArmorSlot slot;

        switch (args[0].toLowerCase()) {
            case "amount": {
                slot = ArmorSlot.getSlot(args[1]);
                return slot == ArmorSlot.UNKNOWN ? null : getAmount(armor.get(slot));
            }

            case "color": {
                if (args.length < 3) {
                    return null;
                }

                slot = ArmorSlot.getSlot(args[2]);
                final ColorType colorType = ColorType.getType(args[1]);

                return colorType == ColorType.UNKNOWN || slot == ArmorSlot.UNKNOWN ? null : getColor(armor.get(slot), colorType);
            }

            case "durability": {
                if (args.length < 3) {
                    return null;
                }

                final DurabilityType type = DurabilityType.getType(args[1]);
                slot = ArmorSlot.getSlot(args[2]);

                return type == DurabilityType.UNKNOWN || slot == ArmorSlot.UNKNOWN ? null : getDurability(armor.get(slot), type);
            }

            case "has": {
                slot = ArmorSlot.getSlot(args[1]);
                return slot == ArmorSlot.UNKNOWN ? null : Boolean.toString(!isNull(armor.get(slot)));
            }

            case "material": {
                slot = ArmorSlot.getSlot(args[1]);
                return slot == ArmorSlot.UNKNOWN ? null : getMaterial(armor.get(slot));
            }

            case "maxamount": {
                slot = ArmorSlot.getSlot(args[1]);
                return slot == ArmorSlot.UNKNOWN ? null : getMaxAmount(armor.get(slot));
            }

            default: {
                return null;
            }
        }
    }

    private void loadItems(final PlayerInventory inventory) {
        armor.put(ArmorSlot.HELMET, inventory.getHelmet());
        armor.put(ArmorSlot.CHESTPLATE, inventory.getChestplate());
        armor.put(ArmorSlot.LEGGINGS, inventory.getLeggings());
        armor.put(ArmorSlot.BOOTS, inventory.getBoots());
    }

    private boolean isNull(final ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }

    private String getAmount(final ItemStack item) {
        return isNull(item) ? "0" : Integer.toString(item.getAmount());
    }

    private String getColor(final ItemStack item, final ColorType type) {
        if (isNull(item) || !item.getType().name().startsWith("LEATHER_")) {
            return "";
        }

        final Color color = ((LeatherArmorMeta) item.getItemMeta()).getColor();

        switch (type) {
            case RED: {
                return Integer.toString(color.getRed());
            }

            case GREEN: {
                return Integer.toString(color.getGreen());
            }

            case BLUE: {
                return Integer.toString(color.getBlue());
            }

            case HEX: {
                return String.format("%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
            }
        }

        return "";
    }

    private String getDurability(final ItemStack item, final DurabilityType type) {
        switch (type) {
            case MAX: {
                return getMaxDurability(item);
            }

            case LEFT: {
                return getLeftDurability(item);
            }

            default: {
                return null;
            }
        }
    }

    private String getLeftDurability(final ItemStack item) {
        return isNull(item) ? "0" : Integer.toString(item.getType().getMaxDurability() - item.getDurability());
    }

    private String getMaterial(final ItemStack item) {
        return isNull(item) ? "AIR" : item.getType().name();
    }

    private String getMaxAmount(final ItemStack item) {
        return isNull(item) ? "0" : Integer.toString(item.getMaxStackSize());
    }

    private String getMaxDurability(final ItemStack item) {
        return isNull(item) ? "0" : Integer.toString(item.getType().getMaxDurability());
    }

    private enum ArmorSlot {
        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS,
        UNKNOWN;

        public static ArmorSlot getSlot(final String value) {
            for (ArmorSlot slot : values()) {
                if (slot.name().equalsIgnoreCase(value)) {
                    return slot;
                }
            }

            return UNKNOWN;
        }
    }

    private enum ColorType {
        RED,
        GREEN,
        BLUE,
        HEX,
        UNKNOWN;

        public static ColorType getType(final String value) {
            for (ColorType type : values()) {
                if (type.name().equalsIgnoreCase(value)) {
                    return type;
                }
            }

            return UNKNOWN;
        }
    }

    private enum DurabilityType {
        LEFT,
        MAX,
        UNKNOWN;

        public static DurabilityType getType(final String value) {
            for (DurabilityType type : values()) {
                if (type.name().equalsIgnoreCase(value)) {
                    return type;
                }
            }

            return UNKNOWN;
        }
    }
}
