package uk.co.hopperelec.mc.party;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Events implements Listener {
    Main main;
    public Events(Main mainClass) {main = mainClass;}

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        main.parties.add(new Party(main,event.getPlayer()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        main.getPartyFromPlayer(event.getPlayer()).quit(event.getPlayer());
        for (Party party : main.parties) party.invitees.remove(event.getPlayer());
    }
}
