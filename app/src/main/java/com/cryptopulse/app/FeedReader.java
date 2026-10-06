package com.cryptopulse.app;
import java.io.*;import java.util.*;import java.text.*;import javax.xml.parsers.*;import org.xml.sax.*;import org.xml.sax.helpers.DefaultHandler;
/** Bounded HTTPS responses are parsed with DTDs/entities disabled. RSS and Atom supported. */
public final class FeedReader {
 public static long date(String s){for(String f:new String[]{"EEE, dd MMM yyyy HH:mm:ss Z","EEE, d MMM yyyy HH:mm:ss Z","yyyy-MM-dd'T'HH:mm:ssXXX","yyyy-MM-dd'T'HH:mm:ss.SSSXXX","yyyy-MM-dd'T'HH:mm:ss'Z'","yyyy-MM-dd HH:mm:ss"})try{SimpleDateFormat d=new SimpleDateFormat(f,Locale.US);d.setTimeZone(TimeZone.getTimeZone("UTC"));d.setLenient(false);return d.parse(s.trim()).getTime();}catch(Exception ignored){}return 0;}
 static String clean(String s){return s.replaceAll("(?is)<(script|style)[^>]*>.*?</\\1>","").replaceAll("<[^>]*>"," ").replace("&nbsp;"," ").replace("&amp;","&").replace("&quot;","\"").replace("&#39;","'").replaceAll("\\s+"," ").trim();}
 public static List<Core.Story> parse(String xml,String source,int rank)throws Exception{
  if(xml.startsWith("\uFEFF"))xml=xml.substring(1);
  if(xml.toUpperCase(Locale.US).contains("<!DOCTYPE")||xml.toUpperCase(Locale.US).contains("<!ENTITY"))throw new IOException("DTD/entity declarations are not supported");
  List<Core.Story>out=new ArrayList<>();SAXParserFactory factory=SAXParserFactory.newInstance();factory.setNamespaceAware(true);XMLReader reader=factory.newSAXParser().getXMLReader();reader.setEntityResolver((a,b)->new InputSource(new StringReader("")));
  reader.setErrorHandler(new DefaultHandler(){public void error(SAXParseException e)throws SAXException{throw e;}public void fatalError(SAXParseException e)throws SAXException{throw e;}});
  reader.setContentHandler(new DefaultHandler(){int depth=0,itemDepth=-1,fieldDepth=-1;String title="",url="",summary="",when="",field="";StringBuilder text=new StringBuilder();
   String tag(String local,String q){return local.isEmpty()?q:local;}
   public void startElement(String uri,String local,String q,Attributes a){String t=tag(local,q);depth++;if(t.equals("item")||t.equals("entry")){itemDepth=depth;title=url=summary=when="";field="";fieldDepth=-1;}
    else if(itemDepth>0&&depth==itemDepth+1){if(t.equals("link")&&a.getValue("href")!=null&&(a.getValue("rel")==null||a.getValue("rel").equals("alternate")))url=a.getValue("href");if(Arrays.asList("title","link","description","summary","pubDate","published","updated").contains(t)){field=t;fieldDepth=depth;text.setLength(0);}}}
   public void characters(char[]c,int start,int length){if(fieldDepth>0)text.append(c,start,length);}
   public void endElement(String uri,String local,String q){String t=tag(local,q);if(depth==fieldDepth){String v=text.toString();if(field.equals("title"))title=clean(v);else if(field.equals("link")&&url.isEmpty())url=v.trim();else if(field.equals("description")||field.equals("summary"))summary=clean(v);else if(field.equals("pubDate")||field.equals("published")||(field.equals("updated")&&when.isEmpty()))when=v;field="";fieldDepth=-1;}
    if(depth==itemDepth&&(t.equals("item")||t.equals("entry"))){long at=date(when);if(!title.isEmpty()&&Core.allowedUrl(url)&&at>0)out.add(new Core.Story(title,url,source,summary,rank,at));itemDepth=-1;}depth--;}
  });reader.parse(new InputSource(new StringReader(xml)));if(out.isEmpty())throw new IOException("No dated RSS/Atom stories");return out;
 }
}
