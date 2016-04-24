package me.menexia.guarduniform;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/*
[1:47:17 PM] Will: Guards will always be wearing chain armor with a certain enchantment
[1:47:26 PM] Will: depending on their rank
[1:47:29 PM] Will: trainee-guard-alphaguard
[1:47:35 PM] Will: this chain armor cannot be taken off
[1:47:50 PM] Will: and will not drop on death
[1:47:54 PM] Will: or be able to be stored in chests, etc.
DONE

[1:48:39 PM] Will: this armor should override any worn armor
[1:48:59 PM] Will: but would it be possible to take the armor theyre wearing and put it in their inventory?
[1:49:03 PM] Will: when they first become guard
[1:49:08 PM] Will: so they dont lose it
[1:50:04 PM] xavi ablaza: sure, what plugin does the server use for managing ranks?
[1:50:27 PM] Will: PEX
[1:50:36 PM] Will: a simple permission node would suffice
[1:50:41 PM] Will: say, convicted.guard
[1:50:43 PM] Will: or something
DONE

[1:52:08 PM] Will: yea
[1:52:20 PM] Will: so, even though guards will be forced to wear chain armor
[1:52:21 PM] Will: always
[1:52:30 PM] Will: I don't want other players to have it
[1:52:47 PM] Will: so anyone that does not have the guard node
[1:52:51 PM] Will: or is not a guard
[1:52:58 PM] Will: cannot equip chain armor
[1:53:07 PM] Will: or, if its easier, just delete it from the inventories of non-guards
[1:53:16 PM] Will: if they ever get ahold of it somehow
[1:53:32 PM] Will: main goal is to easily distinguish guards from the crowd
the armor can't be taken out in the first place, so non-guards can't have it all together.
DONE, POSSIBLY...

[1:55:35 PM] Will: so, the following should only happen to players with guard permission node
[1:55:49 PM] Will: when they die, they drop a full set of iron armor, as if they were wearing it
[1:55:57 PM] Will: (so there's still an incentive to kill guards)
[1:56:04 PM] Will: however, this armor has random damage to it
percentage like dynafish, 
50/50 chance for bottom half and top half
do a random chancing of the bottom half and the top half.
you can combine helmets together, also.

[1:57:04 PM] Will: ppl with guard permission node
[1:57:17 PM] Will: cannot remove health potions from their inventory
[1:57:21 PM] Will: or drop them on death
[1:57:30 PM] Will: they will get them via /kit, which you dont need to worry about
[1:57:38 PM] Will: but once they get them, they can't get rid of them
[1:57:40 PM] Will: unless they drink them
[1:57:49 PM] Will: or they die (in which case it just disappears)
[1:58:09 PM] Will: sorry if this is complicated. but thats about it :P
[1:58:15 PM] xavi ablaza: also for health splash?
[1:58:31 PM] Will: hmm
[1:58:37 PM] Will: yes
[1:58:42 PM] Will: health splash and drink
[1:58:51 PM] xavi ablaza: alright.

basically, health and splash potions also cannot be removed from a guard's inventory
or can be dropped on death

these pots can be arrangeable though, so probably check if the click range is null, or something

ADDITIONAL SHIZNITS
Changing enchant system, check if the armor was chain perhaps
Chain armor not to drop on death in general
When upgraded, change their armor
Fix chest stuff
 */
/**
 * 
 * @author Xavier Luis Ablaza - MeneXia
 *
 */
public class GuardUniform extends JavaPlugin {
    // Logger for console
    public Logger logger;
    
    // ArrayList of Maps containing Armor Enchantments
    // The Map esentially is a list of Enchantments for a particular armor piece
    public List<Map<Enchantment, Integer>> armorEnchants = new ArrayList<Map<Enchantment, Integer>>();
    
    // List of Healing Potions
    public final static Short[] healPots = { (short) 16341, (short) 16373, (short) 32725,
            (short) 32757 };

    @Override
    public void onDisable() {
        this.logger.info("Version " + this.getDescription().getVersion() + " disabled!");
    }

    @Override
    public void onEnable() {
        // Get logger
        logger = this.getLogger();
        
        // Generate config file
        checkConfig();
        
        // Loads non-volatile enchantments in config.yml into volatile array (optimization)
        loadEnchants();
        
        // Create new main listener and register its events
        new GUMainListener(this);

        // Check if config.yml states that it doesn't want chain armor to break. Ever.
        // Create new damage listener and register its events if yes.
        if (this.getConfig().getBoolean("noBreak", true)) {
            new GUDamageListener(this);
        }

        this.logger.info("Version " + this.getDescription().getVersion() + " enabled!");
    }

    /**
     * Generates config.yml file
     */
    private void checkConfig() {
        String name = "config.yml";
        File actual = new File(getDataFolder(), name);
        if (!actual.exists()) {
            getDataFolder().mkdir();
            InputStream input = getClass().getResourceAsStream("/defaults/config.yml");
            if (input != null) {
                FileOutputStream output = null;

                try {
                    output = new FileOutputStream(actual);
                    byte[] buf = new byte[4096]; // [8192]?
                    int length = 0;
                    while ((length = input.read(buf)) > 0) {
                        output.write(buf, 0, length);
                    }
                    this.logger.info("Default configuration file written: " + name);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (input != null)
                            input.close();
                    } catch (IOException e) {
                    }

                    try {
                        if (output != null)
                            output.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }

    /**
     * Loads enchants of AlphaGuard, Guard, and Trainee from config.yml
     * into the Armor Enchantments ArrayList (volatile)
     * 
     * INDEX IS IMPORTANT!
     * 0 - AlphaGuard Helmet
     * 1 - AlphaGuard Chestplate
     * 2 - AlphaGuard Leggings 
     * 3 - AlphaGuard Boots 
     * 4 - Guard Helmet
     * 5 - Guard Chestplate 
     * 6 - Guard Leggings 
     * 7 - Guard Boots
     * 8 - Trainee Helmet
     * 9 - Trainee Chestplate 
     * 10 - Trainee Leggings 
     * 11 - Trainee Boots
     */
    public void loadEnchants() {
        final String base = "ArmorEnchants.";
        final String[] paths = {
                base + "AlphaGuard.HELM",
                base + "AlphaGuard.LEGGINGS",
                base + "AlphaGuard.CHESTPLATE",
                base + "AlphaGuard.BOOTS",
                base + "Guard.HELM",
                base + "Guard.CHESTPLATE",
                base + "Guard.LEGGINGS",
                base + "Guard.BOOTS",
                base + "Trainee.HELM",
                base + "Trainee.CHESTPLATE",
                base + "Trainee.LEGGINGS",
                base + "Trainee.BOOTS"
        };
        boolean hasErrors = false;
        
        // For every armor piece, get its armor enchants from the config.yml
        for (int i=0; i<12; i++) {
            armorEnchants.add(getArmorEnchantments(paths[i], hasErrors));
        }
        
        // If there is an error in the config.yml, disable the plugin
        if (hasErrors) {
            logger.log(Level.SEVERE, "Version " + this.getDescription().getVersion() + 
                    " disabled due to errors in config.yml (check above)!");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    /**
     * Get the specific enchantments for a specific armor piece
     * @param path to armor piece in config.yml
     * @param hasErrors flag variable
     * @return Map<Enchantment, Integer> of enchants in an armor piece
     */
    private Map<Enchantment, Integer> getArmorEnchantments(String path, boolean hasErrors) {
        Map<Enchantment, Integer> armorEnchants = new HashMap<Enchantment, Integer>();
        for (String enchant : this.getConfig().getStringList(path)) {
            String[] parts = enchant.trim().split(" ");
            String enchantName = parts[0]
            /*
             * .replaceAll("PROJECTILEPROTECTION", "PROTECTION_PROJECTILE")
             * .replaceAll("FIREPROTECTION", "PROTECTION_FIRE")
             * .replaceAll("BLASTPROTECTION", "PROTECTION_EXPLOSIONS")
             * .replaceAll("PROTECTION", "PROTECTION_ENVIRONMENTAL")
             * .replaceAll("FEATHERFALLING", "PROTECTION_FALL")
             * .replaceAll("RESPIRATION", "OXYGEN") .replaceAll("AQUAAFFINITY",
             * "WATER_WORKER")
             */;
            if (enchantName.equalsIgnoreCase("PROJECTILEPROTECTION")) {
                enchantName = "PROTECTION_PROJECTILE";
            } else if (enchantName.equalsIgnoreCase("FIREPROTECTION")) {
                enchantName = "PROTECTION_FIRE";
            } else if (enchantName.equalsIgnoreCase("BLASTPROTECTION")) {
                enchantName = "PROTECTION_EXPLOSIONS";
            } else if (enchantName.equalsIgnoreCase("PROTECTION")) {
                enchantName = "PROTECTION_ENVIRONMENTAL";
            } else if (enchantName.equalsIgnoreCase("FEATHERFALLING")) {
                enchantName = "PROTECTION_FALL";
            } else if (enchantName.equalsIgnoreCase("RESPIRATION")) {
                enchantName = "OXYGEN";
            } else if (enchantName.equalsIgnoreCase("AQUAAFFINITY")) {
                enchantName = "WATER_WORKER";
            } else if (enchantName.equalsIgnoreCase("UNBREAKING")) {
                enchantName = "DURABILITY";
            }
            Enchantment enchantment = Enchantment.getByName(enchantName);
            int level = -1;
            if (enchantment == null) {
                logger.log(Level.SEVERE, "Unrecognized enchantment: " + enchantName + " at config.yml path: " + path);
                hasErrors = true;
            }
            try {
                level = Integer.parseInt(parts[1]);
            } catch (NumberFormatException nfe) {
                logger.log(Level.SEVERE, "Unparseable integer: " + parts[1] + " at config.yml path: " + path);
                hasErrors = true;
            }
            if (level <= 0) {
                logger.log(Level.SEVERE, "Enchantment Level: " + parts[1] +
                        " at config.yml path: " + path + " needs to be greater than 0");
                hasErrors = true;
            }
            armorEnchants.put(enchantment, level);
        }
        return armorEnchants;
    }
    
    /**
     * Gets a map of enchants from Arraylist populated by loadEnchants(
     * @param index
     * @return Map of Enchants
     */
    public Map<Enchantment, Integer> getEnchantments(int index) {
        return this.armorEnchants.get(index);
    }

    /**
     * 
     * @param p Player object
     * @return true if OP or has permission "uniform.warden"
     */
    public static boolean isWarden(Player p) {
        if (p.isOp() || p.hasPermission("uniform.warden")) {
            return true;
        } else {
            return false;
        }
    }

}
