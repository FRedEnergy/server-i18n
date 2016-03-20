package ru.skymine.i18n;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks that mods need a locale files
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Localized {}
