package misc.mounts;

import misc.Misc;
import net.risingworld.api.Server;
import net.risingworld.api.World;
import net.risingworld.api.database.Database;
import net.risingworld.api.objects.Npc;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import static net.risingworld.api.Internals.println;

public class MountsDB {
    private Misc misc;
    private MountProtect mountProtect;
    private static Database mountDB;
    private ArrayList<Long> npcToRemove;

    public MountsDB(Misc misc, MountProtect mountProtect) {
        this.misc = misc;
        npcToRemove = new ArrayList<>();
        this.mountProtect = mountProtect;
        loadDb();
    }

    private void loadDb(){
        println("loading mount Database", 10);
        mountDB = misc.getSQLiteConnection(misc.getPath()+"/mountDB.db");
        mountDB.execute("CREATE TABLE IF NOT EXISTS `MOUNTS` (`NPCID` BIGINT, `OWNERID` VARCHAR(255))");
        loadMounts();
    }

    private void loadMounts(){
        try {
            ResultSet result = mountDB.executeQuery("SELECT * FROM `MOUNTS`");
            while(result.next()) {
                Long npcID = result.getLong("NPCID");
                String ownerID = result.getString("OWNERID");
                Npc npc = World.getNpc(npcID);
                if (npc == null) {
                    npcToRemove.add(npcID);
                } else {
                    new MountController(npcID, ownerID, npc.getName());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if(!npcToRemove.isEmpty()){
            println("Removing "+String.valueOf(npcToRemove.size())+" invalid mounts", 10);
            for(Long npcID : npcToRemove){
                removeMount(npcID);
            }
        }
    }

    public static void saveMount(Long npcID, String ownerID){
        mountDB.execute("INSERT INTO `MOUNTS`(NPCID, OWNERID) VALUES ('"+npcID+"','"+ownerID+"')");
        println("Saved new mount. ID:"+npcID+" Owner: "+ownerID+" "+ Server.getLastKnownPlayerName(ownerID), 10);
    }

    public static void removeMount(Long npcID){
        mountDB.execute("DELETE FROM `MOUNTS` WHERE  `NPCID` = '"+npcID+"'");
        println("Removed npc. ID: "+npcID, 10);
    }


}
