package android.content;
import java.util.*;
/** JVM-only in-memory preference adapter for upgrade regression testing. */
public class SharedPreferences {
 final Map<String,Object> values=new HashMap<>();public Map<String,?> getAll(){return new HashMap<>(values);}
 public int getInt(String key,int d){return (Integer)values.getOrDefault(key,d);}public long getLong(String key,long d){return (Long)values.getOrDefault(key,d);}public boolean getBoolean(String key,boolean d){return (Boolean)values.getOrDefault(key,d);}public String getString(String key,String d){return (String)values.getOrDefault(key,d);}
 @SuppressWarnings("unchecked") public Set<String> getStringSet(String key,Set<String>d){return new HashSet<>((Set<String>)values.getOrDefault(key,d));}
 public Editor edit(){return new Editor();}public class Editor {final Map<String,Object> changed=new HashMap<>();final Set<String> deleted=new HashSet<>();public Editor putInt(String k,int v){changed.put(k,v);return this;}public Editor putLong(String k,long v){changed.put(k,v);return this;}public Editor putBoolean(String k,boolean v){changed.put(k,v);return this;}public Editor putString(String k,String v){changed.put(k,v);return this;}public Editor putStringSet(String k,Set<String>v){changed.put(k,new HashSet<>(v));return this;}public Editor remove(String k){deleted.add(k);return this;}public void apply(){for(String k:deleted)values.remove(k);values.putAll(changed);}}
}
