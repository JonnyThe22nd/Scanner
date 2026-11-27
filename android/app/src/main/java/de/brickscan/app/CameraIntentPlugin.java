package de.brickscan.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResult;
import androidx.core.content.FileProvider;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.io.File;

/**
 * Öffnet die System-Kamera-App (mit Blitz und Autofokus) direkt auf der Rückkamera
 * und gibt den Pfad des Fotos an die App zurück. Kein eigenes Kamera-Overlay.
 */
@CapacitorPlugin(name = "RearCamera")
public class CameraIntentPlugin extends Plugin {

    private File photoFile;

    @PluginMethod
    public void shoot(PluginCall call) {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getContext().getPackageManager()) == null) {
                call.reject("Keine Kamera-App gefunden");
                return;
            }
            File dir = new File(getContext().getCacheDir(), "scans");
            if (!dir.exists()) dir.mkdirs();
            photoFile = new File(dir, "scan_" + System.currentTimeMillis() + ".jpg");
            Uri uri = FileProvider.getUriForFile(
                getContext(), getContext().getPackageName() + ".fileprovider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            // Rückkamera vorgeben – wird von den meisten Kamera-Apps beachtet
            intent.putExtra("android.intent.extras.CAMERA_FACING", 0);
            intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 0);
            intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", false);

            startActivityForResult(call, intent, "onShot");
        } catch (Exception e) {
            call.reject("Kamera konnte nicht geöffnet werden: " + e.getMessage());
        }
    }

    @ActivityCallback
    private void onShot(PluginCall call, ActivityResult result) {
        if (call == null) return;
        if (result.getResultCode() != Activity.RESULT_OK || photoFile == null || !photoFile.exists()) {
            call.reject("cancelled");
            return;
        }
        JSObject ret = new JSObject();
        ret.put("path", photoFile.getAbsolutePath());
        call.resolve(ret);
    }
}
