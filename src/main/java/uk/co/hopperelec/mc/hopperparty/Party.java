package uk.co.hopperelec.mc.hopperparty;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Party implements Iterable<Player> {
    private final HopperParty main;
    public List<Player> players = new ArrayList<>();
    public List<Player> invitees = new ArrayList<>();

    public Party(HopperParty mainClass, Player player) {
        main = mainClass; players.add(player);
    }

    @Override
    public Iterator<Player> iterator() {
        return players.iterator();
    }

    public void invite(Player inviter, Player invitee) {
        if (contains(invitee)) inviter.sendMessage(main.pre+ main.mes.get("inviteInviteeAlreadyInParty").replace("$invitee",invitee.getDisplayName()));
        else if (invitees.contains(invitee)) inviter.sendMessage(main.pre+ main.mes.get("inviteInviteeAlreadyInvited").replace("$invitee",invitee.getDisplayName()));
        else {
            invitees.add(invitee);
            invitee.sendMessage(main.pre+ main.mes.get("inviteSuccessfulInviteeMessage").replace("$inviter",inviter.getDisplayName()));
            for (Player player : players) player.sendMessage(main.pre+ main.mes.get("inviteSuccessfulPartyMessage").replace("$inviter",inviter.getDisplayName()).replace("$invitee",invitee.getDisplayName()));}
    }

    public void uninvite(Player uninviter, Player uninvitee) {
        if (contains(uninvitee)) uninviter.sendMessage(main.pre+ main.mes.get("uninviteUninviteeAlreadyInParty").replace("$uninvitee",uninvitee.getDisplayName()));
        else if (!invitees.contains(uninvitee)) uninviter.sendMessage(main.pre+ main.mes.get("uninviteUninviteeNotInvited").replace("$uninvitee",uninviter.getDisplayName()));
        else {
            invitees.remove(uninvitee);
            uninvitee.sendMessage(main.pre+ main.mes.get("uninviteSuccessfulUninviteeMessage").replace("uninviter",uninviter.getDisplayName()));
            for (Player player : players) player.sendMessage(main.pre+ main.mes.get("uninviteSuccessfulPartyMessage").replace("$uninviter",uninviter.getDisplayName()).replace("$uninvitee",uninvitee.getDisplayName()));}
    }

    public boolean contains(Player player) {
        return players.contains(player);
    }

    public Integer size() {
        return players.size();
    }

    public void join(Player joiner) {
        main.getPartyFromPlayer(joiner).quit(joiner);
        for (Player player : players) player.sendMessage(main.pre+ main.mes.get("joinSuccessfulPartyMessage").replace("$joiner",joiner.getDisplayName()));
        players.add(joiner); invitees.remove(joiner);
    }

    public void quit(Player quitter) {
        players.remove(quitter);
        if (size() == 0) main.parties.remove(this);
        else {
            for (Player player : players) player.sendMessage(main.pre+ main.mes.get("quitSuccessfulPartyMessage").replace("$quitter",quitter.getDisplayName()));}
    }
}
