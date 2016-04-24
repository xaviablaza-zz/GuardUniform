
package me.menexia.guarduniform;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class GUArmorManager {
	private static Random random = new Random(101);
	
	public static void checkAndSetUniform(GuardUniform plugin, Player p) {
		PlayerInventory inv = p.getInventory();
		// Perm checks...
		if (p.hasPermission("uniform.alphaguard")) {
			// Does he have the chain armour with the correct enchants?
			boolean hasCorrectArmor = GUArmorManager.hasAlphaGuardArmor(plugin, inv);
			if (!hasCorrectArmor) { // if he does not have the enchants
				GUArmorManager.moveNonChainArmorToInventory(p);
				GUArmorManager.setAlphaGuardArmor(plugin, inv);
			} else if (recallArmour(plugin)) { // Recalling the armour?
				GUArmorManager.clearEquippedChainArmor(p);
			}
		} else if (p.hasPermission("uniform.guard")) {
			boolean hasCorrectArmor = GUArmorManager.hasGuardArmor(plugin, inv);
			if (!hasCorrectArmor) {
				GUArmorManager.moveNonChainArmorToInventory(p);
				GUArmorManager.setGuardArmor(plugin, inv);
			} else if (recallArmour(plugin)) {
				GUArmorManager.clearEquippedChainArmor(p);
			}
		} else if (p.hasPermission("uniform.trainee")) {
			boolean hasCorrectArmor = GUArmorManager.hasTraineeArmor(plugin, inv);
			if (!hasCorrectArmor) {
				GUArmorManager.moveNonChainArmorToInventory(p);
				GUArmorManager.setTraineeArmor(plugin, inv);
			} else if (recallArmour(plugin)) {
				GUArmorManager.clearEquippedChainArmor(p);
			}
		} else { // not a guard and have chain?
			GUArmorManager.clearEquippedChainArmor(p);
		}
	}
	
	public static boolean hasAlphaGuardArmor(GuardUniform plugin, PlayerInventory inv) {
		return checkEnchArmor(plugin, inv, 0, 1, 2, 3);
	}
	
	public static boolean hasGuardArmor(GuardUniform plugin, PlayerInventory inv) {
		return checkEnchArmor(plugin, inv, 4, 5, 6, 7);
	}
	
	public static boolean hasTraineeArmor(GuardUniform plugin, PlayerInventory inv) {
		return checkEnchArmor(plugin, inv, 8, 9, 10, 11);
	}
	
	private static boolean checkEnchArmor(GuardUniform plugin, PlayerInventory inv, int helmet, int chestplate, int leggings, int boots) {
		if ((inv.getHelmet() != null && inv.getHelmet().getType().equals(Material.CHAINMAIL_HELMET) && inv.getHelmet().getEnchantments().equals(plugin.getEnchantments(helmet)))
				&& (inv.getChestplate() != null && inv.getChestplate().getType().equals(Material.CHAINMAIL_CHESTPLATE) && inv.getChestplate().getEnchantments().equals(plugin.getEnchantments(chestplate)))
				&& (inv.getLeggings() != null && inv.getLeggings().getType().equals(Material.CHAINMAIL_LEGGINGS) &&  inv.getLeggings().getEnchantments().equals(plugin.getEnchantments(leggings)))
				&& (inv.getBoots() != null && inv.getBoots().getType().equals(Material.CHAINMAIL_BOOTS) &&  inv.getBoots().getEnchantments().equals(plugin.getEnchantments(boots)))) {
			return true;
		} else {
			return false;
		}
	}
	
	private static void moveNonChainArmorToInventory(Player p) {
		PlayerInventory inv = p.getInventory();
		if (!GUArmorManager.hasEnoughEmptySlots(inv)) {
			GUMessages.sendPSA(p);
			return;
		}
		
		if (inv.getHelmet() != null && !inv.getHelmet().getType().equals(Material.CHAINMAIL_HELMET)) {
			inv.setItem(inv.firstEmpty(), inv.getHelmet());
		}
				
		if (inv.getChestplate() != null && inv.getChestplate().getType().equals(Material.CHAINMAIL_CHESTPLATE)) {
			inv.setItem(inv.firstEmpty(), inv.getChestplate());
		}
				
		if (inv.getLeggings() != null && !inv.getLeggings().getType().equals(Material.CHAINMAIL_LEGGINGS)) {
			inv.setItem(inv.firstEmpty(), inv.getLeggings());
		}

		if (inv.getBoots() != null && !inv.getBoots().getType().equals(Material.CHAINMAIL_BOOTS)) {
			inv.setItem(inv.firstEmpty(), inv.getBoots());
		}
		
	}
	
	private static boolean hasEnoughEmptySlots(PlayerInventory inv) {
		int count = 0;
		for (int i=0; i<36; i++) {
			if (inv.getItem(i) == null) count++;
		}
		if (count >= 4) return true; else return false;
	}
	
	public static void setAlphaGuardArmor(GuardUniform plugin, PlayerInventory inv) {
		setArmor(plugin, inv, 0, 1, 2, 3);
	}
	
	public static void setGuardArmor(GuardUniform plugin, PlayerInventory inv) {
		setArmor(plugin, inv, 4, 5, 6, 7);
	}
	
	public static void setTraineeArmor(GuardUniform plugin, PlayerInventory inv) {
		setArmor(plugin, inv, 8, 9, 10, 11);
	}
	
	private static void setArmor(GuardUniform plugin, PlayerInventory inv, int helmet, int chestplate, int leggings, int boots) {
		ItemStack helmeti = new ItemStack(Material.CHAINMAIL_HELMET, 1),
				chestplatei = new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1),
				leggingsi = new ItemStack(Material.CHAINMAIL_LEGGINGS, 1),
				bootsi = new ItemStack(Material.CHAINMAIL_BOOTS, 1);
		helmeti.addUnsafeEnchantments(plugin.getEnchantments(helmet));
		chestplatei.addUnsafeEnchantments(plugin.getEnchantments(chestplate));
		leggingsi.addUnsafeEnchantments(plugin.getEnchantments(leggings));
		bootsi.addUnsafeEnchantments(plugin.getEnchantments(boots));
		inv.setHelmet(helmeti);
		inv.setChestplate(chestplatei);
		inv.setLeggings(leggingsi);
		inv.setBoots(bootsi);
	}
	
	private static boolean recallArmour(GuardUniform plugin) {
		return plugin.getConfig().getBoolean("recallArmor", false);
	}
	
	private static void clearEquippedChainArmor(Player p) {
		PlayerInventory inv = p.getInventory();
		ItemStack helm = inv.getHelmet();
		ItemStack chest = inv.getChestplate();
		ItemStack pants = inv.getLeggings();
		ItemStack boots = inv.getBoots();
		if (helm != null && helm.getType().equals(Material.CHAINMAIL_HELMET)) inv.setHelmet(null);
		if (chest != null && chest.getType().equals(Material.CHAINMAIL_CHESTPLATE)) inv.setChestplate(null);
		if (pants != null && pants.getType().equals(Material.CHAINMAIL_LEGGINGS)) inv.setLeggings(null);
		if (boots != null && boots.getType().equals(Material.CHAINMAIL_BOOTS)) inv.setBoots(null);
	}
	
	/*
	 * InventoryClickEvent ARMOUR calls
	 */
	
	public static boolean removeCheckAlphaGuard(GuardUniform plugin, Player p, int slot, ItemStack item) {
		return armorRemoveCheck(plugin, p, slot, item, 0, 1, 2, 3);
	}
	
	public static boolean removeCheckGuard(GuardUniform plugin, Player p, int slot, ItemStack item) {
		return armorRemoveCheck(plugin, p, slot, item, 4, 5, 6, 7);
	}

	public static boolean removeCheckTrainee(GuardUniform plugin, Player p, int slot, ItemStack item) {
		return armorRemoveCheck(plugin, p, slot, item, 8, 9, 10, 11);
	}
	
	private static boolean armorRemoveCheck(GuardUniform plugin, Player p, int slot, ItemStack item, int helmet, int chestplate, int leggings, int boots) {
		if ((slot==39 && item.getType().equals(Material.CHAINMAIL_HELMET) && item.getEnchantments().equals(plugin.getEnchantments(helmet)))
				|| (slot==38 && item.getType().equals(Material.CHAINMAIL_CHESTPLATE) && item.getEnchantments().equals(plugin.getEnchantments(chestplate)))
				|| (slot==37 && item.getType().equals(Material.CHAINMAIL_LEGGINGS) && item.getEnchantments().equals(plugin.getEnchantments(leggings)))
				|| (slot==36 && item.getType().equals(Material.CHAINMAIL_BOOTS) && item.getEnchantments().equals(plugin.getEnchantments(boots)))) {
			p.sendMessage(ChatColor.RED + "You cannot remove your uniform!");
			return true;
		}
		return false;
	}
	
	public static void noDropChainAndHealthPots(PlayerDeathEvent event) {
		List<ItemStack> items = event.getDrops();
		Iterator<ItemStack> it = items.iterator();
		while (it.hasNext()) {
			ItemStack item = it.next();
			if (!removeChain(it, item)) {
				removeInstaHealthPotion(it, item);
			}
		}
	}
	
	private static boolean removeChain(Iterator<ItemStack> it, ItemStack item) {
		if (item.getType().equals(Material.CHAINMAIL_HELMET)
				|| item.getType().equals(Material.CHAINMAIL_CHESTPLATE)
				|| item.getType().equals(Material.CHAINMAIL_LEGGINGS)
				|| item.getType().equals(Material.CHAINMAIL_BOOTS)) {
			it.remove();
			return true;
		}
		return false;
	}
	
	private static void removeInstaHealthPotion(Iterator<ItemStack> it, ItemStack item) {
		if (item.getType().equals(Material.POTION) && Arrays.asList(GuardUniform.healPots).contains(item.getDurability())) {
			it.remove();
		}
	}
	
	public static void dropIronArmor(PlayerDeathEvent event) {
		List<ItemStack> items = event.getDrops();
		ItemStack helmet = new ItemStack(Material.IRON_HELMET),
				chestplate = new ItemStack(Material.IRON_CHESTPLATE),
				leggings = new ItemStack(Material.IRON_LEGGINGS),
				boots = new ItemStack(Material.IRON_BOOTS);
		if (random.nextBoolean()) helmet.setDurability((short)random.nextInt(83));
		else helmet.setDurability((short)(random.nextInt(83)+83));
		if (random.nextBoolean()) chestplate.setDurability((short)random.nextInt(120));
		else chestplate.setDurability((short)(random.nextInt(120)+120));
		if (random.nextBoolean()) leggings.setDurability((short)random.nextInt(113));
		else leggings.setDurability((short)(random.nextInt(113)+113));
		if (random.nextBoolean()) boots.setDurability((short)random.nextInt(98));
		else boots.setDurability((short)(random.nextInt(98)+98));
		
		items.add(helmet);
		items.add(chestplate);
		items.add(leggings);
		items.add(boots);
	}
	
	public static void noDropChain(PlayerDeathEvent event) {
		List<ItemStack> items = event.getDrops();
		Iterator<ItemStack> it = items.iterator();
		while (it.hasNext()) {
			ItemStack item = it.next();
			removeChain(it, item);
		}
	}
	
	public static boolean addingArmourViaShiftClick(Player p, boolean isShiftClick, ItemStack armor) {
		if (armor != null) {
			if (isShiftClick) {
				Material armorMat = armor.getType();
				if (armorMat.equals(Material.CHAINMAIL_HELMET) || armorMat.equals(Material.CHAINMAIL_CHESTPLATE) || armorMat.equals(Material.CHAINMAIL_LEGGINGS) || armorMat.equals(Material.CHAINMAIL_BOOTS)) {
					p.sendMessage(ChatColor.RED + "You cannot equip chain armor!");
					return true;
				}
			}
		}
		return false;
	}

	public static boolean addingArmourViaNormalClick(Player p, int slot, ItemStack cursor) {
		if ((slot==39 && cursor.getType().equals(Material.CHAINMAIL_HELMET))
				|| (slot==38 && cursor.getType().equals(Material.CHAINMAIL_CHESTPLATE))
				|| (slot==37 && cursor.getType().equals(Material.CHAINMAIL_LEGGINGS))
				|| (slot==36 && cursor.getType().equals(Material.CHAINMAIL_BOOTS))) {
			p.sendMessage(ChatColor.RED + "You cannot equip chain armor!");
			return true;
		}
		return false;
	}
	
}
