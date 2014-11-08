package com.glacier.dongying.Main;

import com.glacier.dongying.DongyingBBS.BBSPageProcessor;
import com.glacier.dongying.DongyingBa.DongyingPageProcessor;
import com.glacier.dongying.DongyingTieba.TiebaPageProcessor;
import com.glacier.dongying.ShengliShequ.ShengliPageProcessor;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.component.BloomFilterDuplicateRemover;

/**
 * Created by glacier on 14-11-4.
 */
public class Main {
    public static void main(String[] args) {
        //Spider jobSpider = Spider.create(new BBSPageProcessor());     //BBS
        //Spider jobSpider = Spider.create(new TiebaPageProcessor());     //Tieba
        //Spider jobSpider = Spider.create(new ShengliPageProcessor());     //ShengliShequ
        Spider jobSpider = Spider.create(new DongyingPageProcessor());
        jobSpider.setScheduler(new QueueScheduler()
        .setDuplicateRemover(new BloomFilterDuplicateRemover(100000000)));
        //jobSpider.addUrl("http://club.dzwww.com/forum-223-1.html");     //BBS
        //jobSpider.addUrl("http://tieba.baidu.com/f?kw=%B6%AB%D3%AA");     //Tieba
        //jobSpider.addUrl("http://www.slit.cn/bbs/forum.php");           //ShengliShequ
        jobSpider.addUrl("http://www.dy8.com/");
        jobSpider.thread(1);
        jobSpider.run();
    }
}
