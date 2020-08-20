package uk.co.hopperelec.mc.hopperparty;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class HopperParty extends JavaPlugin implements Iterable<Party> {
    public String pre;
    public final Map<String,String> mes = new HashMap<>();
    public final List<Party> parties = new ArrayList<>();

    @Override
    public Iterator<Party> iterator() {
        return parties.iterator();
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();

        pre = config.getString("messagePrefix");
        if (pre == null) {
            System.out.println("Invalid config.yml! Failed to read prefix. Cancelling plugin start-up!");
            setEnabled(false); return;}
        if (config.getBoolean("includeSpace")) pre += " ";

        final ConfigurationSection configMes = config.getConfigurationSection("messages");
        if (configMes == null) {
            System.out.println("Invalid config.yml! Failed to read messages. Cancelling plugin start-up!");
            setEnabled(false); return;}
        for (String configMesKey : configMes.getKeys(false)) mes.put(configMesKey,configMes.getString(configMesKey));

        getServer().getPluginManager().registerEvents(new Events(this), this);
    }

    public boolean onCommand(CommandSender commandSender, Command cmd, String label, String[] args) {
        if (commandSender instanceof Player) {
            Player author = (Player) commandSender;

            if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                author.sendMessage("§0§l-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
                author.sendMessage("§e§l/party§r§4- §eShows this help message");
                author.sendMessage("§e§l/party help §r§4- §eShows this message");
                author.sendMessage("§e§l/party join (inviter)§r§4- §eIf you have been invited to a party by someone this command will accept it");
                author.sendMessage("§e§l/party invite (invitee)§r§4- §eWill send an invite to the player");
                author.sendMessage("§e§l/party uninvite (invitee)§r§4- §eRemoves an invite sent to a player");
                author.sendMessage("§e§l/party leave§r§4- §eLeaves current party and forms a new one");
                author.sendMessage("§e§l/party list§r§4- §eLists players in your current party");
                author.sendMessage("§e§l/party inspect (player)§r§4- §eShows parties they've been invited to, members of their current party and invites sent from their party");
                author.sendMessage("§0§l-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");

            } else if (args[0].equalsIgnoreCase("join")) {
                if (args.length >= 2) {
                    final Player inviter = Bukkit.getPlayerExact(args[1]);
                    if (inviter != null) {
                        if (getPartyFromPlayer(inviter).invitees.contains(author)) {
                            author.sendMessage(pre+mes.get("joinSuccessfulJoinerMessage"));
                            getPartyFromPlayer(inviter).join(author);
                        } else if (inviter == author) author.sendMessage(pre+mes.get("joinSelfMessage"));
                        else author.sendMessage(pre+mes.get("joinNoInvite"));
                    } else author.sendMessage(pre+mes.get("joinCantFindPlayer").replace("$username",args[1]));
                } else author.sendMessage(pre+"Format: /party join (inviter)");

            } else if (args[0].equalsIgnoreCase("invite")) {
                if (author.hasPermission("hopperparty.invite")) {
                    if (args.length >= 2) {
                        final Player invitee = Bukkit.getPlayerExact(args[1]);
                        if (invitee != null) getPartyFromPlayer(author).invite(author, invitee);
                        else author.sendMessage(pre+mes.get("inviteCantFindPlayer").replace("$username", args[1]));
                    } else author.sendMessage(pre+"Format: /party invite (invitee)");
                } else author.sendMessage(pre+mes.get("invalidPermission"));

            } else if (args[0].equalsIgnoreCase("uninvite")) {
                if (author.hasPermission("hopperparty.invite")) {
                    if (args.length >= 2) {
                        final Player invitee = Bukkit.getPlayerExact(args[1]);
                        if (invitee != null) getPartyFromPlayer(author).uninvite(author,invitee);
                        else author.sendMessage(pre+mes.get("uninviteCantFindPlayer").replace("$username",args[1]));
                    } else author.sendMessage(pre+"Format: /party uninvite (invitee)");
                } else author.sendMessage(pre+mes.get("invalidPermission"));

            } else if (args[0].equalsIgnoreCase("leave")) {
                if (getPartyFromPlayer(author).size() != 1) {
                    author.sendMessage(pre+mes.get("quitSuccessfulQuitterMessage"));
                    getPartyFromPlayer(author).quit(author);
                    parties.add(new Party(this,author));
                } else author.sendMessage(pre+mes.get("quitAlone"));

            } else if (args[0].equalsIgnoreCase("list")) {
                author.sendMessage(pre+"Players in your current party: "+getPartyFromPlayer(author));

            } else if (args[0].equalsIgnoreCase("inspect")) {
                if (author.hasPermission("hopperparty.inspect")) {
                    if (args.length >= 2) {
                        final Player player = Bukkit.getPlayerExact(args[1]);
                        if (player != null) {
                            final Party party = getPartyFromPlayer(player);

                            final StringBuilder invitedParties = new StringBuilder();
                            for (Party invitedParty : getPartiesFromInvitee(player))
                                invitedParties.append(", ").append(invitedParty.players.get(0));
                            author.sendMessage(pre+"Player invited to party of: "+invitedParties);

                            author.sendMessage(pre+"Party members: "+party);

                            if (party.invitees.size() != 0) {
                                final StringBuilder invitees = new StringBuilder(party.invitees.get(0).getDisplayName());
                                for (Player invitee : party.invitees) {
                                    if (invitee != party.invitees.get(0)) invitees.append(", ").append(invitee.getDisplayName());}
                                author.sendMessage(pre+"Party invitees: "+invitees);
                            } else author.sendMessage(pre+"Party invitees: None");

                        } else author.sendMessage(pre+mes.get("inspectCantFindPlayer").replace("$username",args[1]));
                    } else author.sendMessage(pre+"Format: /party inspect (player)");
                } else author.sendMessage(pre+mes.get("invalidPermission"));

            } else {author.sendMessage("Unknown subcommand '"+args[0]+"'!"); return false;}
        } else {System.out.println("Party commands cannot be executed from console!"); return false;}
        return true;
    }

    public Party getPartyFromPlayer(Player player) {
        for (Party party : parties) {
            if (party.contains(player)) return party;}
        return null;
    }

    public List<Party> getPartiesFromInvitee(Player invitee) {
        List<Party> invitedParties = new ArrayList<>();
        for (Party party : parties) {
            if (party.invitees.contains(invitee)) invitedParties.add(party);}
        return invitedParties;
    }
}
