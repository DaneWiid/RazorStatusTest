package razortest2;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author dane
 */
public class RazorTest2 {

    private String path;
    Connection.Response response;
    Document doc;
    String[] paths;
    Map<String, String> razorCodes;
    Map<String, String> htmlCodes;

    public static void main(String[] args) throws Exception {

        RazorTest2 test = new RazorTest2();
        
            for (String url : test.paths) {
                test.path = url;
                test.parsePage();
                test.htmlStatus();
                test.razorStatus();
            }

        System.out.println("Everything is working!");
    }//end main

    /**
     * The constructor instantiates and populates the error code maps
     */
    public RazorTest2() {

        paths = new String[]{
            "http://razor-cloud.com/xml/rest/reservation/available/143/2012-03-01/2012-03-30?pos=8508a7e6ce43e091",//Availability
            "http://razor-cloud.com/xml/rest/product/143/pricelist?pos=8508a7e6ce43e091",//Pricetable
            "http://razor-cloud.com/xml/rest/product/list/Accommodation?pos=8508a7e6ce43e091",//Products & Properties
            "http://razor-cloud.com/xml/rest/reservation/quote/1108/2012-06-01/2012-06-30?pos=8508a7e6ce43e091", //Quotes & Specials
        };

        razorCodes = new HashMap<>();

        razorCodes.put("currency_json", "JSON request to currency service failed");
        razorCodes.put("language_json", "JSON request to language service failed");
        razorCodes.put("nameid_json", "JSON request to name ID service failed");
        razorCodes.put("party_json", "JSON request to party service failed");
        razorCodes.put("price_json", "JSON request to price service failed");
        razorCodes.put("product_json", "JSON request to product service failed");
        razorCodes.put("reservation_api", "API error - displays error returned by partner API");
        razorCodes.put("reservation_json", "JSON request to reservation service failed");
        razorCodes.put("text_json", "JSON request to text service failed");
        //razorCodes.put("pos_invalid", "The point of sale code is invalid");

        htmlCodes = new HashMap<>();

        htmlCodes.put("400", "Request cannot be fulfilled due to bad syntax");
        htmlCodes.put("404", "Server not found because of badly formed URL or server is temporarily unavailable");
        htmlCodes.put("414", "Request URI is too long, usually because of large REST upload requests");
        htmlCodes.put("431", "Server cannot accept the request because one or all URL parameters are too large");
        htmlCodes.put("500", "Internal server error because of badly formed URL, absent or incorrectly formatted parameters or parameters not in correct sequence");
        htmlCodes.put("503", "Server temporarily unavailable because it is overloaded or down for maintenance");
        htmlCodes.put("Error loading stylesheet", "An XSL document is absent or is served by a different host than its XML document");
        htmlCodes.put("XML Parsing Error", "Occurs when a REST URL is not correctly formed - check the syntax carefully");
    }//end Constructor

    /**
     * Parse the page as a Jsoup Connection and store it in response object and
     * a Document object
     */
    private void parsePage() {
        try {
            response = Jsoup.connect(path)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 "
                            + "(KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                    .timeout(10000)
                    .execute();
            doc = response.parse();
        } catch (IOException e) {
            System.out.println("io - " + e);
        }
    }//end parsePage

    /**
     * check the HTML status of the pageLoad
     *
     * @return the connection status code
     */
    public void htmlStatus() throws Exception {

        int status = response.statusCode();

        if (htmlCodes.containsKey(status)) {
            throw new Exception((String) htmlCodes.get(status));
        }
    }//end getSitemapStatus

    /**
     * Check the status of relevant Razor codes
     *
     * @throws Exception
     */
    public void razorStatus() throws Exception {

        Element message = doc.select("message").first();
        //System.out.println(message);

        for (Map.Entry entry : razorCodes.entrySet()) {
            if (message.toString().contains((CharSequence) entry.getKey())) {
                throw new Exception((String) entry.getValue());

            }
        }
    }
}

