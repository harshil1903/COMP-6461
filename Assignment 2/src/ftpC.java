
import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ftpC {

    private static List<String> headerLst = null;
    static Socket socket = null;
    static PrintWriter pw = null;
    static BufferedReader br = null;

    public static void main(String[] args) throws UnknownHostException, IOException, URISyntaxException, ClassNotFoundException
    {
        String dir = System.getProperty("user.dir");
        File file = new File("ftpC");
        file.mkdir();

        while(true)
        {
            String request = "";
            String response = "";
            System.out.print("Enter FTP Command > ");
            Scanner sc = new Scanner(System.in);
            request = sc.nextLine();
            String url = "";


            if (request.isEmpty() ) {
                System.out.println("Invalid Command, use the following command suggestions to use");
                //Add a method to display possible command input options
                continue;
            }

            if((request.contains("post") && !request.contains("-d")))
            {
                System.out.println("Please enter POST url with inline data");
                continue;
            }

            List<String> requestlist;
            requestlist = Arrays.asList(request.split(" "));

            if(request.contains("post"))
            {
                url = requestlist.get(3);
            }
            else
            {
                url = requestlist.get(requestlist.size() - 1);
            }

            URI uri = new URI(url);

            String hostName = uri.getHost();
            socket = new Socket(hostName, uri.getPort());

            pw = new PrintWriter(socket.getOutputStream());
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StringBuilder dat = new StringBuilder();
            String line;

            //Send Request
            System.out.println("Sending request to Server");
            pw.write(request + "\n");
            pw.flush();

//            System.out.println("Test 1");
//
//            line = br.readLine();
//
//            System.out.println(line);


            //Receive Response
            while((line = br.readLine()) != null)
            {
                System.out.println(line);
                dat.append(line + "\n");
            }
            response = dat.toString();
//            DataInputStream dis = null;
//            byte[] buffer = new byte[1024];
//            boolean end = false;
//            int read;
//
//            try{
//                dis = new DataInputStream(socket.getInputStream());
//
//                while(!end)
//                {
//                    int bytesRead = dis.read(buffer);
//                    response += new String(buffer, 0 , bytesRead);
//                }
//            }
//            catch (Exception e)
//            {
//                e.printStackTrace();
//            }



            System.out.println("\nResponse from Server : \n " + response);


        }


    }

}
