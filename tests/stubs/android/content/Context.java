package android.content;
/** JVM-only minimal Android preference context; never compiled into the APK. */
public class Context {private final SharedPreferences prefs=new SharedPreferences();public SharedPreferences getSharedPreferences(String name,int mode){return prefs;}}
