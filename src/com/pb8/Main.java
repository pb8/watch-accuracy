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
        } catch (IllegalArgumentException e) {
            System.out.println(argMsg);
            return;
        }

        Integer delta = (int)((watchTime.getMillis() - systemTime.getMillis()) / 1000);

        // todo: refactor this into TimeKeeper
        DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm:ss");
        System.out.println(String.format("System: %s\t Watch: %s\t Delta: %ds", fmt.print(systemTime), fmt.print(watchTime), delta));

        TimeKeeper timeKeeper = new TimeKeeper();
        timeKeeper.logTimecheck(new TimeKeeper.Timecheck(systemTime, delta, -1)); //todo
    }
}

class TimeKeeper {
    private String logfile;

    public TimeKeeper() {
        logfile = "timelog.csv";
    }

    public TimeKeeper(String _logfile) {
        logfile = _logfile;
    }

    public void logTimecheck(Timecheck timecheck) {
        try {
            File file = new File(logfile);
            boolean isNew = !file.exists();
            if (isNew) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            if (isNew) {
                pw.println("Date,Time,Delta,DailyChange");
            }
            pw.println(timecheck.toString());
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadHistory(int n) {
        try {
            File file = new File(logfile);
            if (!file.exists()) {
                return;
            }
            BufferedReader br = new BufferedReader(new FileReader(file));

            for (String line; (line = br.readLine()) != null; ) {
                // todo: stuff it into a TimeDelta collection
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Timecheck {
        public DateTime timestamp;
        public Integer delta;
        public Integer dailyChange;

        public Timecheck(DateTime _timestamp, Integer _delta, Integer _dailyChange) {
            timestamp = _timestamp;
            delta = _delta;
            dailyChange = _dailyChange;
        }

        public String toString(){
            DateTimeFormatter fmtDate = DateTimeFormat.forPattern("yyyy-MM-dd");
            DateTimeFormatter fmtTime = DateTimeFormat.forPattern("HH:mm:ss");
            return String.format("%s,%s,%d,%d", fmtDate.print(timestamp), fmtTime.print(timestamp), delta, dailyChange);
        }
    }
}

