package com.java.zhangyuxuan.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
    private static final int TIMEOUT_LIMIT = 10000;
    public static String getData(String address) throws RuntimeException
    {
        URL url;
        HttpURLConnection connection = null;
        InputStream is = null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try
        {
            url = new URL(address);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(TIMEOUT_LIMIT);
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("accept", "*/*");
            if (connection.getResponseCode() == 200)
            {
                is = connection.getInputStream();
                int len = -1;
                byte []buf = new byte[256];
                while ((len = is.read(buf)) != -1)
                    os.write(buf, 0, len);
                os.flush();
                return os.toString();
            }
            else
            {
                throw new RuntimeException();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try{
                if (is != null)
                    is.close();
                os.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            if (connection != null)
                connection.disconnect();
        }
        return null;
    }

    public static boolean isNetAvailabel(Context context)
    {
        Log.d("db", "getting net state");
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = cm.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isAvailable() && activeInfo.isConnected())
            return true;
        return false;
    }
}

