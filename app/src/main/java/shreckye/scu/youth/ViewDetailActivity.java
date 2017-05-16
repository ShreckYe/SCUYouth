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

package shreckye.scu.youth;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import java.io.IOException;

import shreckye.scu.youth.app.AndroidAppUtil;
import shreckye.scu.youth.net.AsyncHttpParser;
import shreckye.scu.youth.net.DetailContentParsable;

public class ViewDetailActivity extends AppCompatActivity {
    AsyncHttpParser asyncHttpParser;
    DetailContentParsable detailContentParsable;

    CollapsingToolbarLayout toolbarLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    WebView detailWebView;

    public ViewDetailActivity() {
        asyncHttpParser = new AsyncHttpParser();
    }

    public final static String TITLE_KEY = "title", URL_KEY = "url";
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getIntent().getStringExtra(URL_KEY);
        detailContentParsable = new DetailContentParsable(url);

        setContentView(R.layout.activity_view_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(getIntent().getStringExtra(TITLE_KEY));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtil.openWebPageAndShowSnackbarIfFail(ViewDetailActivity.this, url, view);
            }
        });

        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        AppUtil.setupSwipeRefreshLayoutColors(swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshAsync();
            }
        });
        detailWebView = (WebView) findViewById(R.id.detail_web_view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        refreshAsync();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refreshAsync();
                return true;
            default:
                return false;
        }
    }

    private void refreshAsync() {
        swipeRefreshLayout.setRefreshing(true);
        asyncHttpParser.parseContent(detailContentParsable, new AsyncHttpParser.OnParseContentCallback() {
            @Override
            public void onFailure(IOException e) {
                AndroidAppUtil.showLongSnackbar(detailWebView, R.string.internet_connection_failed);
            }

            @Override
            public void onContent(final String baseUrl, final String title, final String content) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toolbarLayout.setTitle(title);
                        detailWebView.loadDataWithBaseURL(baseUrl, content, "text/html; charset=utf-8", null, null);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }
}
