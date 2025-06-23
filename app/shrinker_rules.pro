-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# NavDestination.values accessed reflectively via rememberSaveable.
-keep class codes.chrishorner.planner.ui.screens.NavDestination { *; }