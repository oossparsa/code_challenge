package com.example.parsaBadiei;

import org.apache.el.lang.FunctionMapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.lang.Math.*;

@RestController
public class ApplicationController {

    @Autowired
    FileUploadProperties UploadedFilesProperties;

    //Map<Integer, ArrayList<Long>> file_count_line;
    Map<String, Map<Integer, ArrayList<Long>>> files_line_info;

    @PostConstruct
    public void init(){
        files_line_info = new HashMap<String, Map<Integer, ArrayList<Long>>>();
    }
    @GetMapping("/")
    public String index() {
        return "Greetings from Spring Boot!\nParsa here!";
    }


    @GetMapping("/application/process:{file-name}")
    public Map<Integer,ArrayList<Long>> process_file(@PathVariable("file-name") String fileName){
        File sample_file = new File(UploadedFilesProperties.getLocation()+"/"+fileName);
        long sample_file_length = sample_file.length();
        //System.out.println("file name: "+ sample_file.getName()+", length: "+sample_file_length);
        Scanner sc=null;
        //map of <line_length, line_number>
        Map<Integer, ArrayList<Long>> map_length_line_num= new HashMap<Integer, ArrayList<Long>>();
        // map of <line_length, line>
        //Map<Integer, ArrayList<String>> map_length_line_text = new HashMap<Integer, ArrayList<String>>();
        String line="";
        Integer line_length=0;
        if (sample_file.exists() && sample_file.canRead()){
            try { sc = new Scanner(new FileInputStream(sample_file));
            } catch (IOException e) { e.printStackTrace(); }

            for (long i=0; i<sample_file_length && sc.hasNextLine(); i++) {
                line = sc.nextLine();
                line_length = line.length();
                if (map_length_line_num.containsKey(line_length) ) {
                    map_length_line_num.get(line_length).add(i);
                } else {
                    map_length_line_num.put(line_length, new ArrayList<Long>(Arrays.asList(i)));
                }
            }
        }
        //this.file_count_line = map_length_line_num;
        if (!this.files_line_info.containsKey(fileName))
            this.files_line_info.put(fileName, map_length_line_num);
        return map_length_line_num;}

    @GetMapping("/getline={line},file={file_name}")
    public String getLine(@PathVariable("line") int line_number, @PathVariable("file_name") String fileName){
        Stream<String> lines = null;
        try {
            lines = Files.lines(Paths.get(UploadedFilesProperties.getLocation()+"/"+fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines.skip(line_number-1).findFirst().get();
    }

    @GetMapping("/longestline={file_name},{count}")
    public text_file_response longestLine(@PathVariable("file_name") String fileName, @PathVariable("count") int count){
        //return this.file_count_line.get();
        if (!this.files_line_info.containsKey(fileName))
            process_file(fileName);
        File file = new File(UploadedFilesProperties.getLocation()+"/"+fileName);
        long total_lines = 0;
        Stream<String> lines = null;
        try{
            lines = Files.lines(file.toPath());
            total_lines = lines.count();
        }
        catch (IOException e){e.printStackTrace();}
        long longest_line_num = this.files_line_info.get(fileName).get(
                Collections.max(this.files_line_info.get(fileName).keySet()))
                .get(0);
        if (count ==1){
            try {
                lines = Files.lines(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            String longest_line_value = lines.skip(longest_line_num).findFirst().get();
            return new text_file_response(fileName, total_lines, longest_line_num+1 ,longest_line_value, null);
        }
        else{
            Set<Integer> keySet= this.files_line_info.get(fileName).keySet();
            List<Integer> sortedKeys= keySet.stream().sorted().collect(Collectors.toList());
            Map<Long,String> long_lines = new HashMap<Long,String>();
            List<Integer> keys_to_look_for = sortedKeys.subList(sortedKeys.size()-count, sortedKeys.size());
            List<Long> lines_to_look_for = new ArrayList<Long>();
            for (Integer key : keys_to_look_for)
                lines_to_look_for.add(this.files_line_info.get(fileName).get(key).get(0));

            for (long line_number : lines_to_look_for){
                try {
                    lines = Files.lines(file.toPath());
                    long_lines.put(line_number,lines.skip(line_number).findFirst().get());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return new text_file_response(fileName, total_lines, longest_line_num+1, null,long_lines);
        }

    }

    @GetMapping("/longest{toplongest}")
    public Map<Integer, HashMap<String, String>> longest100lines(@PathVariable("toplongest") int n_top_long_lines){
        String[] files_list = new File(UploadedFilesProperties.getLocation()).list();
        ArrayList<String> text_files = new ArrayList<String>();
        // map: line-length : file-name : line-text
        //Map<Integer, Map<String, ArrayList<String>>> mixed_long_lines = new HashMap<Integer, Map<String, ArrayList<String>>>();
        int top_cut = n_top_long_lines;
        Map<Integer, HashMap<String, String>> mixed_long_lines = new HashMap<Integer, HashMap<String,String>>();
        File file;
        Stream<String> lines = null;
        Set<Integer> keySet;
        List<Integer> keys_to_look_for;
        List<Integer> sortedKeys;
        List<Long> lines_to_look_for;
        for (String file_name : files_list)
            if(file_name.endsWith(".txt")) {
                text_files.add(file_name);
                if (!this.files_line_info.containsKey(file_name))
                    process_file(file_name);

                // get the top 100 longest lines for each file
                file = new File(UploadedFilesProperties.getLocation()+"/"+file_name);
                keySet= this.files_line_info.get(file_name).keySet();
                sortedKeys= keySet.stream().sorted().collect(Collectors.toList());
                n_top_long_lines = top_cut;
                if (n_top_long_lines > sortedKeys.size()) n_top_long_lines = sortedKeys.size();
                keys_to_look_for = sortedKeys.subList(sortedKeys.size()-n_top_long_lines, sortedKeys.size());
                lines_to_look_for = new ArrayList<Long>();
                for (Integer key : keys_to_look_for)
                    lines_to_look_for.add(this.files_line_info.get(file_name).get(key).get(0));

                for (long line_number : lines_to_look_for){
                    try {
                        lines = Files.lines(file.toPath());
                        String line = lines.skip(line_number).findFirst().get();
                        Integer line_length = line.length();
                        //mixed_long_lines.put(line.length(), HashMap.Entry(file_name,line));
                        if (mixed_long_lines.containsKey(line_length)){
                            mixed_long_lines.get(line_length).put(file_name, line);
                        }
                        else {
                            mixed_long_lines.put(line_length, new HashMap<String, String>());
                            mixed_long_lines.get(line_length).put(file_name,line);
                        }

                    } catch (IOException e) { e.printStackTrace(); }
                }
            }


        keySet= mixed_long_lines.keySet();
        sortedKeys= keySet.stream().sorted().collect(Collectors.toList());
        if (top_cut > sortedKeys.size()) top_cut = sortedKeys.size();
        keys_to_look_for = sortedKeys.subList(sortedKeys.size()-top_cut, sortedKeys.size());
        mixed_long_lines.keySet().retainAll(keys_to_look_for);

        return mixed_long_lines;
    }

    //MediaType.APPLICATION_JSON_VALUE
    @GetMapping(value = "/application")
    public String simple_randomLine(){
        return oneRandomLine()[0];
    }


    public String[] oneRandomLine(){
        String[] files_list = new File(UploadedFilesProperties.getLocation()).list();
        ArrayList<String> text_files = new ArrayList<String>();
        for (String file_name : files_list)
            if(file_name.endsWith(".txt"))
                text_files.add(file_name);
        File random_file = new File(UploadedFilesProperties.getLocation()+"/"+text_files.get(new Random().nextInt(text_files.size())));
        //System.out.println("random file name: "+random_file.getName());
        Stream<String> random_file_lines = null;
        long total_lines=0;
        try {
            random_file_lines = Files.lines(random_file.toPath());
            total_lines = random_file_lines.count();
            random_file_lines = Files.lines(random_file.toPath());
        } catch (IOException e) {e.printStackTrace();}
        Integer line_number = new Random().nextInt(Math.toIntExact(total_lines));
        String randomLine = random_file_lines.skip(line_number).findFirst().get();
        String[] response = {randomLine, random_file.getName(), line_number+""};
        return response;
    }

    @GetMapping("/application/*")
    public RandomLineResponse detailed_randomLine(){
        Map<String, Integer> countChars = new HashMap<String, Integer>();
        String[] randomLineRes =oneRandomLine();
        String randomLine = randomLineRes[0];
//        for(char c : randomLine.toCharArray()){
//            if (countChars.containsKey(c+""))
//                countChars.put(c+"", countChars.get(c+"")+1);
//            else
//                countChars.put(c+"", 1);
//        }

        // count the characters in the line
        String lower_case_line = randomLine.toLowerCase();
        ArrayList<Integer> count_letters = new ArrayList<Integer>(26);
        // 97 - 122
        for (int i=0; i<26;i++)
            count_letters.add(i, StringUtils.countOccurrencesOf(lower_case_line, ""+(char)(i+97)));

        int highest_count = Collections.max(count_letters);
        int char_code_point = count_letters.indexOf(highest_count);
        char most_frequent_char = (char) (char_code_point+97);
        if (highest_count==0) most_frequent_char=' ';

        return new RandomLineResponse(randomLine, randomLineRes[1], Integer.parseInt(randomLineRes[2]),most_frequent_char);
    }

    @GetMapping("/application/backwards")
    public String oneRandomBackwards(){
        return new StringBuilder(oneRandomLine()[0]).reverse().toString();
    }




}

