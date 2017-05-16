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

package shreckye.scu.youth.widget;

import android.os.Handler;
import android.support.annotation.IntRange;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by Yongshun Ye on 5/14/2017.
 */

public abstract class LoadMoreRecyclerViewAdapter<LVH extends LoadMoreRecyclerViewLoadViewHolder, DVH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /*private byte state;
    public final static byte STATE_LOAD_MORE = 1, STATE_LOADING = 2, STATE_NO_MORE_RESULTS = 3;*/
    public final static int VIEW_TYPE_DATA = 2, VIEW_TYPE_LOAD = 1;
    LinearLayoutManager linearLayoutManager;

    boolean enabled = true, loading = false, hasScrolledBack = true;
    int threshold;
    public final static int DEFAULT_THRESHOLD = 4;

    private Handler handler;

    public LoadMoreRecyclerViewAdapter(int threshold, boolean enabled, LinearLayoutManager linearLayoutManager) {
        this.threshold = threshold;
        this.enabled = enabled;
        this.linearLayoutManager = linearLayoutManager;

        handler = new Handler();
    }

    public LoadMoreRecyclerViewAdapter(boolean enabled, LinearLayoutManager linearLayoutManager) {
        this(DEFAULT_THRESHOLD, enabled, linearLayoutManager);
    }

    public void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setAdapter(this);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (linearLayoutManager.findLastVisibleItemPosition() >= linearLayoutManager.getItemCount() - 1 - threshold) {
                    if (hasScrolledBack) {
                        hasScrolledBack = false;
                        loadIfNeededAndPostViewChange();
                    }
                } else
                    hasScrolledBack = true;
            }
        });
    }

    // No need to be declared synchronized because it's always called from the UI thread
    public void loadIfNeeded() {
        if (enabled && !loading) {
            loading = true;
            notifyItemChanged(getDataItemCount());

            onLoad();
        }
    }

    public void loadIfNeededAndPostViewChange() {
        if (enabled && !loading) {
            loading = true;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    notifyItemChanged(getDataItemCount());
                }
            });

            onLoad();
        }
    }


    public void notifyLoadingCompleted(boolean enabled) {
        this.enabled = enabled;
        loading = false;
        notifyItemChanged(getDataItemCount());
    }

    public abstract void onLoad();

    @Override
    public int getItemViewType(int position) {
        return position < getDataItemCount() ? getDataItemViewType(position) : VIEW_TYPE_LOAD;
    }

    @IntRange(from = 2)
    public int getDataItemViewType(int position) {
        return VIEW_TYPE_DATA;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_LOAD:
                return onCreateLoadViewHolder(parent);
            default:
                return onCreateDataViewHolder(parent, viewType);
        }
    }

    public abstract LVH onCreateLoadViewHolder(ViewGroup parent);

    public abstract DVH onCreateDataViewHolder(ViewGroup parent, @IntRange(from = 2) int viewType);

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //holder instanceof LVH
        if (position == getDataItemCount()) {
            LoadMoreRecyclerViewLoadViewHolder loadViewHolder = (LoadMoreRecyclerViewLoadViewHolder) holder;
            if (enabled) {
                if (loading)
                    loadViewHolder.setViewLoading();
                else
                    loadViewHolder.setViewLoadMore(this);
            } else
                loadViewHolder.setViewNoMoreResults();
        } else
            onBindDataViewHolder((DVH) holder, position);
    }


    public abstract void onBindDataViewHolder(DVH holder, int position);

    @Override
    public int getItemCount() {
        return getDataItemCount() + 1;
    }

    public abstract int getDataItemCount();
}
