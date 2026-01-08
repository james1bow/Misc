package misc;

import misc.mounts.MountProtect;
import net.risingworld.api.Plugin;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static net.risingworld.api.Internals.println;

public class Misc extends Plugin {

    StorageLogger storageLogger;
    MountProtect mountProtect;
    Time time;
    Properties properties;
    String propertyPath;
    private static Integer timerSeconds;
    private static Integer cooldownTime;
    private static String timeCommand;
    private static String ignoreGroup;
    private static String mountCommand;

    @Override
    public void onEnable() {
        //storageLogger = new StorageLogger();
        mountProtect = new MountProtect(this);
        propertyPath = getPath() + "/config.properties";
        loadProperties();
    }

    @Override
    public void onDisable() {

    }

    private void loadProperties() {
        properties = new Properties();
        try (InputStream stream = new FileInputStream(propertyPath)) {
            properties.load(stream);
            timeCommand = properties.getProperty("timeCommand", "/Time");
            timerSeconds = Integer.parseInt(properties.getProperty("voteTime", "30"));
            cooldownTime = Integer.parseInt(properties.getProperty("cooldownTime", "600"));
            ignoreGroup = properties.getProperty("ignoreGroup", "NOT SETUP");
            mountCommand = properties.getProperty("mountCommand", "/Mount");
            println("Vote Command: "+timeCommand, 10);
            println("Vote Time: "+timerSeconds, 10);
            println("Cooldown Time: "+cooldownTime, 10);
            println("Ignore Group: "+ignoreGroup, 10);
            println("Mount Command: "+mountCommand, 10);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        time = new Time(this);
    }

    public static String getTimeCommand(){
        return timeCommand;
    }

    public static Integer getTimerSecond(){
        return timerSeconds;
    }

    public static Integer getCooldownTime(){
        return cooldownTime;
    }

    public static String getIgnoreGroup(){
        return ignoreGroup;
    }

    public static String getMountCommand(){
        return mountCommand;
    }
}
