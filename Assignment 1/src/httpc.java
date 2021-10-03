
import java.util.Scanner;

public class httpc {
    public static void main(String[] args) throws Exception {
        httplibrary library = new httplibrary();
        Scanner sc = new Scanner(System.in);
        String input;
        String result;

        do {
            System.out.print("> ");
            input = sc.nextLine();
            System.out.println();

            if (input.startsWith("httpc")) {
                if (input.contains("help") && input.indexOf("help") == 6) {
                    //HELP
                    if (input.endsWith("help")) {
                        System.out.println("src.httpc is a curl-like application but supports HTTP protocol only.\n"
                                + "Usage:\n" + "   " + "src.httpc command [arguments]\n" + "The commands are:\n" + "    "
                                + "get     get executes a HTTP GET request and prints the response.\n" + "    "
                                + "post    post executes a HTTP POST request and prints the response.\n" + "    "
                                + "help    prints this screen.\n");

                    } else if (input.contains("get") && input.indexOf("get") == 11 && input.endsWith("get")) {
                        System.out.println("usage:  src.httpc get [-v] [-h key:value] URL\n"
                                + "Get executes a HTTP GET request for a given URL.\n"
                                + "  -v              Prints the detail of the response such as protocol, status,\n"
                                + "and headers.\n"
                                + "  -h key:value     Associates headers to HTTP Request with the format\n"
                                + "'key:value'");

                    } else if (input.contains("post") && input.indexOf("post") == 11 && input.endsWith("post")) {

                        System.out.println("usage: src.httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL\n"
                                + "Post executes a HTTP POST request for a given URL with inline data or from\n" + "file.\n"
                                + " -v Prints the detail of the response such as protocol, status,\n" + "and headers.\n"
                                + " -h key:value Associates headers to HTTP Request with the format\n" + "'key:value'.\n"
                                + " -d string Associates an inline data to the body HTTP POST request.\n"
                                + " -f file Associates the content of a file to the body HTTP POST\n" + "request.\n"
                                + "Either [-d] or [-f] can be used but not both.\n");

                    } else {
                        System.out.println("Please Enter Correct help Command");
                    }

                } else {
                    // GET
                    if (input.contains("get") && !(input.endsWith("help")) && !(input.endsWith("get")))
                    {
                        String url = input.substring(input.indexOf("http://"), input.length() - 1);
                        String data = input.substring(input.indexOf("get") + 4, input.indexOf("http://") - 1);
                        result = library.GET(url, data);
                        System.out.println(result);

                    }

                    // POST
                    else if (input.contains("post") && !(input.endsWith("help")))
                    {
                        String url = input.substring(input.indexOf("http://"), input.length());
                        String data = input.substring(input.indexOf("post") + 5, input.indexOf("http://") - 1);
                        result = library.POST(url, data);
                        System.out.println(result);


                    } else {
                        System.out.println("Invalid Command");
                    }
                }
            } else {
                System.out.println("Command must start with httpc");
            }

        }while (!input.equals("exit"));
    }
}

/*Test Commands

httpc get 'http://httpbin.org/get?course=networking&assignment=1'

httpc get -v 'http://httpbin.org/get?course=networking&assignment=1'

httpc post -h Content-Type:application/json -d '{"Assignment": 1}' http://httpbin.org/post

httpc get -h Content-Type:application/json 'http://httpbin.org/get?course=networking&assignment=1'

curl -X GET "https://httpbin.org/get" -H "accept: application/json"
src.httpc get -h accept:application/json "https://httpbin.org/get"

 */