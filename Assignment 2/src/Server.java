import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {

    // static ServerSocket variable
    private static ServerSocket serverSocket;
    // socket server port on which it will listen
    private static int port = 8080;

    static PrintWriter pw = null;
    static BufferedReader br = null;

    static boolean debug = false;

    public static void main(String[] args) throws IOException, ClassNotFoundException, URISyntaxException {

        String request;
        List<String> requestList = new ArrayList<>();

        String dir = System.getProperty("user.dir");

        System.out.println("Dir ==>>>>> " + dir);

        System.out.print(">");
        Scanner sc = new Scanner(System.in);
        request = sc.nextLine();
        if (request.isEmpty()) {
            System.out.println("Invalid Command Please try again!!");
        }
        String[] requestArray = request.split(" ");
        requestList = new ArrayList<>();
        for (int i = 0; i < requestArray.length; i++) {
            requestList.add(requestArray[i]);
        }

        if (requestList.contains("-v")) {
            debug = true;
        }

        if (requestList.contains("-p")) {
            String portStr = requestList.get(requestList.indexOf("-p") + 1).trim();
            port = Integer.valueOf(portStr);
        }

        if (requestList.contains("-d")) {
            dir = requestList.get(requestList.indexOf("-d") + 1).trim();
            System.out.println("Dir ==>>>>> " + dir);
        }

        //debug = true;
        serverSocket = new ServerSocket(port);
        if (debug)
            System.out.println("Server is up and it assign to port Number: " + port);

        File currentFolder = new File(dir);

        while(true)
        {
            Socket socket = serverSocket.accept();
            if (debug)
                System.out.println("Connection established between server and client");

            pw = new PrintWriter(socket.getOutputStream());
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            request = br.readLine();

            String requestType = request.substring(0,7);

            if(requestType.contains("httpc"))
            {
                System.out.println("Performing HTTPC operations");
            }
            else if(requestType.contains("httpfs"))
            {
                System.out.println("Performing HTTPFS operations");




            }

        }

    }






    /**
     * This method will give list of files from specific directory
     *
     * @return List of files
     */
    static private List<String> getFilesFromDir(File currentDir) {
        List<String> filelist = new ArrayList<>();
        for (File file : currentDir.listFiles()) {
            if (!file.isDirectory()) {
                filelist.add(file.getName());
            }
        }
        return filelist;
    }
}
