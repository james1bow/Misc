package misc.mounts;

import misc.Misc;
import net.risingworld.api.World;
import net.risingworld.api.events.EventMethod;
import net.risingworld.api.events.Listener;
import net.risingworld.api.events.npc.*;
import net.risingworld.api.events.player.PlayerCommandEvent;
import net.risingworld.api.events.player.PlayerMountNpcEvent;
import net.risingworld.api.objects.Npc;
import net.risingworld.api.objects.Player;
import net.risingworld.api.utils.Vector3f;

import java.util.HashMap;

import static net.risingworld.api.Internals.println;

public class MountProtect implements Listener {

    private Misc misc;
    private MountsDB mountsDB;
    private static HashMap<Long, MountController> mountControllers;
    private static int mountCount = 0;

    public MountProtect(Misc misc) {
        this.misc = misc;
        mountControllers = new HashMap<>();
        this.mountsDB = new MountsDB(misc, this);
        misc.registerEventListener(this);
    }

    public static void registerMount(Long npcID, MountController mountController){
        mountControllers.put(npcID, mountController);
        mountCount++;
    }

    public static void unregisterMount(Long npcID){
        mountCount--;
        World.getNpc(npcID).deleteAttribute("MountController");
        mountControllers.remove(npcID);
    }

    public static HashMap<Long, MountController> getAllMountControllers(){
        return mountControllers;
    }

    @EventMethod
    public void addSaddle(NpcAddSaddleEvent event){
        Npc npc = event.getNpc();
        long npcID = npc.getGlobalID();
        new MountController(npcID, event.getRelatedPlayer().getUID());
    }

    @EventMethod
    public void removeSaddle(NpcRemoveSaddleEvent event){
        Npc npc = event.getNpc();
        long npcID = npc.getGlobalID();
        Player player = event.getRelatedPlayer();
        if(npc.hasAttribute("MountController")){
            MountController mountController = (MountController) npc.getAttribute("MountController");
            if(!player.isAdmin() && !mountController.getOwnerID().equalsIgnoreCase(player.getUID())){
                event.setCancelled(true);
                player.sendYellMessage("<color=red>this is NOT your mount!", 5.0f, true);
                println("Player is attempting to remove a saddle from a protected npc: "+player.getName()+" ID: "+player.getUID()+" @ coords: "+npc.getPosition().toString()+" Npc ID: "+npcID , 10);
            }else{
                MountProtect.unregisterMount(npcID);
                MountsDB.removeMount(npcID);
            }
        }
    }

    @EventMethod
    public void addSaddleBag(NpcAddSaddleBagEvent event){
        Npc npc = event.getNpc();
        Player player = event.getRelatedPlayer();
        if(npc.hasAttribute("MountController")){
            MountController mountController = (MountController) npc.getAttribute("MountController");
            if(!player.isAdmin() && !mountController.getOwnerID().equalsIgnoreCase(player.getUID())){
                event.setCancelled(true);
                player.sendYellMessage("<color=red>this is NOT your mount!", 5.0f, true);
                println("Player is attempting to add a saddle bag to a protected npc: "+player.getName()+" ID: "+player.getUID()+" @ coords: "+npc.getPosition().toString()+" Npc ID: "+npc.getGlobalID() , 10);
            }
        }
    }

    @EventMethod
    public void removeSaddleBag(NpcRemoveSaddleBagEvent event){
        Npc npc = event.getNpc();
        Player player = event.getRelatedPlayer();
        if(npc.hasAttribute("MountController")){
            MountController mountController = (MountController) npc.getAttribute("MountController");
            if(!player.isAdmin() && !mountController.getOwnerID().equalsIgnoreCase(player.getUID())){
                event.setCancelled(true);
                player.sendYellMessage("<color=red>this is NOT your mount!", 5.0f, true);
                println("Player is attempting to remove a saddlebag from a protected npc: "+player.getName()+" ID: "+player.getUID()+" @ coords: "+npc.getPosition().toString()+" Npc ID: "+npc.getGlobalID() , 10);
            }
        }
    }

    @EventMethod
    public void npcDeath(NpcDeathEvent event){
        Npc npc = event.getNpc();
        if(event.getNpc().hasAttribute("MountController")){
            NpcDeathEvent.Cause cause = event.getCause();
            if(cause.equals(NpcDeathEvent.Cause.KilledByPlayer)){
                Player killer = (Player)event.getKiller();
                Vector3f eventPosition = event.getNpc().getPosition();
                println("Player is attempting to kill a protected npc: "+killer.getName()+" ID: "+killer.getUID()+" @ coords: "+eventPosition.toString()+" Npc ID: "+npc.getGlobalID() , 10);
                event.setCancelled(true);
            }
        }
    }

    @EventMethod
    public void mountNPC(PlayerMountNpcEvent event){
        Npc npc = event.getNpc();
        Player player = event.getPlayer();
        if(npc.hasAttribute("MountController")){
            MountController mountController = (MountController) npc.getAttribute("MountController");
            if(!player.isAdmin() && !mountController.getOwnerID().equalsIgnoreCase(player.getUID())){
                event.setCancelled(true);
                player.sendYellMessage("<color=red>this is NOT your mount!", 5.0f, true);
                println("Player is attempting to mount a protected npc: "+player.getName()+" ID: "+player.getUID()+" @ coords: "+npc.getPosition().toString()+" Npc ID: "+npc.getGlobalID() , 10);
            }
        }
    }

    @EventMethod
    public void command(PlayerCommandEvent event){
        String[] cmd = event.getCommand().split(" ");
        Player player = event.getPlayer();
        if(cmd[0].equalsIgnoreCase(Misc.getMountCommand()) && player.isAdmin()){

            new MountList(player);
        }
        if(cmd[0].equalsIgnoreCase("/Rename")){
            player.getNpcInLineOfSight(10, (npc) -> {
                if(npc != null && npc.hasAttribute("MountController")){
                    MountController mountController = (MountController) npc.getAttribute("MountController");
                    if(player.isAdmin()){
                        mountController.rename(player);
                    }else{
                        if(mountController.getOwnerID().equalsIgnoreCase(player.getUID())){
                            mountController.rename(player);
                        }else{
                            player.sendTextMessage("<color=red> This Is NOT your mount");
                            println("Player is attempting to rename a protected npc: "+player.getName()+" ID: "+player.getUID()+" @ coords: "+npc.getPosition().toString()+" Npc ID: "+npc.getGlobalID() , 10);
                        }
                    }
                }
            });
        }
        if(cmd[0].equalsIgnoreCase("/Debug")){
            player.getNpcInLineOfSight(10, (npc) -> {
                if(npc != null && npc.hasAttribute("MountController")){
                    MountController mountController = (MountController) npc.getAttribute("MountController");
                    mountController.debug(player);
                }
            });
        }
    }
}
