package com.pb8;

import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


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
    }
}
