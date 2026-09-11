
import app.ThriftServer;
import java.io.File;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author tuanlee
 */
public class EServiceDeamon {
    private static final Logger _Logger = Logger.getLogger(EServiceDeamon.class);

    public static void main(String[] args) throws Exception {

        // log4j from conf/<appenv>.log4j.ini
        String env  = System.getProperty("appenv", "development");
        String conf = System.getProperty("conf", "conf");
        PropertyConfigurator.configure(conf + File.separator + env + ".log4j.ini");

        final ThriftServer server = new ThriftServer();
        if (server.start() < 0) {
            System.exit(503);                 // refuse to run half-started
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                server.stop();
            }
        });
    }
}
