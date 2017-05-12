package com.pb8;

import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.*;


public class Main {

    public static void main(String[] args) {
        String argMsg = "One argument expected: watch time as HH:MM:SS";
        if (args.length != 1) {
            System.out.println(argMsg);
            return;
        }

        DateTime systemTime = new DateTime(); // first snap the system time

        String[] userInput = args[0].split(":");
        if (userInput.length != 3) {
            System.out.println(argMsg);
            return;
        }

        DateTime watchTime;
        try {
            watchTime = new DateTime().withTime(
                    Integer.parseInt(userInput[0]),
                    Integer.parseInt(userInput[1]),
                    Integer.parseInt(userInput[2]), 0);
        } catch (IllegalArgumentException e){
            System.out.println(argMsg);
            return;
        }

        long delta = (watchTime.getMillis() - systemTime.getMillis()) / 1000;

        DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm:ss");
        System.out.println(String.format("System: %s\t Watch: %s\t Delta: %ds", fmt.print(systemTime), fmt.print(watchTime), delta));

        logTimecheck(systemTime, delta);
    }

    public static void logTimecheck(DateTime systemTime, long delta) {
        try {
            File file = new File("timelog.csv");
            boolean isNew = !file.exists();
            if(isNew) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file,true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            if(isNew){
                pw.println("Date,Time,Delta");
            }

            DateTimeFormatter fmtDate = DateTimeFormat.forPattern("yyyy-MM-dd");
            DateTimeFormatter fmtTime = DateTimeFormat.forPattern("HH:mm:ss");
            pw.println(String.format("%s,%s,%d", fmtDate.print(systemTime), fmtTime.print(systemTime), delta));

            pw.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
