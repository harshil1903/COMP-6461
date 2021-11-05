
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ftpC {

    static Socket socket = null;
    static PrintWriter pw = null;
    static BufferedReader br = null;

    public static void main(String[] args) throws IOException, URISyntaxException
    {


        while(true)
        {
            String request = "";
            String response = "";
            System.out.print("Enter FTP Command > ");
            Scanner sc = new Scanner(System.in);
            request = sc.nextLine();
            String url = "";


            if (request.isEmpty() ) {
                System.out.println("Invalid Command");
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


            //Send Request
            System.out.println("Sending request to Server");
            pw.write(request + "\n");
            pw.flush();


            //Receive Response
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StringBuilder dat = new StringBuilder();
            String line;

            socket.setSoTimeout(1 * 1000);

            try
            {
                while ((line = br.readLine()) != null) {
                    dat.append(line + "\n");
                }
            }
            catch (SocketTimeoutException s)
            {
                socket.close();
            }

            pw.close();
            br.close();

            response = dat.toString();
            System.out.println("\nResponse from Server : \n" + response);


        }


    }

}
