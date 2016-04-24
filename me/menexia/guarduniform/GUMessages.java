package me.menexia.guarduniform;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GUMessages {
	
	public static void sendPSA(Player p) {
		p.sendMessage(ChatColor.GOLD + "Bains Penitentiary now requires all "
				+ ChatColor.AQUA + "Trainees, " + ChatColor.DARK_BLUE + "Guards, " + ChatColor.GOLD + "and "
				+ ChatColor.DARK_AQUA + "Alpha" + ChatColor.DARK_BLUE + "Guards " + ChatColor.GOLD +
				"to wear chainmail armor at all times.");
		p.sendMessage(ChatColor.RED + "Unequip your current armor and relog to receive your uniform.");
	}

}
