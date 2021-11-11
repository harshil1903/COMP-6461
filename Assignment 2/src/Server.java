import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Server {

    private static ServerSocket serverSocket;
    private static int port = 8080;
    private static int statusCode = 200;
    static PrintWriter pw = null;
    static BufferedReader br = null;

    static boolean debug = false;

    public static void main(String[] args) throws IOException, ClassNotFoundException, URISyntaxException {

        String request;
        List<String> requestList = new ArrayList<>();

        String dir = System.getProperty("user.dir");

        System.out.println("\nCurrent Directory : " + dir + "\n");

        System.out.print(">");
        Scanner sc = new Scanner(System.in);

        request = sc.nextLine();
        if (request.isEmpty()) {
            System.out.println("Invalid Command Please try again!!");
        }
        String[] requestArray = request.split(" ");

        requestList.addAll(Arrays.asList(requestArray));

        if (requestList.contains("-v")) {
            debug = true;
        }

        if (requestList.contains("-p")) {
            String portStr = requestList.get(requestList.indexOf("-p") + 1).trim();
            port = Integer.parseInt(portStr);
        }

        if (requestList.contains("-d")) {
            //dir = requestList.get(requestList.indexOf("-d") + 1).trim();
            dir = request.substring(request.indexOf("-d")+3);
            System.out.println("Selected directory for operations : " + dir + "\n");
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


            request = br.readLine();

            String rType = request.substring(0,7);

            if(rType.contains("httpc"))
            {
                System.out.println("Performing HTTPC operations\n\n");

                String url = "";
                String response = "";
                String options = "";
                int cl = 0;
                boolean verbose = false;

                List<String> requestData = Arrays.asList(request.split(" "));

                url = request.substring(request.indexOf("http://"), request.length() - 1);

                if(url.contains(" "))
                {
                    url = url.split(" ")[0];
                }

                URI uri = new URI(url);

                String host = uri.getHost();

                if (request.contains("get"))
                {
                    options = request.substring(request.indexOf("get") + 4);
                }

                else if(request.contains("post"))
                {
                    options = request.substring(request.indexOf("post") + 5);
                }

                if(options.contains("-v"))
                    verbose = true;

                String[] datalist = options.split(" ");
                List<String> data = Arrays.asList(datalist);

                String body = "{\n";

                if (request.contains("get"))
                {
                    String query = uri.getRawQuery();

                    List<String> querylist = Arrays.asList(query.split("&"));


                    //QUERY ARGUMENTS
                    body = body + "\t\"args\": {\n";

                    for (int i = 0 ; i < querylist.size() ; i++)
                    {
                        String t1 = querylist.get(i).split("=")[0];
                        String t2 = querylist.get(i).split("=")[1];

                        body = body + "\t\t\"" + t1 + "\": \"" + t2 + "\",\n";
                     }

                    body = body + "\t}, \n";


                    //HEADERS
                    body = body + "\t\"headers\": {\n";
                    for (int i = 0; i < data.size(); i++)
                    {
                        if (data.get(i).equals("-h")) {

                            String t1 = data.get(i+1).split(":")[0];
                            String t2 = data.get(i+1).split(":")[1];
                            body = body + "\t\t\"" + t1 + "\": \"" + t2 + "\",\n";
                        }
                    }

                    body = body + "\t\t\"Connection\": \"close\",\n";
                    body = body + "\t\t\"Host\": \"" + host + "\"\n";
                    body = body + "\t},\n";
                }


                else if(request.contains("post"))
                {

                    boolean jsonFlag = false;
                    String inlineData = "";
                    body = body + "\t\"args\": {},\n";


                    //INLINE DATA
                    body = body + "\t\"data\": \"";
                    if(options.contains("-d ")){
                        int index = data.indexOf("-d") + 1;

                        for (int i = index ; i < data.size() - 1 ; i++)
                            inlineData = inlineData + data.get(i);

                        inlineData = inlineData.substring(2, inlineData.length()-2);

                        body = body + inlineData + "\", \n";
                        cl = body.length();
                    }

                    body = body + "\t\"files\": {},\n";
                    body = body + "\t\"form\": {},\n";


                    //HEADERS
                    body = body + "\t\"headers\": {\n";
                    for (int i = 0; i < data.size(); i++)
                    {
                        if (data.get(i).equals("-h")) {

                            String t1 = data.get(i+1).split(":")[0];
                            String t2 = data.get(i+1).split(":")[1];

                            if(t2.contains("json"))
                                jsonFlag = true;

                            body = body + "\t\t\"" + t1 + "\": \"" + t2 + "\",\n";
                        }
                    }

                    body = body + "\t\t\"Connection\": \"close\",\n";
                    body = body + "\t\t\"Host\": \"" + host + "\"\n";
                    body = body + "\t\t\"Content-Length\": \"" + cl + "\"\n";
                    body = body + "\t},\n";


                    //JSON CONTENT
                    if(jsonFlag )
                    {
                        body = body + "\t\"json\": {\n";

                        List<String> jsonData = Arrays.asList(inlineData.split(","));

                        for (String s : jsonData)
                        {
                            body = body + "\t\t" + s + ",\n";
                        }
                        body = body + "\t},\n";

                    }

                }


                body = body + "\t\"origin\": \"" + InetAddress.getLocalHost().getHostAddress() + "\",\n";
                body = body + "\t\"url\": \"" + url + "\"\n";
                body = body + "}\n";

                response = body;

                String verboseBody = "";

                if(verbose)
                {
                    verboseBody = verboseBody + "HTTP/1.1 200 OK\n";
                    verboseBody = verboseBody + "Date: " + java.util.Calendar.getInstance().getTime() + "\n";
                    verboseBody = verboseBody + "Content-Type: application/json\n";
                    verboseBody = verboseBody + "Content-Length: "+ body.length() +"\n";
                    verboseBody = verboseBody + "Connection: close\n";
                    verboseBody = verboseBody + "Server: Localhost\n";
                    verboseBody = verboseBody + "Access-Control-Allow-Origin: *\n";
                    verboseBody = verboseBody + "Access-Control-Allow-Credentials: true\n";

                    response = verboseBody;
                    response = response + body;
                }

                if(debug)
                    System.out.println(response);
                pw.write(response);
                pw.flush();

                socket.close();
            }


            //HTTPFS FTP REQUEST
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


                // GET or POST
                String requestType = requestData.get(1);

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
                    statusCode = 200;

                }

                else if(requestType.equalsIgnoreCase("GET") && !requestData.get(2).equals("/"))
                {
                    String response = "";
                    String requestedFile = requestData.get(2).substring(1);


                    List<String> files = getFilesFromDir(currentFolder);


                    if (!files.contains(requestedFile))
                    {
                        statusCode = 404;
                    }
                    else
                    {

                        File file = new File(dir + "/" + requestedFile);
                        response = Server.readDataFromFile(file);
                        body = body + "\t\"data\": \"" + response + "\",\n";

                        statusCode = 200;
                    }


                }

                else if(requestType.equalsIgnoreCase("POST"))
                {
                    String response = "";
                    String requestedFile = requestData.get(2).substring(1);
                    String data = "";

                    List<String> files = getFilesFromDir(currentFolder);


                    if (!files.contains(requestedFile))
                        statusCode = 202;
                    else
                        statusCode = 201;

                    int index = requestData.indexOf("-d");

                    for(int i = index + 1 ; i < requestData.size() ; i++)
                    {
                        data = data + requestData.get(i) + " ";
                    }


                    File file = new File(dir + "/" + requestedFile);
                    Server.writeResponseToFile(file, data);

                }

                if(statusCode == 200)
                {
                    body = body + "\t\"status\": \"" + "HTTP/1.1 200 OK" + "\",\n";
                }
                else if(statusCode == 201)
                {
                    body = body + "\t\"status\": \"" + "HTTP/1.1 201 FILE OVER-WRITTEN" + "\",\n";
                }
                else if(statusCode == 202)
                {
                    body = body + "\t\"status\": \"" + "HTTP/1.1 202 NEW FILE CREATED" + "\",\n";
                }
                else if(statusCode == 404)
                {
                    body = body + "\t\"status\": \"" + "HTTP/1.1 404 FILE NOT FOUND" + "\",\n";
                }


                body = body + "\t\"origin\": \"" + InetAddress.getLocalHost().getHostAddress() + "\",\n";
                body = body + "\t\"url\": \"" + url + "\"\n";
                body = body + "}\n";

                if(debug)
                    System.out.println(body);
                pw.write(body);
                pw.flush();


            }

        }

    }


    static public void writeResponseToFile(File fname, String data)
    {
        try
        {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fname));

            bufferedWriter.write(data);
            bufferedWriter.close();

            if(debug)
                System.out.println("Response successfully saved to " + fname);

        } catch (IOException ex) {
            if(debug)
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
                lines.append(line);

            }
            bufferedReader.close();
        }
        catch(IOException ex)
        {
            if(debug)
                System.out.println("Error reading file named '" + fname + "'" + ex);
        }

        return lines.toString();
    }



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
