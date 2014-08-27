import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by glacier on 14-8-27.
 */
public class SichuanRibao {

    SaveXML savexml = new SaveXML();

    public SichuanRibao( String newspaper ) {
        savexml.format.newspaper = newspaper;
    }

    public void start( String URL ) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        savexml.format.crawltime = sdf.format(new Date());
        savexml.format.language = "中文";
        savexml.format.encode = "UTF-8";

        HashMap<String,String> LayoutMap = getLayout(URL);
        for ( String LayoutLink:LayoutMap.keySet() ) {
            String Page = LayoutMap.get(LayoutLink); 			//获得版面
            savexml.format.page = Page;
            HashSet<String> NewsLink = getNewsLinks( LayoutLink );
            for ( String NewsUrl:NewsLink ) {
                getNewsInfo(NewsUrl);
            }
        }
    }

    public HashMap<String,String> getLayout( String Link ) {
        HashMap<String,String> LayoutMap = new HashMap<String,String>();
        try {
            Document Doc = Jsoup.connect(Link)
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(3000)
                    .get();
            //获取版面链接
            //if ( Link.equals("http://esb.sxdaily.com.cn/") ) {
            //    Element TrueURL = Doc.select("td[valign=bottom]").select("a[href]").first();
            //    return getLayout(TrueURL.attr("abs:href"));
            //}
            Elements Layouts = Doc.select("ul[class=listLM]").select("a[href]");
            for (Element Layout:Layouts) {
                LayoutMap.put(Layout.attr("abs:href"), Layout.text());
            }

        } catch( Exception e ) {
            e.printStackTrace();
        }
        return LayoutMap;
    }

    public HashSet<String> getNewsLinks( String LayoutLink ) {
        HashSet<String> NewsLink = new HashSet<String>();
        try {
            Document Doc = Jsoup.connect(LayoutLink)
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(3000)
                    .get();
            Elements Areas = Doc.select("ul[class=listRTM]").select("a[href]");
            for ( Element Area:Areas ) {
                NewsLink.add(Area.attr("abs:href"));
            }
        } catch( Exception e ) {
            e.printStackTrace();
        }
        return NewsLink;
    }

    public void getNewsInfo( String NewsUrl ) { 	//获得新闻来源URL
        try {
            System.out.println(NewsUrl);
            Document Doc = Jsoup.connect(NewsUrl)
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(3000)
                    .get();
            Element TitleEle = Doc.select("h3").first();
            String Title = TitleEle.text(); 		//获得文章title

            String PublishTime = NewsUrl.substring(37,41)+"-"+NewsUrl.substring(41,43)+"-"+NewsUrl.substring(43,45); //获得文章发表日期
            Elements ContentPTags = Doc.select("p[id=page_a]");
            String Content = "\r\n" + ContentPTags.text().replaceAll("     ", "\r\n") + "\r\n"; 	//获得文章正文内容
            List<String> IMGList = new ArrayList<String>(); 		//获得图片地址列表
            Elements IMGs = Doc.select("img[class=auto-width]").select("img[src]");
            for ( Element IMG:IMGs ) {
                IMGList.add(IMG.attr("abs:src"));
            }
            savexml.format.source = NewsUrl;
            savexml.format.title = Title;
            savexml.format.publishtime = PublishTime;
            savexml.format.body = Content;
            savexml.format.img = IMGList;
            savexml.save();
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }

    public String getDate( String Link ) {
        try {
            String UrlPath = new URL(Link).getPath();
            int firstindex = UrlPath.indexOf("2014");
            String Date = UrlPath.substring(firstindex, firstindex+10);
            Date = Date.replace('/', '-');
            return Date;
        } catch ( Exception e ) {
            System.out.println(e);
        }
        return null;
    }
}
