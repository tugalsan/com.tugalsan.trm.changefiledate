package com.tugalsan.trm.changefiledate;

import com.tugalsan.api.cast.client.*;
import com.tugalsan.api.file.server.*;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.random.server.*;
import com.tugalsan.api.time.client.*;
import java.nio.file.*;
import java.util.Comparator;

//WHEN RUNNING IN NETBEANS, ALL DEPENDENCIES SHOULD HAVE TARGET FOLDER!
//cd C:\me\codes\com.tugalsan\trm\com.tugalsan.trm.changefiledate
//java --enable-preview --add-modules jdk.incubator.vector -jar target/com.tugalsan.trm.changefiledate-1.0-SNAPSHOT-jar-with-dependencies.jar    
public class Main {

    final private static TS_Log d = TS_Log.of(true, Main.class);

    public static void main(String... args) {
        var dir = Path.of("\\\\10.0.0.222\\ofis_tarama\\m\\_yeni");
        int hourMin = 10, hourMax = 17;
        TS_DirectoryUtils.subDirectories(dir, true, true).forEach(subDir -> {
            var subDirName = TS_DirectoryUtils.getName(subDir);
            var idx = subDirName.lastIndexOf(" ");
            if (idx == -1) {
                return;
            }
            var dateStr = subDirName.substring(idx + 1);
            if (!TGS_Time.isDateReversed(dateStr)) {
                return;
            }
            var date = TGS_Time.ofDate_YYYY_MM_DD(dateStr);
            if (date == null) {
                d.cr("main", "date false positive", subDir);
                return;
            }
            d.cr("main", "date detected", date.toString_YYYY_MM_DD(), subDir);
            TS_DirectoryUtils.subFiles(dir, null, true, true).forEach(subFile -> {
                if (TS_FileUtils.getTimeLastModified(subFile).hasEqualDateWith(date)) {
                    return;
                }
                var dateAndtime = date.cloneIt();
                dateAndtime.setHour(TS_RandomUtils.nextInt(hourMin, hourMax));
                dateAndtime.setMinute(TS_RandomUtils.nextInt(0, 59));
                dateAndtime.setSecond(TS_RandomUtils.nextInt(0, 59));
                TS_FileUtils.setTimeCreationTime(subFile, dateAndtime);
                TS_FileUtils.setTimeLastModified(subFile, dateAndtime);
                TS_FileUtils.setTimeAccessTime(subFile, dateAndtime);
                d.cr("main", "subFile set", subFile);
            });
            if (TS_DirectoryUtils.getTimeLastModified(subDir).hasEqualDateWith(date)) {
                return;
            }
            var dateAndtime = TS_DirectoryUtils.subFiles(dir, null, true, true).stream()
                    .map(subFile -> TS_FileUtils.getTimeCreationTime(subFile))
                    .max(Comparator.comparing(TGS_Time::getTime))
                    .orElseThrow();
            dateAndtime.setHour(TS_RandomUtils.nextInt(hourMin, hourMax));
            dateAndtime.setMinute(TS_RandomUtils.nextInt(0, 59));
            dateAndtime.setSecond(TS_RandomUtils.nextInt(0, 59));
            TS_DirectoryUtils.setTimeCreationTime(subDir, dateAndtime);
            TS_DirectoryUtils.setTimeLastModified(subDir, dateAndtime);
            TS_DirectoryUtils.setTimeAccessTime(subDir, dateAndtime);

        });
        TS_DirectoryUtils.subFiles(dir, null, true, true).forEach(subFile -> {
            var subFileName = TS_FileUtils.getNameLabel(subFile);
            var idx = subFileName.lastIndexOf(" ");
            if (idx == -1) {
                d.cr("main", "skip idx == -1", subFile);
                return;
            }
            var dateStr = subFileName.substring(idx + 1);
            if (!TGS_Time.isDateReversed(dateStr)) {
                d.cr("main", "0", dateStr.indexOf('.', 0));
                d.cr("main", "5", dateStr.indexOf('.', 5));
                d.cr("main", "TGS_Time.isDate", TGS_Time.isDate(dateStr));
                d.cr("main", "skip !TGS_Time.isDateReversed(dateStr)", subFile);
                return;
            }
            var date = TGS_Time.ofDate_YYYY_MM_DD(dateStr);
            if (date == null) {
                d.cr("main", "date false positive", subFile);
                return;
            }
            d.cr("main", "date detected", date.toString_YYYY_MM_DD(), subFile);
            if (TS_FileUtils.getTimeLastModified(subFile).hasEqualDateWith(date)) {
                return;
            }
            var dateAndtime = date.cloneIt();
            dateAndtime.setHour(TS_RandomUtils.nextInt(hourMin, hourMax));
            dateAndtime.setMinute(TS_RandomUtils.nextInt(0, 59));
            dateAndtime.setSecond(TS_RandomUtils.nextInt(0, 59));
            TS_FileUtils.setTimeCreationTime(subFile, dateAndtime);
            TS_FileUtils.setTimeLastModified(subFile, dateAndtime);
            TS_FileUtils.setTimeAccessTime(subFile, dateAndtime);
            d.cr("main", "subFile set", subFile);
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
