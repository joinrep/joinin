package com.zpi.team.joinin.ui.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.widget.ImageView;

import com.zpi.team.joinin.signin.SignInActivity;
import com.zpi.team.joinin.ui.main.MainActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Arkadiusz on 2015-05-02.
 */
public class LoadProfilePhoto extends AsyncTask<String, Void, Bitmap> {
    private ImageView mImage;
    private Context mContext;
    public LoadProfilePhoto(ImageView image, Context context) {
        mImage = image;
        mContext = context;
    }

    /***
     * @param params
     * param0 - jakie logowanie
     * param1 - id
     */
    protected Bitmap doInBackground(String... params) {
        float density = mContext.getResources().getDisplayMetrics().density;
        int pixels = 66 * (int) density;
        Log.d("LoadProfilePhoto", "doInBackground()");
        String url = null;
        if(params[0].equals(SignInActivity.GOOGLE))
            url = getGooglePhotoUrl(params[1]);
        else
            url = getFacebookPhotoUrl(params[1]);

        return BitmapDecoder.decodeSampledBitmapFromUrl(url, pixels, pixels);
    }

    protected void onPostExecute(Bitmap result) {
        RoundedBitmapDrawable rounded = RoundedBitmapDrawableFactory.create(mContext.getResources(), result);
        rounded.setCornerRadius(270f);
        rounded.setAntiAlias(true);
        mImage.setImageDrawable(rounded);
    }

    public String getFacebookPhotoUrl(String id){
        return "https://graph.facebook.com/" + id + "/picture?type=large";
    }

    public String getGooglePhotoUrl(String id) {
        String address = "http://picasaweb.google.com/data/entry/api/user/" + id + "?alt=json";;
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(address);
        String responseText = null;
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line + "\n");
                }
            } else {
                Log.e("LoadProfilePhoto", "Failed to get JSON object");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int index = builder.indexOf("gphoto$thumbnail");
        responseText = builder.substring(index);
        String http = responseText.substring(responseText.indexOf("http"), responseText.length() - 5);
        http = http.replace("s64-c", "s400-c");
        return http;
    }
}