-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# NavDestination.values accessed reflectively via rememberSaveable.
-keep class codes.chrishorner.reverserainbow.ui.screens.NavDestination { *; }

-dontwarn androidx.test.platform.app.InstrumentationRegistry

# androidx.window references optional system extensions reflectively.
-dontwarn androidx.window.extensions.area.ExtensionWindowAreaPresentation
-dontwarn androidx.window.extensions.core.util.function.Consumer
-dontwarn androidx.window.extensions.core.util.function.Function
-dontwarn androidx.window.extensions.core.util.function.Predicate