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

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.IntRange;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import shreckye.scu.youth.model.InfoItem;
import shreckye.scu.youth.widget.LoadMoreRecyclerViewAdapter;
import shreckye.scu.youth.widget.LoadMoreRecyclerViewLoadViewHolder;

/**
 * Created by Yongshun Ye on 5/10/2017.
 */

public abstract class ContentListItemAdapter extends LoadMoreRecyclerViewAdapter<ContentListItemAdapter.LoadViewHolder, ContentListItemAdapter.ViewHolder> {
    public static class LoadViewHolder extends LoadMoreRecyclerViewLoadViewHolder {

        public FrameLayout frameLayout;
        Button loadMoreButton;
        ProgressBar loadingProgressBar;
        TextView noMoreResultsTextView;

        public LoadViewHolder(FrameLayout frameLayout, Button loadMoreButton, ProgressBar loadingProgressBar, TextView noMoreResultsTextView) {
            super(frameLayout);
            this.frameLayout = frameLayout;
            this.loadMoreButton = loadMoreButton;
            this.loadingProgressBar = loadingProgressBar;
            this.noMoreResultsTextView = noMoreResultsTextView;
        }

        @Override
        public void setViewLoadMore(final LoadMoreRecyclerViewAdapter adapter) {
            loadMoreButton.setVisibility(View.VISIBLE);
            loadingProgressBar.setVisibility(View.GONE);
            noMoreResultsTextView.setVisibility(View.GONE);
        }


        @Override
        public void setViewLoading() {
            loadMoreButton.setVisibility(View.GONE);
            loadingProgressBar.setVisibility(View.VISIBLE);
            noMoreResultsTextView.setVisibility(View.GONE);
        }

        @Override
        public void setViewNoMoreResults() {
            loadMoreButton.setVisibility(View.GONE);
            loadingProgressBar.setVisibility(View.GONE);
            noMoreResultsTextView.setVisibility(View.VISIBLE);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout layout;
        public TextView title, date;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView.findViewById(R.id.item_layout);
            title = (TextView) itemView.findViewById(R.id.title);
            date = (TextView) itemView.findViewById(R.id.date);
        }

    }


    List<InfoItem> infoItems;

    public ContentListItemAdapter(List<InfoItem> infoItems, boolean enabled, LinearLayoutManager linearLayoutManager) {
        super(enabled, linearLayoutManager);
        this.infoItems = infoItems;
    }

    public void setInfoItems(List<InfoItem> infoItems) {
        this.infoItems = infoItems;
    }

    public void addInfoItems(List<InfoItem> infoItems) {
        this.infoItems.addAll(infoItems);
    }

    @Override
    public int getDataItemCount() {
        return infoItems.size();
    }

    @Override
    public LoadViewHolder onCreateLoadViewHolder(ViewGroup parent) {
        FrameLayout frameLayout = new FrameLayout(parent.getContext());
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));

        Button loadMoreButton = new Button(frameLayout.getContext());
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) loadMoreButton.setTextAppearance(android.support.design.R.style.Widget_AppCompat_Button_Borderless);
        loadMoreButton.setText(R.string.load_more);
        loadMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadIfNeeded();
            }
        });
        frameLayout.addView(loadMoreButton);

        ProgressBar loadingProgressBar = new ProgressBar(frameLayout.getContext());
        frameLayout.addView(loadingProgressBar);

        TextView noMoreResultsTextView = new TextView(frameLayout.getContext());
        noMoreResultsTextView.setGravity(Gravity.CENTER);
        noMoreResultsTextView.setText(R.string.no_more_results);
        frameLayout.addView(noMoreResultsTextView);

        return new LoadViewHolder(frameLayout, loadMoreButton, loadingProgressBar, noMoreResultsTextView);
    }

    @Override
    public ViewHolder onCreateDataViewHolder(ViewGroup parent, @IntRange(from = 2) int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.content_list_item, parent, false));
    }

    @Override
    public void onBindDataViewHolder(ViewHolder holder, int position) {
        final InfoItem infoItem = infoItems.get(position);
        holder.title.setText(infoItem.getTitle());
        holder.date.setText(infoItem.getDate());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, ViewDetailActivity.class);
                intent.putExtra(ViewDetailActivity.TITLE_KEY, infoItem.getTitle());
                intent.putExtra(ViewDetailActivity.URL_KEY, infoItem.getLink());
                context.startActivity(intent);
            }
        });
    }
}
