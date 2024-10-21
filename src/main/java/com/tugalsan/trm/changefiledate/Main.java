package com.tugalsan.trm.changefiledate;

import com.tugalsan.api.cast.client.*;
import com.tugalsan.api.file.server.*;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.random.server.*;
import com.tugalsan.api.time.client.*;
import java.nio.file.*;

//WHEN RUNNING IN NETBEANS, ALL DEPENDENCIES SHOULD HAVE TARGET FOLDER!
//cd C:\me\codes\com.tugalsan\trm\com.tugalsan.trm.changefiledate
//java --enable-preview --add-modules jdk.incubator.vector -jar target/com.tugalsan.trm.changefiledate-1.0-SNAPSHOT-jar-with-dependencies.jar    
public class Main {

    final private static TS_Log d = TS_Log.of(true, Main.class);

    public static void main(String... args) {
        TS_DirectoryUtils.subDirectories(Path.of("\\192.168.7.1"), true, false).forEach(s -> {
            d.cr("a", s);
        });

//        var dir = Path.of("C:", "Users", "me", "Desktop", "PDF");
//        randomizeTime_start_with_date(
//                dir,
//                10, 17,
//                0, 59,
//                0, 59
//        );
//        randomizeTime_end_normal(
//                dir,
//                18, 24,
//                0, 59,
//                0, 59
//        );
    }

    //For filenames named "LABEL DD-MM-YYYY", this func re-dates them and randomize time.
    private static void randomizeTime_start_with_date(Path directory, int hourMin, int hourMax, int minMin, int minMax, int secMin, int secMax) {
        if (!TS_DirectoryUtils.isExistDirectory(directory)) {
            d.ce("run", "dir not exists");
            return;
        }
        TS_DirectoryUtils.subFiles(directory, null, true, false).forEach(file -> {
            getDateFromFileName_start_with_date(file,
                    TS_RandomUtils.nextInt(hourMin, hourMax),
                    TS_RandomUtils.nextInt(minMin, minMax),
                    TS_RandomUtils.nextInt(secMin, secMax)
            );
        });
    }

    //For filenames named "LABEL DD-MM-YYYY", this func re-dates them and randomize time.
    private static void randomizeTime_end_normal(Path directory, int hourMin, int hourMax, int minMin, int minMax, int secMin, int secMax) {
        if (!TS_DirectoryUtils.isExistDirectory(directory)) {
            d.ce("run", "dir not exists");
            return;
        }
        TS_DirectoryUtils.subFiles(directory, "*.pdf", false, false).forEach(file -> {
            getDateFromFileName_end_normal(file,
                    TS_RandomUtils.nextInt(hourMin, hourMax),
                    TS_RandomUtils.nextInt(minMin, minMax),
                    TS_RandomUtils.nextInt(secMin, secMax)
            );
        });
    }

    private static void getDateFromFileName_end_normal(Path file, int hour, int min, int sec) {
        var fileLabel = TS_FileUtils.getNameLabel(file);
        var split = fileLabel.split(" ");
        if (split.length != 2) {//FILENAME XX-XX-XXXX
            d.ce("getDateFromFileName_end_normal", "Skipped -> filename not proper: " + fileLabel);
            return;
        }
        var dd = split[1].substring(0, 2);
        var MM = split[1].substring(3, 5);
        var yyyy = split[1].substring(6);
        var intDD = TGS_CastUtils.toInteger(dd);
        var intMM = TGS_CastUtils.toInteger(MM);
        var intYYY = TGS_CastUtils.toInteger(yyyy);
        if (intDD == null || intMM == null || intYYY == null) {
            d.ce("getDateFromFileName_end_normal", "Skipped -> date in filename not proper: " + fileLabel);
            return;
        }
        var date = TGS_Time.ofDate_D_M_Y(split[1]);
        if (date == null) {
            d.ce("getDateFromFileName_end_normal", "Skipped -> date in filename cannot be used: " + fileLabel);
            return;
        }
        getDateFromFileName_end_normal2(file, date.setHour(hour).setMinute(min).setSecond(sec));
    }

    private static void getDateFromFileName_start_with_date(Path file, int hour, int min, int sec) {
        var fileLabel = TS_FileUtils.getNameLabel(file);
        var split = fileLabel.split(" ");
        if (split.length < 2) {//FILENAME XX-XX-XXXX
            d.ce("getDateFromFileName_start_with_date", "Skipped -> filename not proper: " + fileLabel);
            return;
        }
        var yyyy = split[0].substring(0, 4);
        d.ci("getDateFromFileName_start_with_date", "yyyy", yyyy);
        var MM = split[0].substring(5, 7);
        d.ci("getDateFromFileName_start_with_date", "MM", MM);
        var dd = split[0].substring(8, 9);
        d.ci("getDateFromFileName_start_with_date", "dd", dd);
        var intYYY = TGS_CastUtils.toInteger(yyyy);
        var intMM = TGS_CastUtils.toInteger(MM);
        var intDD = TGS_CastUtils.toInteger(dd);
        if (intYYY == null) {
            d.ce("getDateFromFileName_start_with_date", "Skipped -> date in filename not proper: " + fileLabel, "intYYY == null", yyyy);
            return;
        }
        if (intDD == null) {
            d.ce("getDateFromFileName_start_with_date", "Skipped -> date in filename not proper: " + fileLabel, "intDD == null", dd);
            return;
        }
        if (intMM == null) {
            d.ce("getDateFromFileName_start_with_date", "Skipped -> date in filename not proper: " + fileLabel, "intMM == null", MM);
            return;
        }
        var date = TGS_Time.ofDate_YYYY_MM_DD(split[0]);
        if (date == null) {
            d.ce("getDateFromFileName_start_with_date", "Skipped -> date in filename cannot be used: " + fileLabel);
            return;
        }
        getDateFromFileName_start_with_date(file, date.setHour(hour).setMinute(min).setSecond(sec));
    }

    private static void getDateFromFileName_end_normal2(Path file, TGS_Time dateAndtime) {
        TS_FileUtils.setTimeCreationTime(file, dateAndtime);
        TS_FileUtils.setTimeLastModified(file, dateAndtime);
        TS_FileUtils.setTimeAccessTime(file, dateAndtime);
        TS_FileUtils.rename(file, TS_FileUtils.getNameLabel(file).split(" ")[0] + "." + TS_FileUtils.getNameType(file));
        d.cr("getDateFromFileName_end_normal2", "done", file);
    }

    private static void getDateFromFileName_start_with_date(Path file, TGS_Time dateAndtime) {
        TS_FileUtils.setTimeCreationTime(file, dateAndtime);
        TS_FileUtils.setTimeLastModified(file, dateAndtime);
        TS_FileUtils.setTimeAccessTime(file, dateAndtime);
        d.cr("getDateFromFileName_start_with_date", "done", file);
    }
}
