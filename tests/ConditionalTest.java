package com.cryptopulse.app;
import java.net.*;import java.io.*;import java.util.*;
/** Simulates publisher responses without network access; verifies request validators and body avoidance. */
public class ConditionalTest {
 static int checks,requests,bodyReads;static Fake last;static int response=200;static boolean validators=true,redirect=false;
 static void check(boolean x,String message){checks++;if(!x)throw new AssertionError(message);}
 static class Fake extends HttpURLConnection {
  Map<String,String> request=new HashMap<>();Fake(URL u){super(u);requests++;last=this;}
  public void connect(){}public void disconnect(){}public boolean usingProxy(){return false;}
  public void setRequestProperty(String k,String v){request.put(k,v);}
  public int getResponseCode(){return redirect?302:response;}
  public String getHeaderField(String k){if(k.equals("Location")&&redirect)return "https://example.gov/feed";if(!validators)return null;return k.equals("ETag")?"\"v1\"":k.equals("Last-Modified")?"Tue, 06 Oct 2026 20:00:00 GMT":null;}
  public InputStream getInputStream(){bodyReads++;return new ByteArrayInputStream("changed-feed".getBytes(java.nio.charset.StandardCharsets.UTF_8));}
 }
 public static void main(String[]args)throws Exception {
  URL.setURLStreamHandlerFactory(p->p.equals("https")?new URLStreamHandler(){protected URLConnection openConnection(URL u){return new Fake(u);}}:null);
  String url="https://publisher.example/feed";Fetcher.Response first=Fetcher.cached(url);
  check(!first.notModified&&first.body.equals("changed-feed"),"200 body processed");check(Fetcher.etags.get(url).equals("\"v1\""),"server ETag cached");check(Fetcher.modified.containsKey(url),"server date cached");
  response=304;Fetcher.Response second=Fetcher.cached(url);check(last.request.get("If-None-Match").equals("\"v1\""),"ETag transmitted");check(last.request.get("If-Modified-Since").equals("Tue, 06 Oct 2026 20:00:00 GMT"),"server timestamp transmitted");check(second.notModified&&second.body.isEmpty()&&bodyReads==1,"304 never opens body stream");
  response=200;validators=false;Fetcher.cached(url);check(!Fetcher.etags.containsKey(url)&&!Fetcher.modified.containsKey(url),"obsolete validators cleared");
  redirect=true;int before=requests;boolean blocked=false;try{Fetcher.cached(url);}catch(IOException e){blocked=true;}check(blocked&&requests==before+1,"government redirect blocked before connection");
  System.out.println("PASS: "+checks+" conditional HTTP checks");
 }
}
