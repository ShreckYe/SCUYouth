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
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import shreckye.scu.youth.app.AndroidAppUtil;
import shreckye.scu.youth.model.InfoItem;
import shreckye.scu.youth.net.AsyncHttpParser;
import shreckye.scu.youth.net.InfoListParsable;


public abstract class ContentListFragment extends Fragment {
    AsyncHttpParser asyncHttpParser;
    InfoListParsable infoListParsable;
    Integer pageCount;
    int currentPage;

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView contentListRecyclerView;
    ContentListItemAdapter contentListItemAdapter;

    public ContentListFragment(InfoListParsable infoListParsable) {
        asyncHttpParser = new AsyncHttpParser();
        this.infoListParsable = infoListParsable;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.content_list_menu, menu);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        AppUtil.setupSwipeRefreshLayoutColors(swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshAsync();
            }
        });

        contentListRecyclerView = (RecyclerView) view.findViewById(R.id.content_list);
        contentListItemAdapter = new ContentListItemAdapter(new ArrayList<InfoItem>(0), false, (LinearLayoutManager) contentListRecyclerView.getLayoutManager()) {
            @Override
            public void onLoad() {
                loadItemsAsync(++currentPage);
            }
        };
        contentListItemAdapter.setupRecyclerView(contentListRecyclerView);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtil.openWebPageAndShowSnackbarIfFail(ContentListFragment.this, infoListParsable.pageUrlAt(0), getView());
            }
        });

        refreshAsync();

        return view;
    }

    /*void loadPageCountAsync() {
        asyncHttpParser.parsePageCount(infoListParsable, new AsyncHttpParser.OnParsePageCountCallback() {
            @Override
            public void onFailure(IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onParsePageCount(int pageCount) {
                ContentListFragment.this.pageCount = pageCount;
            }
        });
    }*/


    void loadItemsAsync(int pageIndex) {
        asyncHttpParser.parseItems(infoListParsable, pageIndex, new AsyncHttpParser.OnParseItemsCallback() {
            @Override
            public void onFailure(IOException e) {
                e.printStackTrace();
                showInternetConnectionFailedSnackbar();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        contentListItemAdapter.notifyLoadingCompleted(true);
                    }
                });
            }

            @Override
            public void onItems(List<InfoItem> infoItems) {
                addInfoItemsAndNotifyOnUiThread(infoItems);
            }
        });
    }

    void refreshAsync() {
        swipeRefreshLayout.setRefreshing(true);
        asyncHttpParser.parsePageCount(infoListParsable, new AsyncHttpParser.OnParsePageCountCallback() {
            @Override
            public void onFailure(IOException e) {
                e.printStackTrace();
                setRefreshingOnUiThread(false);
                showInternetConnectionFailedSnackbar();
            }

            @Override
            public void onPageCount(final int pageCount) {
                asyncHttpParser.parseItems(infoListParsable, 0, new AsyncHttpParser.OnParseItemsCallback() {
                    @Override
                    public void onFailure(IOException e) {
                        e.printStackTrace();
                        setRefreshingOnUiThread(false);
                        showInternetConnectionFailedSnackbar();
                    }

                    @Override
                    public void onItems(List<InfoItem> infoItems) {
                        ContentListFragment.this.pageCount = pageCount;
                        currentPage = 0;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                contentListItemAdapter.notifyLoadingCompleted(true);
                            }
                        });
                        setInfoItemsAndNotifyOnUiThread(infoItems);
                        setRefreshingOnUiThread(false);
                    }
                });
            }
        });
    }

    private void setInfoItemsAndNotifyOnUiThread(List<InfoItem> infoItems) {
        contentListItemAdapter.setInfoItems(infoItems);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                contentListItemAdapter.notifyDataSetChanged();
            }
        });
    }

    private void addInfoItemsAndNotifyOnUiThread(List<InfoItem> infoItems) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                contentListItemAdapter.notifyLoadingCompleted(pageCount == null || currentPage < pageCount - 1);
            }
        });

        final int count = contentListItemAdapter.getDataItemCount(), size = infoItems.size();
        contentListItemAdapter.addInfoItems(infoItems);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                contentListItemAdapter.notifyItemRangeInserted(count, size);
            }
        });
    }

    private void setRefreshingOnUiThread(final boolean refreshing) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(refreshing);
            }
        });
    }

    private void showInternetConnectionFailedSnackbar() {
        AndroidAppUtil.showLongSnackbar(getView(), R.string.internet_connection_failed);
    }
}
