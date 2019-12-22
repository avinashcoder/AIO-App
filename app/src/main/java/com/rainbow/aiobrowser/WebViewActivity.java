package com.rainbow.aiobrowser;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


public class WebViewActivity extends AppCompatActivity implements AdvancedWebView.Listener {

    @BindView(R.id.webview)
    AdvancedWebView mWebView;
    @BindView( R.id.progressBar )
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_web_view );
        ButterKnife.bind( this );
        Intent intent = getIntent();
        String url = intent.getStringExtra( "URL" );
        mWebView.setListener(this, this);
        mWebView.loadUrl(url);
        mWebView.getSettings().setSupportMultipleWindows( true );
        mWebView.setWebChromeClient( new WebChromeClient(){

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress( newProgress );
                if(newProgress==100){
                    progressBar.setVisibility( View.GONE );
                }else{
                    progressBar.setVisibility( View.VISIBLE );
                }
            }

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                AdvancedWebView newWebView = new AdvancedWebView(WebViewActivity.this);
                newWebView.getSettings().setJavaScriptEnabled(true);
                newWebView.getSettings().setSupportZoom(true);
                newWebView.getSettings().setBuiltInZoomControls(true);
                newWebView.getSettings().setSupportMultipleWindows(true);
//                view.addView(newWebView);
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();

                newWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Intent browserIntent = new Intent(WebViewActivity.this,WebViewActivity.class);
                        browserIntent.putExtra( "URL",url );
                        startActivity(browserIntent);
                        //view.loadUrl(url);
                        return true;
                    }
                });

                return true;
            }
        } );
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {

    }

    @Override
    public void onPageFinished(String url) {

    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {

    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

    }

    @Override
    public void onExternalPageRequest(String url) {

        Intent intent = new Intent( this,WebViewActivity.class );
        intent.putExtra( "URL",url );
        startActivity( intent );

    }

    @Override
    public void onBackPressed() {
        if(mWebView.canGoBack()){
            mWebView.goBack();
        }else{
            super.onBackPressed();
        }
    }
}
