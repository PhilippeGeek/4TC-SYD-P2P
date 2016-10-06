package fr.insa.lyon.tc.p2p;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Map a client to a remote server to request a file.
 */
public class RemoteServer {

    private static final int DEFAULT_PORT = 1236;
    public static final int FILE_TIMEOUT = 500;
    private final String ip;
    private final int port;
    private String range;

    public RemoteServer(String ip) {
        this(ip, DEFAULT_PORT);
    }

    public RemoteServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public boolean readFile(String file, int ttl, OutputStream out){
        if(!hasFile(file)){
            return false;
        }
        try {
            Socket s = new Socket(ip, port);
            s.setSoTimeout(FILE_TIMEOUT);
            System.out.println("Trying server " + ip);

            PrintWriter srv = new PrintWriter(s.getOutputStream(), true);
            srv.println(ttl + "\n" + file);

            int nbytes = Server.copyStream(s.getInputStream(), out, true);
            s.close();
            return nbytes > 0;
        } catch(IOException ignored) { } // ignore
        return false;
    }

    private boolean hasFile(String file) {
        return range==null || file.matches("^["+range+"].*");
    }

    public File getFile(String file, int ttl) {
        if(!hasFile(file)){
            return null;
        }
        try {
            Socket s = new Socket(ip, port);
            PrintWriter srv = new PrintWriter(s.getOutputStream(), true);

            // Ask for requested file, max depth (calls to further servers) = 3
            srv.println(ttl + "\n" + file);

            // Bring back the file, if any
            File f = new File("." + File.separator + file);
            FileOutputStream out = new FileOutputStream(f);
            boolean found = (Server.copyStream(s.getInputStream(), out, true) > 0);
            out.close();
            if (!found) {
                f.delete();
                return null;
            } else {
                return f;
            }
        } catch (IOException e) {
            return null;
        }
    }

    public void setRange(String range) {
        this.range = range;
    }

    public static List<RemoteServer> getServers(String path) throws IOException {
        ArrayList<RemoteServer> remoteServers = new ArrayList<>();
        Files.lines(Paths.get(path)).forEach((config)->{
            if(config.split(" ").length<2) return;
            String range = config.split(" ")[0];
            String[] server = config.split(" ")[1].split(":");
            RemoteServer rs;
            if(server.length>1){
                rs = new RemoteServer(server[0]);
            } else {
                rs = new RemoteServer(server[0], Integer.parseInt(server[1]));
            }
            rs.setRange(range);
            remoteServers.add(rs);
        });
        return remoteServers;
    }
}
