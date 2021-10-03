
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

        String res = "";
        String headerKeyValue;
        StringBuilder request = new StringBuilder("");


        URI uri =  new URI(url);
        String host = uri.getHost();
        String path = uri.getRawPath();
        String query = uri.getRawQuery();
        int port = uri.getPort();
        String uAgent = httplibrary.class.getName();
        String conn = "";


        if(path.length() == 0 || path == null)
            path = "/";

        if(query != null)
            query = "?" + query;
        else
            query = "";

        if(port == -1)
            port = 80;

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
            ans.append(line + NL);
        }
        res = ans.toString();
        //Extra

        //Verbose
        if(!data.contains("-v"))
            res = res.substring(res.indexOf("{"), res.lastIndexOf("}")+1);


        // Writing Response to a file using -o
        if(options.contains("-o ")) {

            String fname = data.get(data.indexOf("-o") + 1);

            writeResponseToFile(fname, res);
            return "";

        }


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


        URI uri =  new URI(url);
        String host = uri.getHost();
        String path = uri.getRawPath();
        String query = uri.getRawQuery();
        int port = uri.getPort();
        String uAgent = httplibrary.class.getName();
        String conn = "";


        if(path.length() == 0 || path == null)
            path = "/";

        if(query != null)
            query = "?" + query;
        else
            query = "";

        if(port == -1)
            port = 80;


        //Extra
        Socket sock = new Socket(host, 80);
        OutputStream os = sock.getOutputStream();
        PrintWriter pw = new PrintWriter(os);

        req = "POST " + path + " HTTP/1.0" + NL + "Host: " + host + NL ;
        request.append(req);


        // Inline Data using -d
        if(options.contains("-d ")){
            body = options.substring(options.indexOf("{", options.indexOf("-d")), options.indexOf("}")+1);
            System.out.println(body);
            cl = body.length();
        }

        // File Data using -f
        if(options.contains("-f ")){

            String fname = data.get(data.indexOf("-f") + 1);

            body = readDataFromFile(fname);
            cl = body.length();
        }

        request.append("Content-Length: " + cl + NL);


        // Headers using -h
        for (int i = 0; i < data.size(); i++)
        {
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

        request.append(NL + body);

        //Sending Part
        pw.write(request.toString());
        pw.flush();


        //Receiving Part
        BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        String line;
        StringBuilder ans = new StringBuilder();


        while ((line = br.readLine()) != null)
        {
            ans.append(line + NL);
        }

        res = ans.toString();
        //Extra


        if(!data.contains("-v"))
            res = res.substring(res.indexOf("{"), res.lastIndexOf("}"));


        // Writing Response to a file using -o
        if(options.contains("-o "))
        {
            String fname = data.get(data.indexOf("-o") + 1);
            writeResponseToFile(fname, res);
            return "";
        }

        return res;
    }


    public void writeResponseToFile(String fname, String data)
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

    public String readDataFromFile(String fname)
    {
        StringBuilder lines = new StringBuilder("");
        String line = null;

        try
        {

            BufferedReader bufferedReader = new BufferedReader(new FileReader("Assignment 1/src/"+ fname));

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

}