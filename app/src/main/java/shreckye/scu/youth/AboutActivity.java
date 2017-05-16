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

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView versionInfoTextView = (TextView) findViewById(R.id.version_info),
                licenseInfoTextView = (TextView) findViewById(R.id.license_info),
                openSourceLicensesInfoTextView = (TextView) findViewById(R.id.open_source_licenses_info);

        try {
            versionInfoTextView.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        licenseInfoTextView.setText(getRawResourceText(R.raw.license, 1024));
        openSourceLicensesInfoTextView.setText(getRawResourceText(R.raw.open_source_licenses, 8196));
    }

    private String getRawResourceText(int resId, int bufferSize) {
        try {
            InputStream in = getResources().openRawResource(resId);
            byte[] buffer = new byte[bufferSize];
            int length = in.read(buffer);
            return new String(buffer, 0, length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
