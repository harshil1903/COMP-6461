
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.Scanner;

public class httpClient {

    static Socket socket = null;
    static PrintWriter pw = null;
    static BufferedReader br = null;

    public static boolean validateGet(String input)
    {
        if(input.contains("-d") || input.contains("-f"))
        {
            return false;
        }
        return true;
    }

    public static boolean validatePost(String input)
    {
        if(input.contains("-d") && input.contains("-f"))
        {
            return false;
        }
        return true;
    }



    public static void main(String[] args) throws Exception {
        httpClientLibrary library = new httpClientLibrary();
        Scanner sc = new Scanner(System.in);
        String input;
        String result;


        do {
            System.out.print("> ");
            input = sc.nextLine();
            System.out.println();

            if (input.startsWith("httpc"))
            {
                if (input.contains("help") && input.indexOf("help") == 6)
                {
                    //HELP
                    if (input.endsWith("help"))
                    {
                        System.out.println("httpc is a curl-like application but supports HTTP protocol only.\n"
                                + "Usage:\n" + "   " + "httpc command [arguments]\n" + "The commands are:\n" + "    "
                                + "get     get executes a HTTP GET request and prints the response.\n" + "    "
                                + "post    post executes a HTTP POST request and prints the response.\n" + "    "
                                + "help    prints this screen.\n");

                    }
                    else if (input.contains("get") && input.indexOf("get") == 11 && input.endsWith("get"))
                    {
                        System.out.println("usage: httpc get [-v] [-h key:value] URL\n"
                                + "Get executes a HTTP GET request for a given URL.\n"
                                + "  -v              Prints the detail of the response such as protocol, status,\n"
                                + "and headers.\n"
                                + "  -h key:value     Associates headers to HTTP Request with the format\n"
                                + "'key:value'");

                    }
                    else if (input.contains("post") && input.indexOf("post") == 11 && input.endsWith("post"))
                    {
                        System.out.println("usage: httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL\n"
                                + "Post executes a HTTP POST request for a given URL with inline data or from\n" + "file.\n"
                                + " -v Prints the detail of the response such as protocol, status,\n" + "and headers.\n"
                                + " -h key:value Associates headers to HTTP Request with the format\n" + "'key:value'.\n"
                                + " -d string Associates an inline data to the body HTTP POST request.\n"
                                + " -f file Associates the content of a file to the body HTTP POST\n" + "request.\n"
                                + "Either [-d] or [-f] can be used but not both.\n");

                    }
                    else
                    {
                        System.out.println("Please Enter Correct help Command");
                    }

                }
                else
                {

                    // GET
                    if (input.contains("get") && !(input.endsWith("help")) && !(input.endsWith("get")))
                    {
                        if(!validateGet(input))
                        {
                            System.out.println("GET command can not have -d or -f as options. \nTry Again");
                            continue;
                        }

                        String url = input.substring(input.indexOf("http://"), input.length() - 1);
                        if(url.contains(" "))
                        {
                            url = url.split(" ")[0];
                        }

                        //String data = input.substring(input.indexOf("get") + 4);
                        //result = library.GET(url, data);
                        //System.out.println(result);

                        String response;
                        URI uri = new URI(url);

                        String hostName = uri.getHost();
                        socket = new Socket(hostName, uri.getPort());

                        pw = new PrintWriter(socket.getOutputStream());


                        //Send Request
                        System.out.println("Sending request to Server");
                        pw.write(input + "\n");
                        pw.flush();


                        //Receive response
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
                        System.out.println("\nResponse from Server : \n " + response);

                    }

                    // POST
                    else if (input.contains("post") && !(input.endsWith("help")))
                    {
                        if(!validatePost(input))
                        {
                            System.out.println("POST command can have either -d or -f but not both of them together as options. \nTry Again");
                            continue;
                        }


                        String url = input.substring(input.indexOf("http://"), input.length());
                        if(url.contains(" "))
                        {
                            url = url.split(" ")[0];
                        }

//                        String data = input.substring(input.indexOf("post") + 5);
//                        result = library.POST(url, data);
//                        System.out.println(result);

                        URI uri = new URI(url);

                        String hostName = uri.getHost();
                        socket = new Socket(hostName, uri.getPort());

                        pw = new PrintWriter(socket.getOutputStream());


                        //Send Request
                        System.out.println("Sending request to Server");
                        pw.write(input + "\n");
                        pw.flush();

                        //Receive response
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
                        String response = dat.toString();
                        System.out.println("\nResponse from Server : \n " + response);


                    }
                    else
                    {
                        System.out.println("Invalid  GET/POST Command");
                    }
                }
            }
            else
            {
                System.out.println("Command must start with httpc");
            }

        }while (!input.equals("exit"));
    }
}


/*Test Commands

httpc get 'http://httpbin.org/get?course=networking&assignment=1'

httpc get -v 'http://httpbin.org/get?course=networking&assignment=1'

httpc get -v 'http://httpbin.org/get?course=networking&assignment=1' -o outget.txt

httpc get -h Content-Type:application/json 'http://httpbin.org/get?course=networking&assignment=1'

httpc post -h Content-Type:application/json -d '{"Assignment": 1, "Course":6481}' http://httpbin.org/post

httpc post -h Content-Type:application/json -f abc.txt http://httpbin.org/post

httpc post -v -h Content-Type:application/json -f abc.txt http://httpbin.org/post -o out.txt



curl -X GET "https://httpbin.org/get" -H "accept: application/json"
httpc get -h accept:application/json 'http://httpbin.org/get'

curl -X GET "http://httpbin.org/headers" -H  "accept: application/json"
httpc get -h accept:application/json 'http://httpbin.org/headers'

 */