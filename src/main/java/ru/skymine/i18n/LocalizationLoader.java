package ru.skymine.i18n;

import com.google.common.base.Charsets;
import net.minecraftforge.fml.common.FMLModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;

@Localized
@Mod(modid = "server-i18n", acceptableRemoteVersions = "*")
public class LocalizationLoader {

    public static final String LANG_DIR_PATH = "lang/";
    public static final File LANG_DIR = new File(LANG_DIR_PATH);

    @Mod.Instance("server-i18n")
    public static LocalizationLoader instance;

    @Mod.EventHandler
    public void load(FMLPreInitializationEvent event) throws IOException {
        LANG_DIR.mkdir();
        Loader.instance().getModList()
                .stream()
                .filter(FMLModContainer.class::isInstance)
                .map(FMLModContainer.class::cast)
                .map(it -> it.getMod().getClass())
                .filter(it -> it.isAnnotationPresent(Localized.class))
                .forEach(this::extractLocalesFrom);
        SI18n.findAndInjectLocales();
    }

    private void extractLocalesFrom(Class clazz){
        try {
            String modid = ((Mod)clazz.getAnnotation(Mod.class)).modid();
            List<String> files = IOUtils.readLines(clazz.getClassLoader().getResourceAsStream(LANG_DIR_PATH), Charsets.UTF_8);
            for (String file : files) {
                InputStream data = clazz.getClassLoader().getResourceAsStream(LANG_DIR_PATH + file);
                File destinationDir = new File(LANG_DIR + File.separator + modid);
                File destination = new File(destinationDir, file);
                if(destination.exists()) {
                    continue;
                } else {
                    destinationDir.mkdir();
                    destination.createNewFile();
                }
                FileUtils.copyInputStreamToFile(data, destination);
                data.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
