package misc;

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

    private static String timeCommand;

    @Override
    public void onEnable() {
        //storageLogger = new StorageLogger();
        //mountProtect = new MountProtect();
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
            println("Vote Command: "+timeCommand, 10);
            println("Vote Time: "+timerSeconds, 10);

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
}
