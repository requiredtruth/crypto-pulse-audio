package com.cryptopulse.app;
import android.content.*;import org.json.*;import java.util.*;
public class MigrationTest {
 static int checks;static void check(boolean x,String m){checks++;if(!x)throw new AssertionError(m);}
 static JSONObject item(String source,String url)throws Exception{return new JSONObject().put("source",source).put("url",url);}
 static JSONObject pending(String type,String text)throws Exception{return new JSONObject().put("type",type).put("text",text);}
 public static void main(String[]args)throws Exception {
  Context c=new Context();SharedPreferences p=Store.p(c);p.edit().putInt("schema",2).putInt("newsSeconds",240).putInt("rank_CoinDesk",7).putInt("rank_CNBC Markets",2).putBoolean("source_MarketWatch",true).putString("etag:https://search.cnbc.com/feed","old").putString("voice","chosen-voice").putString("lastSpoken","BTC up 0.00%").apply();
  Store.save(c,"stories",new JSONArray().put(item("CNBC Markets","https://cnbc.com/a")).put(item("MarketWatch","https://example.org/syndicated")).put(item("CoinDesk","https://coindesk.com/a")));
  Store.save(c,"seen",new JSONArray().put(item("","https://marketwatch.com/a")).put(item("","https://coindesk.com/a")));
  Store.save(c,"customSources",new JSONArray().put(new JSONObject().put("name","Custom: News").put("url","https://cnbc.com/feed")).put(new JSONObject().put("name","Custom: MarketWatch").put("url","https://example.org/feed")).put(new JSONObject().put("name","Custom: Good").put("url","https://example.org/feed")));
  Store.save(c,"pending",new JSONArray().put(pending("price_BTC","BTC up 0.00%")).put(pending("price_ETH","ETH down 0.13%")).put(pending("digest","BTC up 0.00%. ETH down 0.00%.")).put(pending("news","MarketWatch reports. Old story")).put(pending("news","CoinDesk reports. Good story")));
  Store.save(c,"log",new JSONArray().put("123|CNBC Markets reports. Old").put("124|BTC down 0.00%").put("125|CoinDesk reports. Good"));
  Store.migrate(c);check(Store.number(c,"schema",0)==3,"upgrade version marked");check(Store.number(c,"newsSeconds",0)==240,"news interval retained");check(Store.number(c,"rank_CoinDesk",0)==7,"retained source ranking");check(Store.text(c,"voice","").equals("chosen-voice"),"voice preference retained");check(!p.getAll().containsKey("rank_CNBC Markets")&&!p.getAll().containsKey("source_MarketWatch"),"excluded publisher prefs removed");check(!p.getAll().containsKey("etag:https://search.cnbc.com/feed"),"removed publisher validators removed");
  check(Store.array(c,"stories").length()==1&&Store.array(c,"stories").getJSONObject(0).getString("source").equals("CoinDesk"),"removed publisher history purged");check(Store.array(c,"seen").length()==1,"removed publisher dedup record purged");check(Store.array(c,"customSources").length()==1,"blocked custom URL and name purged");check(Store.sources(c).size()==10,"nine built-in feeds plus retained custom");check(Store.array(c,"pending").length()==1&&Store.array(c,"pending").getJSONObject(0).getString("text").startsWith("CoinDesk"),"stale price/digest and excluded news queue purged");check(Store.array(c,"log").length()==1,"old zero and removed publisher logs purged");check(!p.getAll().containsKey("lastSpoken"),"old last-spoken status reset");
  Store.save(c,"pending",new JSONArray().put(pending("price_BTC","BTC up 0.13%")));Store.migrate(c);check(Store.array(c,"pending").length()==1,"migration is idempotent and preserves new speech");
  Context fresh=new Context();Store.migrate(fresh);check(Store.number(fresh,"schema",0)==3&&Store.number(fresh,"newsSeconds",0)==60,"fresh install defaults correct");check(Store.sources(fresh).size()==9,"fresh catalog has nine feeds");
  System.out.println("PASS: "+checks+" upgrade migration checks");
 }
}
