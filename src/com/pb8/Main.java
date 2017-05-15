package com.pb8;

import org.joda.time.*;
import org.joda.time.format.*;

import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        String argMsg = "One argument expected: watch time as HH:mm:ss";
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

        double delta = (watchTime.getMillis() - systemTime.getMillis()) / 1000;

        TimeKeeper timeKeeper = new TimeKeeper();
        TimeKeeper.Timecheck timecheck = timeKeeper.createTimecheck(systemTime, delta);
        timeKeeper.logTimecheck(timecheck);

        DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm:ss");
        System.out.println(String.format("System: %s\tWatch: %s\tDelta: %.0fs\tAvg: %.1fs", fmt.print(systemTime), fmt.print(watchTime), delta, timecheck.avgDelta));
    }
}

class TimeKeeper {
    private String logfile;
    private List<Timecheck> timecheckHist = new ArrayList<>();

    public TimeKeeper() {
        logfile = "timelog.csv";
        loadHistory();
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
                pw.println("Date|Time|Delta|AvgDelta");
            }
            pw.println(timecheck.toString());
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadHistory() {
        if(!timecheckHist.isEmpty()){
            timecheckHist.clear();
        }
        try {
            File file = new File(logfile);
            if (!file.exists()) {
                return;
            }

            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
            BufferedReader br = new BufferedReader(new FileReader(file));
            for (String line; (line = br.readLine()) != null; ) {
                String[] row = line.split("\\|");
                if(row[0].equals("Date")) continue; // skip header
                DateTime ts = formatter.parseDateTime(row[0] + " " + row[1]);
                Double delta = Double.parseDouble(row[2]);
                Double dailyChange = Double.parseDouble(row[3]);
                Timecheck t = new Timecheck(ts, delta, dailyChange);
                timecheckHist.add(t);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Timecheck createTimecheck(DateTime systemTime, double delta) {
        // calculate a 5-period rolling avg of delta
        double avgDelta = 0;
        int n = timecheckHist.size();
        if(n == 0 || timecheckHist.get(n-1).avgDelta == -1){
            avgDelta = delta;
        }
        else {
            double sumDelta = 0;
            for(int i = (n >= 4 ? n-4 : 0); i < n; i++){
                sumDelta += timecheckHist.get(i).delta;
            }
            avgDelta = (sumDelta + delta) / (n < 4 ? n + 1 : 5);
        }

        return new Timecheck(systemTime, delta, avgDelta);
    }

    public static class Timecheck {
        public DateTime timestamp;
        double delta;
        double avgDelta;

        public Timecheck(DateTime _timestamp, double _delta, double _avgDelta) {
            timestamp = _timestamp;
            delta = _delta;
            avgDelta = _avgDelta;
        }

        public String toString(){
            DateTimeFormatter fmtDate = DateTimeFormat.forPattern("yyyy-MM-dd");
            DateTimeFormatter fmtTime = DateTimeFormat.forPattern("HH:mm:ss");
            return String.format("%s|%s|%.0f|%.1f", fmtDate.print(timestamp), fmtTime.print(timestamp), delta, avgDelta);
        }
    }
}

