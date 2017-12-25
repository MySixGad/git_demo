package com.example.administrator.up_demo;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import com.tangxiaolv.telegramgallery.GalleryActivity;
import java.util.ArrayList;
import java.util.List;

public class Web extends AppCompatActivity implements View.OnClickListener{
    private static final int REQUEST_CODE = 732;

    private TextView tvResults;
    private ArrayList<String> mResults = new ArrayList<>();
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadMessage1;
    boolean isHighV = true;
    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        WebView webview = (WebView) findViewById(R.id.web);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        webview.getSettings().setJavaScriptEnabled(true);//是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        webview.getSettings().setSupportZoom(true);//是否可以缩放，默认true
        webview.getSettings().setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
        webview.getSettings().setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
        webview.getSettings().setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
        webview.getSettings().setAppCacheEnabled(true);//是否使用缓存
        webview.getSettings().setDomStorageEnabled(true);//DOM Storage
        webview.getSettings().setJavaScriptEnabled(true);;//DOM Storage

       // webview.loadUrl("http://qxu2147650096.my3w.com/public/index.php/appweb/company/index?uid="+userInfo.getUid()+"");
        webview.setWebChromeClient(new MyChromeClient());
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient() {
                @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
}

    /**
     * onActivityResult里通过onReceiveValue方法将uri给js
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data==null){
            return;
        }
        if (isHighV) {  //高版本
            List<String> photos = (List<String>) data.getSerializableExtra(GalleryActivity.PHOTOS);
            String result = photos.get(0);
            if (null == mUploadMessage1)
                return;
            Uri[] c = new Uri[1];
            Uri uri = getUri(result);
            c[0] = uri;
            mUploadMessage1.onReceiveValue(c);
            mUploadMessage1 = null;
            return;
        } else {  //低版本
            List<String> photos = (List<String>) data.getSerializableExtra(GalleryActivity.PHOTOS);
            String result = photos.get(0);
            if (null == mUploadMessage)
                return;
            Uri uri = getUri(result);
            mUploadMessage.onReceiveValue(uri);
            mUploadMessage = null;
            return;
        }
    }


    /**
     * 在此处监听网页事件  并打开相册
     */
    class MyChromeClient extends WebChromeClient {
        public boolean onShowFileChooser(android.webkit.WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            isHighV = true;
            mUploadMessage1 = filePathCallback;

            return true;
        }

        // For Android 3.0+
        public void openFileChooser(ValueCallback uploadMsg) {
            mUploadMessage = uploadMsg;
            isHighV = false;
            //打开图库 用的compile 'com.library.tangxiaolv:telegramgallery:1.0.1'框架

        }

        //3.0--版本
        public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
            openFileChooser(uploadMsg);
        }

        // For Android 4.1
        public void openFileChooser(ValueCallback uploadMsg, String acceptType, String capture) {
            openFileChooser(uploadMsg);
        }

    }



    /**
     * path转uri
     */
    private Uri getUri(String path) {
        Uri uri = null;
        if (path != null) {
            path = Uri.decode(path);
            ContentResolver cr = this.getContentResolver();
            StringBuffer buff = new StringBuffer();
            buff.append("(")
                    .append(MediaStore.Images.ImageColumns.DATA)
                    .append("=")
                    .append("'" + path + "'")
                    .append(")");
            Cursor cur = cr.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.ImageColumns._ID},
                    buff.toString(), null, null);
            int index = 0;
            for (cur.moveToFirst(); !cur.isAfterLast(); cur
                    .moveToNext()) {
                index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                index = cur.getInt(index);
            }
            if (index == 0) {

            } else {
                Uri uri_temp = Uri.parse("content://media/external/images/media/" + index);
                if (uri_temp != null) {
                    uri = uri_temp;
                }
            }
        }
        return uri;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.imgBack:
                finish();
                break;
        }
    }
}
