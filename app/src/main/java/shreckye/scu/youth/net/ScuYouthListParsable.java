/*
 * Copyright 2017 Yongshun Ye
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package shreckye.scu.youth.net;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import shreckye.scu.youth.model.InfoItem;

/**
 * Created by Yongshun Ye on 5/3/2017.
 */

public abstract class ScuYouthListParsable implements InfoListParsable {

    public final static String SITE = "http://tuanwei.scu.edu.cn", COMMON_ROOT = "http://tuanwei.scu.edu.cn/index.php/main/web";

    public abstract String urlSuffix();

    String rootUrl() {
        return COMMON_ROOT + "/" + urlSuffix();
    }

    @Override
    public String pageCountUrl() {
        return rootUrl();
    }

    @Override
    public int parsePageCount(String body) {
        return Integer.parseInt(Jsoup.parseBodyFragment(body)
                .getElementsByClass("pagination").get(0)
                .children().last().text());
    }

    @Override
    public String pageUrlAt(int index) {
        return rootUrl() + "/p/" + (index + 1);
    }

    @Override
    public List<InfoItem> parseInfoItems(String body) {
        Elements items = Jsoup.parseBodyFragment(body).getElementsByClass("list-art").get(0).children();
        ArrayList<InfoItem> infoItems = new ArrayList<>(items.size());
        for (Element item : items) {
            Element a = item.getElementsByTag("a").get(0);
            InfoItem infoItem = new InfoItem(a.text(),
                    item.getElementsByTag("span").get(0).text()
                    , SITE + a.attr("href"));
            infoItems.add(infoItem);
        }

        return infoItems;
    }
}
