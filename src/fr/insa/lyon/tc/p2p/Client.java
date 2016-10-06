//-------------//
// Client.java //
//-------------//

package fr.insa.lyon.tc.p2p;

import java.net.*;
import java.io.*;
import java.util.Objects;
import java.util.Scanner;

/*
* Usage: Client <filename>
* Lookup a file across one or more server(s), and bring it here.
* List of server IP addresses should be in local "server.in" text file
* (one IP per line)
*/
public class Client {

    public static void main(String[] args) throws Exception {
        RemoteServer server = new RemoteServer("127.0.0.1", 1237);
        String request;
        do{
            System.out.print("request > ");
            request = new Scanner(System.in).next();
            if(server.getFile(request, 5)!=null){
                System.out.println("File received!");
            } else {
                System.out.println("File not found");
            }
        } while (!Objects.equals(request, "exit"));
//        String ip;
//        //Change port corresponding to your team
//        int port=1234;
//        boolean found = false;
//
//        // Loop on servers list (obtained from local "servers.lst" file)
//        BufferedReader hosts = new BufferedReader(new FileReader("servers.lst"));
//        while(! found && (ip = hosts.readLine()) != null) {
//
//            Socket s = new Socket(ip, port);
//            PrintWriter srv = new PrintWriter(s.getOutputStream(), true);
//
//            // Ask for requested file, max depth (calls to further servers) = 3
//            srv.println(2 + "\n" + args[0]);
//
//            // Bring back the file, if any
//            File f = new File("." + File.separator + args[0]);
//            FileOutputStream out = new FileOutputStream(f);
//            found = (Server.copyStream(s.getInputStream(), out, true) > 0);
//            out.close();
//            if(! found) f.delete();
//
//        }
//        hosts.close();
    }
}
