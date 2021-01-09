package com.example.home_android_interface;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;

public class DirectWebpage extends MainActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.fragment_direct_webpage, frameLayout);


        String url = "https://stackoverflow.com/questions/29592695/android-webview-wont-load-my-url-but-will-load-others";

        WebView webview = (WebView) findViewById(R.id.myWebView);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl(url);

    }

}
