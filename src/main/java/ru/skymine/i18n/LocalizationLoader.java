package ru.skymine.i18n;

import com.google.common.base.Charsets;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.commons.compress.compressors.FileNameUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

@Localized
@Mod(modid = "server-i18n", acceptableRemoteVersions = "*")
public class LocalizationLoader {

    public static final String LANG_DIR_PATH = "lang/";

    @Mod.Instance("server-i18n")
    public static LocalizationLoader instance;

    @Mod.EventHandler
    public void load(FMLPreInitializationEvent event) throws IOException {
        Configuration config = new Configuration(new File(event.getModConfigurationDirectory(), "localization.cfg"));
        String defLocale = config.getString("Default Locale", "Localization", "ru", "Default locale for mods");
        config.save();
        SI18n.setDefaultLocale(defLocale);
        List<FMLModContainer> localized = Loader.instance().getModList()
                .stream()
                .filter(FMLModContainer.class::isInstance)
                .map(FMLModContainer.class::cast)
                .filter(it -> it.getMod().getClass().isAnnotationPresent(Localized.class))
                .collect(Collectors.toList());
        localized.stream()
                .filter(it -> it.getSource().isDirectory())
                .forEach(it -> extractLocalesFrom(it.getMod().getClass()));
        localized.stream()
                .filter(it -> it.getSource().isFile())
                .forEach(it -> extractLocalesFrom(it.getSource()));
    }

    private void extractLocalesFrom(File source){
        try(JarFile file = new JarFile(source)) {
            Enumeration<JarEntry> entries = file.entries();
            while(entries.hasMoreElements()){
                JarEntry entry = entries.nextElement();
                if(entry.getName().startsWith(LANG_DIR_PATH)){
                    try(InputStreamReader reader = new InputStreamReader(file.getInputStream(entry))) {
                        SI18n.injectLocale(FilenameUtils.getBaseName(entry.getName()), reader);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void extractLocalesFrom(Class clazz){
        try(InputStream clspthResources = clazz.getClassLoader().getResourceAsStream(LANG_DIR_PATH)) {
            List<String> files = IOUtils.readLines(clspthResources, Charsets.UTF_8);
            for (String file : files) {
                InputStreamReader data = new InputStreamReader(clazz.getClassLoader().getResourceAsStream(LANG_DIR_PATH + file));
                SI18n.injectLocale(FilenameUtils.getBaseName(file), data);
                data.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
