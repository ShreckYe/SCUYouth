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

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import shreckye.scu.youth.app.AndroidAppUtil;

/**
 * Created by Yongshun Ye on 5/14/2017.
 */

public class AppUtil {
    public static void openWebPageAndShowSnackbarIfFail(Fragment fragment, String url, View view) {
        try {
            fragment.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (ActivityNotFoundException e) {
            AndroidAppUtil.showLongSnackbar(view, R.string.internet_connection_failed);
        }
    }

    public static void openWebPageAndShowSnackbarIfFail(Activity activity, String url, View view) {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (ActivityNotFoundException e) {
            AndroidAppUtil.showLongSnackbar(view, R.string.internet_connection_failed);
        }
    }

    public static void setupSwipeRefreshLayoutColors(SwipeRefreshLayout swipeRefreshLayout) {
        swipeRefreshLayout.setColorSchemeResources(R.color.material_light_green_A200, R.color.material_orange_500);
    }
}