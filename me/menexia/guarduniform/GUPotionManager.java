package me.menexia.guarduniform;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;

public class GUPotionManager {
	private final static InventoryType[] shiftClickInvents = {InventoryType.CHEST, InventoryType.ENDER_CHEST, InventoryType.DISPENSER, InventoryType.BREWING};
	
	public static boolean isRemovingPotionFromInventory(GuardUniform plugin, InventoryClickEvent event) {
		/*
		 * inventories that shift click potions into the upper inventory:
		 * chest, enderchest, dispenser, brewing stand
		 */
		Player p = (Player)event.getWhoClicked();
		ItemStack item;
		InventoryType inventoryType;
		// is it or is it not a shift click?
		if (event.isShiftClick()) {
			item = event.getCurrentItem();
			// is the item shift clicked a health potion?
			if (isHealthPotion(item)) {
				// is the inventoryType a chest, enderchest, dispenser or brewing stand?
				// (as they are possible inventories for potions to be shift clicked into)
				inventoryType = event.getView().getType();
				if (Arrays.asList(shiftClickInvents).contains(inventoryType)) {
					p.sendMessage(ChatColor.RED + "You cannot remove health potions from your inventory!");
					return true;
				}
			}
		} else {
			item = event.getCursor();
			// is the item on the cursor a health potion?
			if (isHealthPotion(item)) {
				// was the item not clicked within the quickbar?
				SlotType slotType = event.getSlotType();
				if (!slotType.equals(SlotType.QUICKBAR)) {
					// was the item clicked on the outside?
					inventoryType = event.getView().getType();
					if (slotType.equals(SlotType.OUTSIDE)
							|| ((!inventoryType.equals(InventoryType.PLAYER) || !inventoryType.equals(InventoryType.ANVIL)) && event.getRawSlot() < event.getView().getTopInventory().getSize())) {
						p.sendMessage(ChatColor.RED + "You cannot remove health potions from your inventory!");
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private static boolean isHealthPotion(ItemStack item) {
		if (item != null && item.getType().equals(Material.POTION)) {
			if (Arrays.asList(GuardUniform.healPots).contains(item.getDurability())) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isDroppingHealthPotions(GuardUniform plugin, Player p, ItemStack itemStackDrop) {
		if (p.hasPermission("uniform.alphaguard") || p.hasPermission("uniform.guard") || p.hasPermission("uniform.trainee")) {
			if (isHealthPotion(itemStackDrop)) {
				p.sendMessage(ChatColor.RED + "You cannot remove health potions from your inventory!");
				return true;
			}
		}
		return false;
	}

}
