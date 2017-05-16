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

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import shreckye.scu.youth.model.InfoItem;

/**
 * Created by Yongshun Ye on 5/3/2017.
 */

public class AsyncHttpParser {
    OkHttpClient client;

    public AsyncHttpParser() {
        client = new OkHttpClient();
    }

    public void parsePageCount(final InfoListParsable infoListParsable, final OnParsePageCountCallback onParsePageCountCallback) {
        Request request = new Request.Builder()
                .url(infoListParsable.pageCountUrl())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onParsePageCountCallback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                onParsePageCountCallback.onPageCount(infoListParsable.parsePageCount(response.body().string()));
            }
        });

    }

    public interface OnParsePageCountCallback {
        void onFailure(IOException e);

        void onPageCount(int pageCount);
    }

    public void parseItems(final InfoListParsable infoListParsable, int pageIndex, final OnParseItemsCallback onParseItemsCallback) {
        Request request = new Request.Builder()
                .url(infoListParsable.pageUrlAt(pageIndex))
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onParseItemsCallback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                onParseItemsCallback.onItems(infoListParsable.parseInfoItems(response.body().string()));
            }
        });
    }

    public interface OnParseItemsCallback {

        void onFailure(IOException e);

        void onItems(List<InfoItem> infoItems);
    }

    public void parseContent(final InfoContentParsable infoContentParsable, final OnParseContentCallback onParseContentCallback) {
        Request request = new Request.Builder()
                .url(infoContentParsable.url())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onParseContentCallback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String content = infoContentParsable.parseContent(response.body().string());
                onParseContentCallback.onContent(HttpUtil.getBaseUrl(infoContentParsable.url()), infoContentParsable.parseTitle(content), content);
            }
        });
    }

    public interface OnParseContentCallback {

        void onFailure(IOException e);

        void onContent(String baseUrl, String title, String content);
    }
}
