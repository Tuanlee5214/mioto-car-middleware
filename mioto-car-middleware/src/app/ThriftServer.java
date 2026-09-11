/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app;
import org.apache.log4j.Logger;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import thrift.MiotoCarService;
import thrift.handler.CarServiceHandler;
import util.Config;


/**
 *
 * @author tuanlee
 */
public class ThriftServer {
      private static final Logger _Logger = Logger.getLogger(ThriftServer.class);

    private final int _port;
    private final int _numThread;
    private TServer _server;
    private Thread  _thread;

    public ThriftServer() {
        _port      = Config.getInt(ThriftServer.class, "thrift", "port", 10100);
        _numThread = Config.getInt(ThriftServer.class, "thrift", "num-thread", 4);
    }

    public int start() {
        try {
            MiotoCarService.Iface handler = new CarServiceHandler();
            MiotoCarService.Processor<MiotoCarService.Iface> processor =
                    new MiotoCarService.Processor<MiotoCarService.Iface>(handler);

            TServerTransport transport = new TServerSocket(_port);

            TThreadPoolServer.Args args = new TThreadPoolServer.Args(transport)
                    .processor(processor)
                    .protocolFactory(new TBinaryProtocol.Factory())
                    .minWorkerThreads(2)
                    .maxWorkerThreads(_numThread);

            _server = new TThreadPoolServer(args);

            // serve() blocks, so it gets its own thread
            _thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    _Logger.info("CarService listening on port " + _port
                            + " (workers=" + _numThread + ")");
                    _server.serve();
                }
            }, "thrift-server");
            _thread.start();
            return 0;
        } catch (Exception ex) {
            _Logger.error("cannot start thrift server on port " + _port, ex);
            return -1;
        }
    }

    public void stop() {
        if (_server != null) {
            _server.stop();
            _Logger.info("CarService stopped");
        }
    }
}
