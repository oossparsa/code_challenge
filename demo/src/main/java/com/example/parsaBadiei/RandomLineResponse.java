package com.example.parsaBadiei;

public class RandomLineResponse {
    private String line;
    private String fileName;
    private long line_number;
    private char mostFrequentChar;

    public RandomLineResponse(String line, String fileName, long line_number, char mostFrequentChar){
        this.line = line;
        this.fileName = fileName;
        this.line_number = line_number;
        this.mostFrequentChar = mostFrequentChar;
    }

    public String getLine() {
        return line;
    }

    public String getFileName() {
        return fileName;
    }

    public long getLine_number() {
        return line_number;
    }

    public char getMostFrequentChar() {
        return mostFrequentChar;
    }
}
