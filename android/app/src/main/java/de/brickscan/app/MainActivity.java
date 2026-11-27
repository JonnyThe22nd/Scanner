package de.brickscan.app;

import android.os.Bundle;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(PriceViewPlugin.class);
        registerPlugin(CameraIntentPlugin.class);
        super.onCreate(savedInstanceState);
    }
}
