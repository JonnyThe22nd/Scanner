package de.brickscan.app;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;

import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

/**
 * Zeigt eine Webseite (BrickLink-Preisseite) in einem kleinen Fenster über der App,
 * damit man den Preis ablesen und gleichzeitig in der App eintippen kann.
 * Die Seite wird nur angezeigt; die App liest nichts davon aus.
 */
@CapacitorPlugin(name = "PriceView")
public class PriceViewPlugin extends Plugin {

    private WebView web;
    private OnBackPressedCallback backCallback;

    @Override
    public void load() {
        backCallback = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                if (web != null && web.canGoBack()) {
                    web.goBack();
                } else {
                    hide();
                    getBridge().triggerWindowJSEvent("priceviewclosed");
                }
            }
        };
        getActivity().getOnBackPressedDispatcher().addCallback(getActivity(), backCallback);
    }

    @PluginMethod
    public void open(PluginCall call) {
        final String url = call.getString("url");
        final double top = call.getDouble("top", 0.0);
        final double height = call.getDouble("height", 320.0);
        if (url == null || !url.startsWith("https://")) {
            call.reject("Ungültige Adresse");
            return;
        }
        getActivity().runOnUiThread(() -> {
            try {
                float d = getContext().getResources().getDisplayMetrics().density;
                ViewGroup root = getActivity().findViewById(android.R.id.content);
                if (web == null) {
                    web = new WebView(getContext());
                    WebSettings s = web.getSettings();
                    s.setJavaScriptEnabled(true);
                    s.setDomStorageEnabled(true);
                    s.setSupportZoom(true);
                    s.setBuiltInZoomControls(true);
                    s.setDisplayZoomControls(false);
                    s.setUseWideViewPort(true);
                    s.setLoadWithOverviewMode(true);
                    web.setWebViewClient(new WebViewClient());
                    CookieManager.getInstance().setAcceptCookie(true);
                    CookieManager.getInstance().setAcceptThirdPartyCookies(web, true);
                    web.setElevation(24f * d);
                    root.addView(web, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 10));
                }
                // Position der App-WebView im Fenster berücksichtigen (Statusleiste / Edge-to-Edge)
                int[] appLoc = new int[2];
                int[] rootLoc = new int[2];
                getBridge().getWebView().getLocationInWindow(appLoc);
                root.getLocationInWindow(rootLoc);
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, Math.max(1, (int) Math.round(height * d)));
                lp.gravity = Gravity.TOP;
                lp.topMargin = (appLoc[1] - rootLoc[1]) + (int) Math.round(top * d);
                web.setLayoutParams(lp);
                web.setVisibility(View.VISIBLE);
                web.bringToFront();
                web.loadUrl(url);
                backCallback.setEnabled(true);
                call.resolve();
            } catch (Exception e) {
                call.reject("Fenster konnte nicht geöffnet werden: " + e.getMessage());
            }
        });
    }

    @PluginMethod
    public void reload(PluginCall call) {
        getActivity().runOnUiThread(() -> {
            if (web != null) web.reload();
            call.resolve();
        });
    }

    @PluginMethod
    public void close(PluginCall call) {
        getActivity().runOnUiThread(() -> {
            hide();
            call.resolve();
        });
    }

    private void hide() {
        if (web != null) {
            web.stopLoading();
            web.loadUrl("about:blank");
            web.clearHistory();
            web.setVisibility(View.GONE);
        }
        if (backCallback != null) backCallback.setEnabled(false);
    }
}
