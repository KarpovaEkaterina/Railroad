package ru.tsystems.karpova.connector;

import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Server {

    private static Logger log = Logger.getLogger(Server.class);

    public static void main(String[] args) {
        try {
            Properties properties = new Properties();
            properties.load(Server.class.getClassLoader().getResourceAsStream("server.properties"));

            Integer port = Integer.valueOf(properties.getProperty("server.port", "1234"));
            ServerSocket socket = new ServerSocket(port);
            Socket connectionSocket;
            Executor handlerExecutor = new ThreadPoolExecutor(10, 20, 30,
                    TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
            while ((connectionSocket = socket.accept()) != null) {
                handlerExecutor.execute(new RequestHandler(connectionSocket));
            }
        } catch (IOException e) {
            log.error("Server startup error", e);
        }
    }

}
