package com.example.parsaBadiei;

import java.util.Map;

public class text_file_response {
    private String fileName;
    private long total_lines;
    private long longest_line_num;
    private String longest_line_value;
    private Map<Long, String> long_lines;

    public text_file_response(String fileName, long total_lines, long longest_line_num,
                              String getLongest_line_value, Map<Long, String> long_lines){
        this.fileName=fileName;
        this.total_lines=total_lines;
        this.longest_line_num=longest_line_num;
        this.longest_line_value=getLongest_line_value;
        this.long_lines=long_lines;
    }

    public String getFileName() {
        return fileName;
    }

    public long getTotal_lines() {
        return total_lines;
    }

    public long getLongest_line_num() {
        return longest_line_num;
    }

    public String getLongest_line_value() {
        return longest_line_value;
    }

    public Map<Long, String> getLong_lines() {
        return long_lines;
    }
}
