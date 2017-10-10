import algoritms.IProductMapGenerator;
import algoritms.KeywordProductMapGenerator;
import org.apache.log4j.Logger;
import algoritms.FullProductMapGenerator;
import support.TimeChecker;

import java.util.HashMap;
import java.util.Map;

public class ParserInitializer {
    private final static Logger LOGGER = Logger.getLogger(ParserInitializer.class);
    private final static int DEFAULT_CONNECTION_TIMEOUT = 2000;
    private final static int DEFAULT_REQUEST_TIMEOUT = 2000;
    private final static int DEFAULT_SOCKET_TIMEOUT = 2000;
    private static final long MEGABYTE = 1024L * 1024L;

    public static void main(String[] args) {
        IProductMapGenerator generator;
        String algo;
        int connectionTimeout = getIntArgument(args, 1, DEFAULT_CONNECTION_TIMEOUT);
        int requestTimeout = getIntArgument(args, 2, DEFAULT_REQUEST_TIMEOUT);
        int socketTimeout = getIntArgument(args, 3, DEFAULT_SOCKET_TIMEOUT);
        boolean usePrettyHeaders = getBooleanArgument(args, 4);
        if (args.length == 0 || args[0].length() == 0 || args[0].equals("")) {
            algo = "full site map";
            generator = new FullProductMapGenerator(connectionTimeout, requestTimeout, socketTimeout);
        } else {
            String keyword = args[0];
            algo = "keyword search: " + args[0];
            ;
            generator = new KeywordProductMapGenerator(keyword, connectionTimeout, requestTimeout, socketTimeout);
        }
        if (usePrettyHeaders) {
            Map<String, String> headers = new HashMap<>();
            headers.put("User-Agent", "User-Agent: Mozilla/5.0 (X11; Linux i686; rv:2.0.1) Gecko/20100101 Firefox/4.0.1");
            generator.setHeaders(headers);
        }
        TimeChecker checker = new TimeChecker();
        LOGGER.info("Starting algoritm: " + algo);
        printMemory();
        generator.generateSiteMap("https://www.aboutyou.de");
        LOGGER.info("Generation has been finished in "+checker.doCheck()+" ms.");
        printMemory();
    }

    private static void printMemory(){
        Runtime runtime = Runtime.getRuntime();
        long memory = runtime.totalMemory() - runtime.freeMemory();
        LOGGER.info("Used memory is megabytes: " + bytesToMegabytes(memory));
    }

    public static long bytesToMegabytes(long bytes) {
        return bytes / MEGABYTE;
    }

    private static int getIntArgument(String[] arguments, int number, int defaultValue) {
        int result;
        try {
            result = Integer.parseInt(arguments[number]);
        } catch (NumberFormatException e) {
            LOGGER.warn("Argument " + arguments[number] + " is not a valid integer value, using default: " + defaultValue);
            return defaultValue;
        } catch (ArrayIndexOutOfBoundsException e) {
            LOGGER.warn("No integer argument with index: " + number);
            return defaultValue;
        }
        return result>999?result:defaultValue;
    }

    private static boolean getBooleanArgument(String[] arguments, int number) {
        boolean result;
        try {
            result = arguments[number] != null && Boolean.parseBoolean(arguments[number]);
        } catch (NumberFormatException e) {
            LOGGER.warn("Argument " + arguments[number] + " is not a valid boolean value, using default false value");
            return false;
        } catch (ArrayIndexOutOfBoundsException e) {
            LOGGER.warn("No boolean argument with index: " + number);
            return false;
        }
        return result;
    }
}
