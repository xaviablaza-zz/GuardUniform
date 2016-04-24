package me.menexia.guarduniform;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class GUDamageListener implements Listener {
	public GUDamageListener(GuardUniform plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onDamageOnGuard(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player)event.getEntity();
			if (p.hasPermission("uniform.alphaguard") || p.hasPermission("uniform.guard") || p.hasPermission("uniform.trainee") || p.hasPermission("uniform.warden")) {
				ItemStack[] armor = p.getInventory().getArmorContents();	
				if (armor.length > 0) {
					for (int i = 0; i < armor.length; i++) {
						if (armor[i] != null) armor[i].setDurability((short)(-armor[i].getType().getMaxDurability()));
					}
				}
			}
		}
	}

}
