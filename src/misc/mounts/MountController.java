package misc.mounts;

import net.risingworld.api.Server;
import net.risingworld.api.World;
import net.risingworld.api.objects.Npc;
import net.risingworld.api.objects.Player;

public class MountController {

    private Long npcID;
    private String ownerID;
    private String name;
    private Npc npc;

    public MountController(Long npcID, String ownerID, String name) {
        this.npcID = npcID;
        this.ownerID = ownerID;
        this.name = name;
        this.npc = World.getNpc(npcID);
        npc.setAttribute("MountController", this);
        MountProtect.registerMount(npcID, this);
    }

    public MountController(Long npcID, String ownerID) {
        this.npcID = npcID;
        this.ownerID = ownerID;
        this.name = "";
        MountsDB.saveMount(npcID, ownerID);
    }

    public void rename(Player player){
        player.showInputMessageBox("Rename NPC", "Rename this NPC? (MAX Chars: 24)", name, (newName)->{
            if(newName.length()>24){
                String shorted = newName.substring(0, 23);
                npc.setName(shorted);
            }else{
                npc.setName(newName);
                name = newName;
            }
        });
    }

    public Long getID(){
        return npcID;
    }

    public String getName(){
        return name;
    }

    public String getOwnerID(){
        return ownerID;
    }

    public Npc getNpc(){
        return npc;
    }

    public void debug(Player player){
        player.sendTextMessage("Name: "+name);
        player.sendTextMessage("ID: "+npcID);
        player.sendTextMessage("Owner ID: "+ownerID);
        player.sendTextMessage("Owner Name: "+ Server.getLastKnownPlayerName(ownerID));
    }
}
