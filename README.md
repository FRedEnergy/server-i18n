# server-i18n
Extensions which allows server side modules to have it's own localization files

##Usage

####Configuring

At first you need to mark that your mod requires server side localtization.
To do this simply add `@Localized`annotation to your main mod class
After that you class should look like this
```
//acceptableRemoteVersions needed to indicate that your mod doesn't require any clint side modification
@Localized
@Mod(modid = "example", acceptableRemoteVersions="*") 
public class ExampleMod {
...
 //events and logic
...
}
```

After that you must create `lang` folder into your `resources` directory. In this folder you should put all your *.lang files which would be injected into I18n registry

That how it looks

```
|-- src
    |-- main
        |-- resources
            |-- lang
                |-- en.lang
                |-- ru.lang
                |-- fr.lang
```

####Working with localized strings
```
String localized = SI18n.get("key") //to access localized string by key
String localizedFormatted = SI18n.get("key.formatted", 3.14f) //you can provided arguments for formatting after key
String localizedSpecialLocale = SI18n.getL("fr", "key") //explicitly specifying locale 
```
            
