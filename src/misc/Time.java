package misc;

import net.risingworld.api.Server;
import net.risingworld.api.Timer;
import net.risingworld.api.events.EventMethod;
import net.risingworld.api.events.Listener;
import net.risingworld.api.events.player.PlayerCommandEvent;
import net.risingworld.api.objects.Player;
import java.util.ArrayList;
import static net.risingworld.api.utils.Utils.StringUtils.isNumeric;

public class Time implements Listener{
   Misc misc;
   Timer timer;
   Timer cooldownTimer;
   Boolean voteOngoing = false;
   Boolean isCooldown = false;
   int hour;
   int minute;
   int yes = 0;
   int no = 0;
   int timerTick;
   int cooldownTick;
   ArrayList<String> votedList;
   String initPlayerId;

    public Time(Misc misc) {
        this.misc = misc;
        misc.registerEventListener(this);
        votedList = new ArrayList<>();
        timerTick = Misc.getTimerSecond();
        cooldownTick = Misc.getCooldownTime();
    }

    @EventMethod
    public void command(PlayerCommandEvent event){
        String[] cmd = event.getCommand().split(" ");
        Player player = event.getPlayer();
        if(isCooldown){
            cooldownError(player);
            return;
        }
        if(player.getPermissionGroup().equalsIgnoreCase(Misc.getIgnoreGroup())){
            permissionError(player);
            return;
        }
        if(cmd[0].equalsIgnoreCase(Misc.getTimeCommand())){
            if(cmd.length != 3 || voteOngoing){
                error(player);
                return;
            }else{
                if(isNumeric(cmd[1]) && isNumeric(cmd[2]) && !event.getCommand().contains(".") && !event.getCommand().contains(":")){
                    if(hour>23 || hour <0 || minute <0 || minute >59){
                        error(player);
                        return;
                    }
                    hour = Integer.parseInt(cmd[1]);
                    minute = Integer.parseInt(cmd[2]);
                    yes++;
                    votedList.add(player.getUID());
                    voteOngoing = true;
                    initPlayerId = player.getUID();
                    buildTimer(player.getName());
                }else{
                    error(player);
                }
            }
        }if(cmd[0].equalsIgnoreCase("/Yes") && voteOngoing || cmd[0].equalsIgnoreCase("/No") && voteOngoing){
            if(cmd[0].equalsIgnoreCase("/Yes")){
                if(votedList.contains(player.getUID())){
                    return;
                }else{
                    yesVote(player.getUID());
                }
            }else{
                if(votedList.contains(player.getUID())){
                    return;
                }else{
                    noVote(player.getUID());
                }
            }
        }
    }

    private void yesVote(String playerID){
        yes++;
        votedList.add(playerID);
    }

    private void noVote(String playerID){
        no++;
        votedList.add(playerID);
    }

    private void buildTimer(String playerName){
        for(Player player : Server.getAllPlayers()){
            player.sendTextMessage(playerName + " has requested the time be changed to " + String.valueOf(hour) + ":" + String.valueOf(minute));
            if(!initPlayerId.equalsIgnoreCase(player.getUID())) {
                player.sendTextMessage("Type /Yes or /No to vote!");
            }
        }
        timer = new Timer(1f, 0f, -1, () -> {
            timerTick--;
            if(votedList.size() == Server.getPlayerCount()){
                killTimer();
                return;
            }
            if(timerTick == 0){
                killTimer();
            }
        });
        timer.start();
    }

    public void killTimer(){
        timer.kill();
        if(yes>no || yes == no){
                Server.sendInputCommand("tod "+String.valueOf(hour)+" "+String.valueOf(minute));
        }else{
            for(Player player : Server.getAllPlayers()){
                player.sendTextMessage("The no's won.");
            }
        }
        votedList.clear();
        yes = 0;
        no = 0;
        voteOngoing = false;
        timerTick = Misc.getTimerSecond();
        hour = 0;
        minute = 0;
        startCooldown();
    }

    private void startCooldown(){
        isCooldown = true;
        cooldownTimer = new Timer(1f, 0f, -1, () -> {
            cooldownTick--;
            if(cooldownTick == 0){
                cooldownTimer.kill();
                isCooldown = false;
                cooldownTick = Misc.getCooldownTime();
            }
        });
        cooldownTimer.start();
    }

    private void error(Player player){
        player.sendTextMessage("Invalid command or voting already in progress");
        player.sendTextMessage("Time Command: "+Misc.getTimeCommand()+" hh mm");
    }

    private void cooldownError(Player player){
        player.sendTextMessage("Time change is in cooldown");
        player.sendTextMessage("<Color=yellow>you can vote again in "+String.valueOf((cooldownTick/60)+" Minutes "+String.valueOf(cooldownTick % 60)+" Seconds</Color>"));
    }

    private void permissionError(Player player){
        player.sendTextMessage("<Color=red>You do not have permission to use this command</Color>");
    }
}
