package com.tugalsan.trm.changefiledate;

import com.tugalsan.api.cast.client.*;
import com.tugalsan.api.file.server.*;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.random.server.*;
import com.tugalsan.api.time.client.*;
import java.nio.file.*;

//WHEN RUNNING IN NETBEANS, ALL DEPENDENCIES SHOULD HAVE TARGET FOLDER!
public class Main {

    final private static TS_Log d = TS_Log.of(Main.class.getSimpleName());

    /*
        for filenames named //FILENAME DD-MM-YYYY
        this func re-dates them and randomize time.
        example input params
        Path.of("C:", "me", "desk", "PDF")
        HOUR btw 18-24
        MIN btw 18-24
        SEC btw 18-24
     */
    public static void randomizeTime(Path directory, int hourMin, int hourMax, int minMin, int minMax, int secMin, int secMax) {
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
        var date = TGS_Time.toDate(split[1]);
        if (date == null) {
            d.ce("getDateFromFileName_end_normal", "Skipped -> date in filename cannot be used: " + fileLabel);
            return;
        }
        getDateFromFileName_end_normal2(file, date.setHour(hour).setMinute(min).setSecond(sec));
    }

    private static void getDateFromFileName_end_normal2(Path file, TGS_Time dateAndtime) {
        TS_FileUtils.setTimeCreationTime(file, dateAndtime);
        TS_FileUtils.setTimeLastModified(file, dateAndtime);
        TS_FileUtils.setTimeAccessTime(file, dateAndtime);
        TS_FileUtils.rename(file, TS_FileUtils.getNameLabel(file).split(" ")[0] + "." + TS_FileUtils.getNameType(file));
        d.cr("getDateFromFileName_end_normal2", "done", file);
    }
}
