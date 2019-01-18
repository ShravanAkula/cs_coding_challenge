import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.HashMap;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;
import java.util.logging.Level;


import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.*;


public class processing_json {
    public static void main(String[] args) throws IOException, ParseException {

        HashMap<String,ArrayList<String>> multiMap = new HashMap<>();

        JSONParser parser = new JSONParser();

        BufferedReader reader = new BufferedReader(new FileReader(input_scanner()));
        String line = reader.readLine();
        Logger LOGGER = logger_method();

        while (line != null){
            LOGGER.log(Level.FINER, "Processing line " + line + " from json file");
            ArrayList<String> values = new ArrayList<String>();

            JSONObject jsonObject = (JSONObject) parser.parse(line);

            String id = (String) jsonObject.get("id");
            String state = (String) jsonObject.get("state");
            String host = (String) jsonObject.get("host");
            String timestamp = jsonObject.get("timestamp").toString();
            LOGGER.log(Level.FINER, "id : " + id + " ; host : " + host +" ; state : " + state  +" ; timestamp : " + timestamp);


            if (multiMap.containsKey(id)){
                Long time_difference;
                values = multiMap.get(id);
                if (values.contains(host)) {
                    values.add(state);
                    values.add(timestamp);
                    if (state.equalsIgnoreCase("Finished")){
                        time_difference = (Long.parseLong(timestamp) - Long.parseLong(values.get(values.indexOf("STARTED") + 1)));
                        values.add(Long.toString(time_difference));
                    }
                    else if (state.equalsIgnoreCase("STARTED")){
                        time_difference = (Long.parseLong(values.get(values.indexOf("FINISHED") + 1)) - Long.parseLong(timestamp));
                        values.add(Long.toString(time_difference));
                    }
                    else{
                        Integer i = 0;
                        time_difference = new Long(i);

                    }

                    if (time_difference >= 4){
                        LOGGER.log(Level.SEVERE, "Alert job "+ id + " has run for " + time_difference +" msec");
                    }
                }

            }
            else{
                Collections.addAll(values, host, state, timestamp);
                multiMap.put(id, values);
            }
            line = reader.readLine();
        }
        System.out.println(multiMap);
    }

    public static Logger logger_method(){
        Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        return LOGGER;
    }


    public static String input_scanner() throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter the absolute path of JSON file : ");
        String input = br.readLine();

        Logger LOGGER = logger_method();
        LOGGER.log(Level.INFO, "Processing file " + input);
        return input;
    }
}