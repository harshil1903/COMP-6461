
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class httplibrary {
    public static String NL = "\r\n";

    public String GET(String url, String options) throws URISyntaxException, IOException {


        List<String> headerList = new ArrayList<>();
        String[] datalist = options.split(" ");
        List<String> data = Arrays.asList(datalist);
        String body = "";

        String res = "";
        String headerKeyValue;
        StringBuilder request = new StringBuilder("");
        //FileOutputStream out = null;


        URI uri =  new URI(url);
        String host = uri.getHost();
        String path = uri.getRawPath();
        String query = uri.getRawQuery();
        int port = uri.getPort();
        String uAgent = httplibrary.class.getName();
        String conn = "";

//		System.out.println("Host: " + host);
//		System.out.println("Path: " + path);
//		System.out.println("Query: " + query);
//		System.out.println();

        if(path.length() == 0 || path == null)
            path = "/";

        if(query != null)
            query = "?" + query;
        else
            query = "";

        if(port == -1)
            port = 80;


        SocketAddress serverAddress = new InetSocketAddress(host, port);

        //Extra
        Socket sock = new Socket(host, 80);
        OutputStream os = sock.getOutputStream();
        PrintWriter pw = new PrintWriter(os);

        res = "GET " + path + query + " HTTP/1.0" + NL + "Host: " + host +  NL;
        request.append(res);


        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).equals("-h")) {
                headerList.add(data.get(i + 1));
            }

        }

        if(!headerList.isEmpty())
        {
            for (String s : headerList) {
                headerKeyValue = s.split(":")[0] + ": " + s.split(":")[1] + NL;
                request.append(headerKeyValue);
            }
        }

        request.append(NL);

        //Sending Part
        pw.write(request.toString());
        pw.flush();


        //Receiving Part
        BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        String line;
        StringBuilder ans = new StringBuilder();


        while ((line = br.readLine()) != null)
        {
            //System.out.println(line);
            ans.append(line + NL);
        }
        res = ans.toString();
        //Extra


        // Verbose
        if(!data.contains("-v"))
            return res.substring(res.indexOf("{"), res.lastIndexOf("}")+1);

        return res;
    }


    public String POST(String url, String options) throws URISyntaxException, IOException {


        List<String> headerList = new ArrayList<>();
        String[] datalist = options.split(" ");
        List<String> data = Arrays.asList(datalist);
        String body = "";
        int cl = 0;

        String req = "";
        String res = "";
        String headerKeyValue;
        StringBuilder request = new StringBuilder("");
        //FileOutputStream out = null;


        URI uri =  new URI(url);
        String host = uri.getHost();
        String path = uri.getRawPath();
        String query = uri.getRawQuery();
        int port = uri.getPort();
        String uAgent = httplibrary.class.getName();
        String conn = "";

//		System.out.println("Host: " + host);
//		System.out.println("Path: " + path);
//		System.out.println("Query: " + query);
        System.out.println();

        if(path.length() == 0 || path == null)
            path = "/";

        if(query != null)
            query = "?" + query;
        else
            query = "";

        if(port == -1)
            port = 80;


        SocketAddress serverAddress = new InetSocketAddress(host, port);

        //Extra
        Socket sock = new Socket(host, 80);
        OutputStream os = sock.getOutputStream();
        PrintWriter pw = new PrintWriter(os);

        req = "POST " + path + " HTTP/1.0" + NL + "Host: " + host + NL ;
        request.append(req);


        // Inline Data using -d
        if(options.contains("-d ")){
            body = options.substring(options.indexOf("{", options.indexOf("-d")), options.indexOf("}")+1);
            cl = body.length();
        }

        // File Data using -f
        if(options.contains("-f ")){

            String fname = data.get(data.indexOf("-f") + 1);
            //System.out.println("FILE NAME : " + fname);
            StringBuilder lines = new StringBuilder();
            String line = null;

            try
            {
                FileReader fileReader = new FileReader(fname);

                BufferedReader bufferedReader = new BufferedReader(fileReader);

                while((line = bufferedReader.readLine()) != null)
                {
                    lines.append(line);
                }
                body = lines.toString();
                bufferedReader.close();
            }
            catch(IOException ex)
            {
                System.out.println("Error reading file named '" + fname + "'");
            }

        }


        request.append("Content-Length: " + cl + NL);


        // Headers using -h
        for (int i = 0; i < data.size(); i++)
        {
            if (data.get(i).equals("-h")) {
                headerList.add(data.get(i + 1));
            }
            //Add for -d and -f for post
        }

        if(!headerList.isEmpty())
        {
            for (String s : headerList) {
                headerKeyValue = s.split(":")[0] + ": " + s.split(":")[1] + NL;
                request.append(headerKeyValue);
            }
        }


        request.append( NL + body);

        //Sending Part
        pw.write(request.toString());
        pw.flush();


        //Receiving Part
        BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        String line;
        StringBuilder ans = new StringBuilder();


        while ((line = br.readLine()) != null)
        {
            //System.out.println(line);
            ans.append(line + NL);
        }
        res = ans.toString();
        //Extra

        // Verbose
        if(!data.contains("-v"))
            return res.substring(res.indexOf("{"), res.lastIndexOf("}"));


        return res;
    }

}