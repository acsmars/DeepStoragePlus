package me.darkolythe.deepstorageplus.dsu.managers;

import me.darkolythe.deepstorageplus.DeepStoragePlus;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;

public class SorterUpdateManager {

    private DeepStoragePlus main;
    public SorterUpdateManager(DeepStoragePlus plugin) {
        main = plugin;
    }

    /*
    Update the items in the dsu. This is done when items are added, taken, Storage Containers are added, taken, and when opening the dsu.
     */
    public void sortItems(Inventory inv, Long minTimeSinceLast) {
        Location location = inv.getLocation();
        if (location == null) {
            return;
        }
        if (!DeepStoragePlus.recentSortCalls.containsKey(location)) {
            DeepStoragePlus.recentSortCalls.put(location, 0L);
        }
        // Reject calls that are too recent relative to the last sort from this inv.
        if (System.currentTimeMillis() - DeepStoragePlus.recentSortCalls.get(location) < minTimeSinceLast) {
            return;
        }
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, () -> {
            if (!DeepStoragePlus.recentSortCalls.containsKey(location)) {
                return;
            }
            if(SorterManager.sortItems(inv)) {
                DeepStoragePlus.recentSortCalls.put(location, System.currentTimeMillis());
            }
            else { // On a failed sort we know the next run will probably cache miss, so we delay it even further for performance reasons
                DeepStoragePlus.recentSortCalls.put(location, System.currentTimeMillis() + 30000L);
            }
        }, 5L);
    }
}
