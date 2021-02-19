package org.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class LogToFile {
    static void LogToFile(String text) {
        Date data = new java.util.Date();
        try (
                FileWriter fw = new FileWriter("LogFile.txt", true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {
            out.println(data + " " + text);

        } catch (
                IOException e) {
        }
    }
}
