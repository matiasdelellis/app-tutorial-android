/*
 * Nextcloud Notes Tutorial for Android
 *
 * @copyright Copyright (c) 2020 John Doe <john@doe.com>
 * @author John Doe <john@doe.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.nextcloud.notestutorial.api;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.GsonBuilder;
import com.nextcloud.android.sso.api.NextcloudAPI;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NoCurrentAccountSelectedException;
import com.nextcloud.android.sso.helper.SingleAccountHelper;
import com.nextcloud.android.sso.model.SingleSignOnAccount;

import retrofit2.NextcloudRetrofitApiBuilder;

public class ApiProvider {
    private final String TAG = ApiProvider.class.getCanonicalName();

    @NonNull
    protected Context context;
    protected static API mApi;

    protected static String ssoAccountName;

    public ApiProvider(Context context) {
        this.context = context;
        initSsoApi(new NextcloudAPI.ApiConnectedListener() {
            @Override
            public void onConnected() {
                // Ignore..
            }

            @Override
            public void onError(Exception ex) {
                // Ignore...
            }
        });
    }

    public void initSsoApi(final NextcloudAPI.ApiConnectedListener callback) {
        try {
            SingleSignOnAccount ssoAccount = SingleAccountHelper.getCurrentSingleSignOnAccount(context);
            NextcloudAPI nextcloudAPI = new NextcloudAPI(context, ssoAccount, new GsonBuilder().create(), callback);

            ssoAccountName = ssoAccount.name;
            mApi = new NextcloudRetrofitApiBuilder(nextcloudAPI, API.mApiEndpoint).create(API.class);
        } catch (NextcloudFilesAppAccountNotFoundException | NoCurrentAccountSelectedException e) {
            Log.d(TAG, "setAccout() called with: ex = [" + e + "]");
        }
    }

    public static API getAPI() {
        return mApi;
    }

    public static String getAccountName() {
        return ssoAccountName;
    }

}