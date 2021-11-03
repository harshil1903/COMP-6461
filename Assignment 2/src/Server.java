import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
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

            try {
                pw = new PrintWriter(socket.getOutputStream());
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
            }

//            System.out.println("TEST 1 ");


            request = br.readLine();
//            String line;
//            if((line = br.readLine()) == null)
//                System.out.println("FILE NULL");
//            else
//                System.out.println("FILE NOT NULL");


            System.out.println(request);


            String rType = request.substring(0,7);



            if(rType.contains("httpc"))
            {
                System.out.println("Performing HTTPC operations");
            }

            else if(rType.contains("httpfs"))
            {


                System.out.println("Performing HTTPFS operations");
                String url = "";

                List<String> requestData = Arrays.asList(request.split(" "));

                if(request.contains("post"))
                {
                    url = requestData.get(3);
                }
                else
                {
                    url = requestData.get(requestData.size() - 1);
                }

                URI uri = new URI(url);

                String host = uri.getHost();

                String body = "{\n";
                body = body + "\t\"args\":";
                body = body + "{},\n";
                body = body + "\t\"headers\": {";


                body = body + "\n\t\t\"Connection\": \"close\",\n";
                body = body + "\t\t\"Host\": \"" + host + "\"\n";
                body = body + "\t},\n";

                String requestType = requestData.get(1);

                System.out.println(requestType);

                if(requestType.equalsIgnoreCase("GET") && requestData.get(2).equals("/"))
                {

                    body = body + "\t\"files\": { ";
                    List<String> files = getFilesFromDir(currentFolder);

                    //Can use files directly
                    List<String> fileFilterList = new ArrayList<String>();
                    fileFilterList.addAll(files);

                    for (int i = 0; i < fileFilterList.size() - 1; i++) {
                            body = body + files.get(i) + " ,\n\t\t\t    ";
                    }

                    body = body + fileFilterList.get(fileFilterList.size() - 1) + " },\n";

                 //   System.out.println(body);


                }

                else if(requestType.equalsIgnoreCase("GET") && !requestData.get(2).equals("/"))
                {
                    String response = "";
                    String requestedFileName = requestData.get(2).substring(1);

                    System.out.println(requestedFileName + "     TEST");

                    List<String> files = getFilesFromDir(currentFolder);

                    for(String f : files)
                        System.out.println(files);

                    if (!files.contains(requestedFileName)) {
                        //Send ERROR 404 : REQUESTED FILE NOT FOUND
                        response = "ERROR 404, REQUESTED FILE NOT FOUND";

                        body = body + "\t\"error\": \"" + response + "\",\n";

                    }
                    else {

                        File file = new File(dir + "/" + requestedFileName);
//                        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
//                        String st;
//                        while ((st = bufferedReader.readLine()) != null) {
//                            response = response + st;
//                        }
                        response = Server.readDataFromFile(file);
                           // serverResponse.setResponseCode("203");
                        body = body + "\t\"data\": \"" + response + "\",\n";
                           // System.out.println("Test 4 " + body);


                    }



                }

                else if(requestType.equalsIgnoreCase("POST"))
                {

                }

                body = body + "\t\"origin\": \"" + InetAddress.getLocalHost().getHostAddress() + "\",\n";
                body = body + "\t\"url\": \"" + url + "\"\n";
                body = body + "}\n";

                System.out.println(body);
                pw.write(body);
                pw.flush();


            }

        }

    }


    static public void writeResponseToFile(String fname, String data)
    {
        try
        {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("Assignment 1/src/" + fname));

            bufferedWriter.write(data);
            bufferedWriter.close();

            System.out.println("Response successfully saved to " + fname);

        } catch (IOException ex) {
            System.out.println("Error Writing file named '" + fname + "'" + ex);
        }
    }

    static public String readDataFromFile(File fname)
    {
        StringBuilder lines = new StringBuilder("");
        String line = null;

        try
        {

            BufferedReader bufferedReader = new BufferedReader(new FileReader(fname));

            while((line = bufferedReader.readLine()) != null)
            {
                lines.append(line + "\n");

            }
            bufferedReader.close();
        }
        catch(IOException ex)
        {
            System.out.println("Error reading file named '" + fname + "'" + ex);
        }

        return lines.toString();
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
