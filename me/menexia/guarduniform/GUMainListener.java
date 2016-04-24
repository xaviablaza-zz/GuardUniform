package me.menexia.guarduniform;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class GUMainListener implements Listener {
	private final GuardUniform plugin;
	public GUMainListener(GuardUniform plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onGuardJoin(final PlayerJoinEvent event) {
		Player p = event.getPlayer();
		if (GuardUniform.isWarden(p)) return;
		GUArmorManager.checkAndSetUniform(plugin, p);
	}
	
	@EventHandler
	public void onGuardRespawn(final PlayerRespawnEvent event) {
		Player p = event.getPlayer();
		
		if (GuardUniform.isWarden(p)) {
			return;
		}
		
		PlayerInventory inv = p.getInventory();
		
		if (p.hasPermission("uniform.alphaguard")) {
			GUArmorManager.setAlphaGuardArmor(plugin, inv);
		} else if (p.hasPermission("uniform.guard")) {
			GUArmorManager.setGuardArmor(plugin, inv);
		} else if (p.hasPermission("uniform.trainee")) {
			GUArmorManager.setTraineeArmor(plugin, inv);
		}
		
	}
	
	@EventHandler
	public void onPlayerDeath(final PlayerDeathEvent event) {
		// Drop no chain armor and health potions if has permission
		Player p = event.getEntity();
		if (p.hasPermission("uniform.alphaguard")
				|| p.hasPermission("uniform.guard")
				|| p.hasPermission("uniform.trainee")) {
			GUArmorManager.noDropChainAndHealthPots(event);
			GUArmorManager.dropIronArmor(event);
		} else { // if not, just don't drop the chain.
			GUArmorManager.noDropChain(event);
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInventoryClick(final InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		/*
		 * A Personal Anecdote:
		 * 
		 * Bukkit Bug:
		 * InventoryClickEvent is fired TWICE when opening the inventory
		 * in creative mode for the first time upon entering a server
		 * 
		 * Try un-commenting line 75. (v1.7)
		 */
//		p.sendMessage("ClickEvent fired!");
		
		
//		InventoryView view = event.getView();
//		Inventory ak = view.getTopInventory();
//		InventoryType type = view.getType();
//		p.sendMessage("" + event.getRawSlot() + " and " +  ak.getName() + "/" + ak.getTitle() + ": " + ak.getSize());
//		p.sendMessage(type.getDefaultTitle() + " " + type.name() + " " + type.toString());
		/*
		 * Result if Player Inventory:
		 * 18 and container.crafting/container.crafting: 5 (which is 4 crafting slots and 1 result slot, 0, 1, 2, 3, 4)
		 * Crafting CRAFTING CRAFTING
		 */
		
		
		if (GuardUniform.isWarden(p)) return;
		int slot = event.getSlot();
		ItemStack item = event.getCurrentItem();
		
		try {
			if (p.hasPermission("uniform.alphaguard")) {
				if (GUArmorManager.removeCheckAlphaGuard(plugin, p, slot, item)) { // is he trying to remove his armour?
					event.setCancelled(true);
				}
				else if (GUPotionManager.isRemovingPotionFromInventory(plugin, event)) { // is he not? then is he removing a potion from his inventory?
					event.setCancelled(true);
					p.updateInventory();
				}
			} else if (p.hasPermission("uniform.guard")) {
				 if (GUArmorManager.removeCheckGuard(plugin, p, slot, item)) {
					 event.setCancelled(true);
				 } else if (GUPotionManager.isRemovingPotionFromInventory(plugin, event)) {
					 event.setCancelled(true);
					 p.updateInventory();
				 }
			} else if (p.hasPermission("uniform.trainee")) {
				if (GUArmorManager.removeCheckTrainee(plugin, p, slot, item)) {
					event.setCancelled(true);
				} else if (GUPotionManager.isRemovingPotionFromInventory(plugin, event)) {
					event.setCancelled(true);
					p.updateInventory();
				}
			} else {
				// is he trying to add chain armour via shift click?
				if (GUArmorManager.addingArmourViaShiftClick(p, event.isShiftClick(), event.getCurrentItem())) {
					event.setCancelled(true);
					p.updateInventory();
				}
				// is he trying to add chain armour via normal click?
				else if (GUArmorManager.addingArmourViaNormalClick(p, slot, event.getCursor())) {
					event.setCancelled(true);
					p.updateInventory();
				}
			}
		} catch (NullPointerException nPE) {
			nPE.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void dropCheck(final PlayerDropItemEvent event) {
		Player p = event.getPlayer();
		if (GuardUniform.isWarden(p)) return;
		ItemStack itemStackDrop = event.getItemDrop().getItemStack();
		if (GUPotionManager.isDroppingHealthPotions(plugin, p, itemStackDrop)) {
			event.setCancelled(true);
			p.updateInventory();
		}
	}

}
