package xcarpaccio;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static xcarpaccio.StringUtils.stringify;

@SuppressWarnings("restriction")
public class MyHttpServer
{
    private final int port;
    private final Logger logger;
    private HttpServer server;

    MyHttpServer(int port) {
        this.port = port;
        this.logger = new Logger();
    }

    MyHttpServer(int port, Logger logger) {
        this.port = port;
        this.logger = logger;
    }

    void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/ping", new PingHttpHandler());
        server.createContext("/feedback", new FeedbackHttpHandler());
        server.createContext("/order", new OrderHttpHandler());
        server.createContext("/", new ConsoleHttpHandler());
        server.start();

        logger.log("Server running on port " + port + "...");
    }

    void shutdown() {
        if(server != null) {
            logger.log("Stopping server...");
            server.stop(2);
        }
    }

    public static void main( String[] args ) throws IOException {
        new MyHttpServer(9000).start();
    }

    private abstract class AbstractHttpHandler implements HttpHandler {
        protected static final String NO_CONTENT = "";

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response = respond(httpExchange);
            httpExchange.sendResponseHeaders(200, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        public abstract String respond(HttpExchange httpExchange);
    }

    private class PingHttpHandler extends AbstractHttpHandler {
        @Override
        public String respond(HttpExchange httpExchange) {
            return "pong";
        }
    }

    private class FeedbackHttpHandler extends AbstractHttpHandler {
        protected final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public String respond(HttpExchange httpExchange) {
            InputStream body = httpExchange.getRequestBody();

            try {
                Message message = objectMapper.readValue(body, Message.class);
                logger.log(message.getType() + ": " + message.getContent());
            } catch (IOException exception) {
                logger.error(exception.getMessage());
            }

            return NO_CONTENT;
        }
    }

    private class ConsoleHttpHandler extends AbstractHttpHandler {
		@Override
        public String respond(HttpExchange httpExchange) {
            String method = httpExchange.getRequestMethod();
            String uri = httpExchange.getRequestURI().getPath();
            logger.log(method + " " + uri + " " + stringify(httpExchange.getRequestBody()));
            return NO_CONTENT;
        }
    }
    
    private static Map<String, Integer> COUNTRIES = new HashMap<String, Integer>();
    
    static
    {
        COUNTRIES.put("DE", 20);
        COUNTRIES.put("FR", 20);
        COUNTRIES.put("RO", 20);
        COUNTRIES.put("NL", 20);
        COUNTRIES.put("LV", 20);
        COUNTRIES.put("MT", 20);
        COUNTRIES.put("EL", 20);
        
        COUNTRIES.put("UK", 21);
        COUNTRIES.put("PL", 21);
        COUNTRIES.put("BG", 21);
        COUNTRIES.put("DK", 21);
        COUNTRIES.put("IE", 21);
        COUNTRIES.put("CY", 21);
        
        
        COUNTRIES.put("IT", 25);
        COUNTRIES.put("LU", 25);
        
        COUNTRIES.put("ES", 19);
        COUNTRIES.put("CZ", 19);
        COUNTRIES.put("BE", 24);
        COUNTRIES.put("SI", 24);
        COUNTRIES.put("PT", 23);
        COUNTRIES.put("SE", 23);
        COUNTRIES.put("HR", 23);
        COUNTRIES.put("LT", 23);
        
        COUNTRIES.put("HU", 27);
        COUNTRIES.put("AT", 22);
        COUNTRIES.put("EE", 22);
        COUNTRIES.put("FI", 17);
        COUNTRIES.put("SK", 18);
        
        
    }
    
    private class OrderHttpHandler extends AbstractHttpHandler {
        protected final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public String respond(HttpExchange httpExchange) {
            InputStream body = httpExchange.getRequestBody();
            
            try 
            {
            	
                Order order = objectMapper.readValue(body, Order.class);
                logger.error(order.toString());
                
                Integer perc = COUNTRIES.get(order.getCountry()); 
                
           //     Optional<Reduction> reduction = Reduction.valueOfFrom(order.getReduction());
                
                if ("STANDARD".equalsIgnoreCase(order.getReduction()) && perc != null)
                {
                	Double total = order.totalWithPerc(perc);
                	
                		total = getTotalWithReduction(total);
                		String totalResponse = generateAndLog(total);
						return totalResponse;
 
                }
                else if ("HALF PRICE".equals(order.getReduction()) && perc != null)
                {
                	Double total = order.totalWithPerc(perc);
                	
            		total = total / 2;
            		String totalResponse = generateAndLog(total);
					return totalResponse;

                }
                else if ("PAY THE PRICE".equals(order.getReduction()) && perc != null)
                {
                	Double total = order.totalWithPerc(perc);
            		String totalResponse = generateAndLog(total);
					return totalResponse;
                }
                else
                {
                	
                	logger.error("ATTENTION NON GERE : ");
                	  String method = httpExchange.getRequestMethod();
                      String uri = httpExchange.getRequestURI().getPath();
                      logger.log(method + " " + uri + " " + stringify(httpExchange.getRequestBody()));
                }
               
//                logger.log(message.getType() + ": " + message.getContent());
            } catch (IOException exception) {
                logger.error(exception.getMessage());
            }

            return NO_CONTENT;
        }

		private String generateAndLog(Double total) {
			String totalResponse = String.format("{\"total\": %s}",  total);
			logger.error(totalResponse);
			return totalResponse;
		}

		private Double getTotalWithReduction(Double total) {
			if (total >= 50000)
			{
				total = total - total *0.15;
			}
			if (total >= 1000 && total <5000)
			{
				total = total - total * 0.03;
			}
			if (total >= 5000 && total <7000)
			{
				total = total - total * 0.05;
			}
			if (total >= 7000 && total <10000)
			{
				total = total - total * 0.07;
			}
			if (total >= 10000 && total <50000)
			{
				total = total - total * 0.1;
			}

			return total;
		}
    }

}
