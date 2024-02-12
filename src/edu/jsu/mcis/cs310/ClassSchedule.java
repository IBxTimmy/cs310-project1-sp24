package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class ClassSchedule {
    
    private final String CSV_FILENAME = "jsu_sp24_v1.csv";
    private final String JSON_FILENAME = "jsu_sp24_v1.json";
    
    private final String CRN_COL_HEADER = "crn";
    private final String SUBJECT_COL_HEADER = "subject";
    private final String NUM_COL_HEADER = "num";
    private final String DESCRIPTION_COL_HEADER = "description";
    private final String SECTION_COL_HEADER = "section";
    private final String TYPE_COL_HEADER = "type";
    private final String CREDITS_COL_HEADER = "credits";
    private final String START_COL_HEADER = "start";
    private final String END_COL_HEADER = "end";
    private final String DAYS_COL_HEADER = "days";
    private final String WHERE_COL_HEADER = "where";
    private final String SCHEDULE_COL_HEADER = "schedule";
    private final String INSTRUCTOR_COL_HEADER = "instructor";
    private final String SUBJECTID_COL_HEADER = "subjectid";
    
    public String convertCsvToJsonString(List<String[]> csv) {
        JsonObject scheduletypeObj = new JsonObject();
        JsonObject subjectObj = new JsonObject();
        JsonObject courseObj = new JsonObject();
        ArrayList<JsonObject> sectionObj = new ArrayList<>();
        
        Iterator<String[]> iterator = csv.iterator();
        String[] headerRow = iterator.next();
        HashMap<String, ArrayList<String>> headers = new HashMap<>();
        // Initialize ArrayLists for each header
        for (String header : headerRow) {
            headers.put(header, new ArrayList<>());
        }
        // Populate the HashMap with data from CSV
        while (iterator.hasNext()) {
            String[] record = iterator.next();
    
            for (int i = 0; i < headerRow.length; i++) {
                headers.get(headerRow[i]).add(record[i]);
            }
        }


        
        
        //Takes the type column in the csv and pairs it with the schedule type
        HashMap<String, String> scheduletype = new HashMap<>();
        ArrayList<String> types = headers.get(TYPE_COL_HEADER);
        ArrayList<String> schedules = headers.get(SCHEDULE_COL_HEADER);
        for (int i = 0; i < types.size(); i++){
            scheduletype.put(types.get(i), schedules.get(i));
        }
        //Enhanced for loop to populate the scheduletypeObj JsonObject with the key-value pairs from the scheduletype HashMap
        for (String key : scheduletype.keySet()){
            scheduletypeObj.put(key, scheduletype.get(key));
        }

        
        
        //HashMap for subject pair
        HashMap<String, String> subjectpair = new HashMap<>();
        ArrayList<String> subject = headers.get(SUBJECT_COL_HEADER);
        ArrayList<String> number = headers.get(NUM_COL_HEADER);
        ArrayList<String> subjectID = new ArrayList<>();
        ArrayList<String> num = new ArrayList<>();
        //Gets num column from csv file and splits it
        for (String str : number) {
            String[] splitnum = str.split(" ");
            int halfLength = splitnum.length / 2;
            // Add the first half of the split values to the 'subjectID' ArrayList
            for (int i = 0; i < halfLength; i++) {
                subjectID.add(splitnum[i]);
            }
            // Add the second half of the split values to the 'num' ArrayList
            for (int i = halfLength; i < splitnum.length; i++) {
                num.add(splitnum[i]);
            } 
        }
        //Create a HashMap to store the mapping between the first half of the split information and the subject array
        HashMap<String, String> tempMap = new HashMap<>();
        //Iterates through the subjectID ArrayList and to populate the subjectpair HashMap
        for (int i = 0; i < subjectID.size(); i++){
            String key = subjectID.get(i);
            String value = subject.get(i);
            //Check if the key already exists in the tempMap to avoid diplicates
            if (!tempMap.containsKey(key)){
                subjectpair.put(key, value);
                tempMap.put(key, value);//Adds to tempMap to mark it as seen
            }
        }
        for (String key : subjectpair.keySet()){
            subjectObj.put(key, subjectpair.get(key));
        }
        
        
        //course hashmap and arraylist for description and credit number from the csv headers hashmap
        HashMap<String, HashMap<String, String>> course = new HashMap<>();
        ArrayList<String> desc = headers.get(DESCRIPTION_COL_HEADER);
        ArrayList<String> cred_num = headers.get(CREDITS_COL_HEADER);
        //Create inner map for course
        for (int i = 0; i < desc.size(); i++){
            String key = NUM_COL_HEADER;
            HashMap<String,String> innerMap = new HashMap<>();
            //Populate the inner map with the description, credits, subject id, and course number
            innerMap.put(DESCRIPTION_COL_HEADER, desc.get(i));
            innerMap.put(CREDITS_COL_HEADER, cred_num.get(i));
            innerMap.put("subjectid", subjectID.get(i));
            innerMap.put("num", num.get(i));
            course.put(key, innerMap);
        }
        for (String key : course.keySet()){
            courseObj.put(key, course.get(key));
        }
        
        
        
        ArrayList<HashMap<String, String>> section = new ArrayList<>();
        ArrayList<String> crn_num = headers.get(CRN_COL_HEADER);
        ArrayList<String> section_num = headers.get(SECTION_COL_HEADER);
        ArrayList<String> classtype = headers.get(TYPE_COL_HEADER);
        ArrayList<String> starttime = headers.get(START_COL_HEADER);
        ArrayList<String> endtime = headers.get(END_COL_HEADER);
        ArrayList<String> days = headers.get(DAYS_COL_HEADER);
        ArrayList<String> where = headers.get(WHERE_COL_HEADER);
        ArrayList<String> instructor = headers.get(INSTRUCTOR_COL_HEADER);
        for (int i = 0; i < headers.size();i++){
            HashMap<String, String> map = new HashMap<>();
            map.put("subjectid", subjectID.get(i));
            map.put("num", num.get(i));
            map.put(CRN_COL_HEADER, crn_num.get(i));
            map.put(SECTION_COL_HEADER, section_num.get(i));
            map.put(TYPE_COL_HEADER, classtype.get(i));
            map.put(START_COL_HEADER, starttime.get(i));
            map.put(END_COL_HEADER, endtime.get(i));
            map.put(DAYS_COL_HEADER, days.get(i));
            map.put(WHERE_COL_HEADER, where.get(i));
            map.put(INSTRUCTOR_COL_HEADER, instructor.get(i));
            section.add(map);
        }
        for (HashMap<String, String> map : section){
            JsonObject jsonObj = new JsonObject(map);
            sectionObj.add(jsonObj);
        }
        
        
        
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(scheduletypeObj);
        jsonArray.add(subjectObj);
        jsonArray.add(courseObj);
        jsonArray.addAll(sectionObj);
        
        String jsonString = Jsoner.serialize(jsonArray);
        
        
        
        // remove this!
        return jsonString;
        
    }

    
    public String convertJsonToCsvString(JsonObject json) throws IOException {
        StringBuilder csvFile = new StringBuilder();
        try{
            BufferedReader reader = new BufferedReader(new FileReader(CSV_FILENAME));
            String line;
            while((line = reader.readLine()) != null){
                csvFile.append(line).append("\n");
            }
        }
        catch(IOException e) {e.printStackTrace();}
        String csvString = csvFile.toString().trim();
        
        CSVReader reader = new CSVReader(new StringReader(csvString));
        //List<String[]> full = reader.readAll();
        
        
        
        return ""; // remove this!
        
    }
    
    public JsonObject getJson() {
        
        JsonObject json = getJson(getInputFileData(JSON_FILENAME));
        return json;
        
    }
    
    public JsonObject getJson(String input) {
        
        JsonObject json = null;
        
        try {
            json = (JsonObject)Jsoner.deserialize(input);
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return json;
        
    }
    
    public List<String[]> getCsv() {
        
        List<String[]> csv = getCsv(getInputFileData(CSV_FILENAME));
        return csv;
        
    }
    
    public List<String[]> getCsv(String input) {
        
        List<String[]> csv = null;
        
        try {
            
            CSVReader reader = new CSVReaderBuilder(new StringReader(input)).withCSVParser(new CSVParserBuilder().withSeparator('\t').build()).build();
            csv = reader.readAll();
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return csv;
        
    }
    
    public String getCsvString(List<String[]> csv) {
        
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer, '\t', '"', '\\', "\n");
        
        csvWriter.writeAll(csv);
        
        return writer.toString();
        
    }
    
    private String getInputFileData(String filename) {
        
        StringBuilder buffer = new StringBuilder();
        String line;
        
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        
        try {
        
            BufferedReader reader = new BufferedReader(new InputStreamReader(loader.getResourceAsStream("resources" + File.separator + filename)));

            while((line = reader.readLine()) != null) {
                buffer.append(line).append('\n');
            }
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return buffer.toString();
        
    }
    
}