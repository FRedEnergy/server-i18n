package ru.skymine.i18n;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.apache.commons.io.FilenameUtils;

import java.io.*;

/**
 * Server side localization
 *
 * 'Cause MC Lang API isn't accessible on server side, we must use this
 */
public class SI18n {

    private static String defaultLocale = "en";
    private static final Table<String, String, String> localizations = HashBasedTable.create();

    public static void findAndInjectLocales() throws IOException {
        findAndInjectLocales(LocalizationLoader.LANG_DIR);
    }

    public static void findAndInjectLocales(File dir) throws IOException {
        if(dir.isDirectory() && dir.exists()){
            for(File file: dir.listFiles())
                if(file.isFile() && file.getName().endsWith(".lang")) {
                    injectLocale(FilenameUtils.getBaseName(file.getAbsolutePath()), new FileReader(file));
                }
        } else {
            dir.mkdir();
        }
    }

    public static void injectLocale(String locale, Reader data) throws IOException {
        BufferedReader reader = new BufferedReader(data);
        reader.lines()
                .filter(it -> !it.startsWith("#"))
                .filter(it -> it.contains("="))
                .map(it -> it.split("=", 2))
                .forEach(it -> localizations.put(locale, it[0], it[1]));
    }

    public static String get(String key, Object ... data){
        return get(defaultLocale, key, data);
    }

    public static String get(String locale, String key, Object ... data){
        if(localizations.contains(locale, key)) {
            return String.format(localizations.get(locale, key), data);
        } else {
            return key;
        }
    }

    public static String getDefaultLocale() {
        return defaultLocale;
    }

    public static void setDefaultLocale(String defaultLocale) {
        SI18n.defaultLocale = defaultLocale;
    }

    public static Table<String, String, String> getLocalizations() {
        return localizations;
    }
}
