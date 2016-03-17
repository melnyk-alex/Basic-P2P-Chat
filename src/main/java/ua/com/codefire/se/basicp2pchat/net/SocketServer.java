package ua.com.codefire.se.basicp2pchat.net;



import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple class based on {@link java.net.ServerSocket}.
 *
 * May be used for chat etc.
 *
 * @author CodeFireUA
 */
public abstract class SocketServer implements Runnable {

    public static final int DEFAULT_PORT = 8008;
    public static final int DEFAULT_TIMEOUT = 1000;

    private ServerState state = ServerState.STOPPED;
    private int port;
    private int timeout;
    private ServerSocket serverSocket;

    /**
     * Create Server object with port by {@link #DEFAULT_PORT} and timeout by
     * {@link #DEFAULT_TIMEOUT}.
     */
    public SocketServer() {
        this(DEFAULT_PORT);
    }

    /**
     * Create Server object with custom port and timeout by
     * {@link #DEFAULT_TIMEOUT}.
     *
     * @param port custom port 0-65535.
     */
    public SocketServer(int port) {
        this(port, DEFAULT_TIMEOUT);
    }

    /**
     * Create Server object with custom port and timeout.
     *
     * @param port custom port 0-65535.
     * @param timeout custom timeout from 1 millisecond.
     */
    public SocketServer(int port, int timeout) {
        this.port = port;
        this.timeout = timeout;
    }

    public ServerState getState() {
        return state;
    }

    /**
     * Setup server state.
     *
     * Don't call this method for stop or start server.
     *
     * @param state
     */
    protected void setState(ServerState state) {
        this.state = state;
    }

    public int getPort() {
        return port;
    }

    /**
     * Setup listen port.
     *
     * @param port
     * @throws InvalidStateServerException
     */
    public void setPort(int port) throws InvalidStateServerException {
        switch (getState()) {
            case STOPPED:
                setPort(port);
                break;
        }

        throw new InvalidStateServerException("Server must be stopped before change port number.");
    }

    public int getTimeout() {
        return timeout;
    }

    /**
     * Setup socket timeout.
     *
     * @param timeout
     * @throws InvalidStateServerException
     */
    public void setTimeout(int timeout) throws InvalidStateServerException {
        switch (getState()) {
            case STOPPED:
                setTimeout(timeout);
                break;
        }

        throw new InvalidStateServerException("Server must be stopped before change timeout.");
    }

    /**
     * Make new thread if {@link #state} is STOPPED.
     *
     * @return true if started otherwise false.
     * @throws IOException
     * @throws IllegalThreadStateException
     * @see java.net.ServerSocket
     * @see java.lang.Thread
     */
    public synchronized boolean listen() throws IllegalThreadStateException, IOException {
        switch (getState()) {
            case STOPPED:
                serverSocket = new ServerSocket(port);
                serverSocket.setSoTimeout(timeout);

                new Thread(this).start();
                return true;
        }

        return false;
    }

    /**
     * Stopping server listening port.
     *
     * @return true if stopped otherwise false.
     * @throws IOException
     */
    public synchronized boolean stop() throws IOException {
        switch (getState()) {
            case RUNNING:
                setState(ServerState.STOPPING);
                serverSocket.close();
                serverSocket = null;
                return true;
        }

        return false;
    }

    @Override
    public void run() {
        setState(ServerState.RUNNING);

        try {
            process();
        } catch (Exception ex) {
            Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (state != ServerState.STOPPING) {
                try {
                    stop();
                } catch (IOException ex) {
                    Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * Main server process method.
     */
    protected void process() throws ServerException {
        while (getState() == ServerState.RUNNING) {
            try {
                Socket socket = serverSocket.accept();
                incomingSocket(socket);
            } catch (SocketTimeoutException ex) {
                ;
            } catch (SocketException ex) {
                throw new ServerException("Stopping exception", ex);
            } catch (IOException ex) {
                Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Invokes when socket accepted.
     *
     * @param socket accepted socket.
     */
    protected abstract void incomingSocket(Socket socket) throws IOException;

    /**
     * Describes server states.
     */
    public enum ServerState {
        STOPPED, RUNNING, STOPPING;
    }

    public class ServerException extends RuntimeException {

        public ServerException(String message) {
            super(message);
        }

        public ServerException(String message, Throwable cause) {
            super(message, cause);
        }

    }

    public class InvalidStateServerException extends ServerException {

        public InvalidStateServerException(String message) {
            super(message);
        }

    }

}
