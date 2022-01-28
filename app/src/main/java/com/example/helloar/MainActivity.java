package com.example.helloar;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ArFragment arCam;
    private int clickNo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(checkSystemSupport(this)){
            arCam = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arCameraArea);
            arCam.setOnTapArPlaneListener(((hitResult, plane, motionEvent) -> {
                clickNo++;
                if(clickNo == 1){
                    Anchor anchor = hitResult.createAnchor();
                    ModelRenderable.builder()
                            .setSource(this, R.raw.gfg_gold_text_stand_2)
                            .setIsFilamentGltf(true)
                            .build()
                            .thenAccept(modelRenderable -> addModel(anchor, modelRenderable))
                            .exceptionally(throwable -> {
                                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                builder.setMessage("Somethings not right" + throwable.getMessage()).show();
                                return null;
                            });
                }
            }));
        }
        else {
            return;
        }
    }

    private void addModel(Anchor anchor, ModelRenderable modelRenderable) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arCam.getArSceneView().getScene());
        TransformableNode transform = new TransformableNode(arCam.getTransformationSystem());
        transform.setParent(anchorNode);
        transform.setRenderable(modelRenderable);
        transform.select();
    }

    public static boolean checkSystemSupport(Activity activity){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            String openGlVersion = ((ActivityManager) Objects.requireNonNull(activity.getSystemService(Context.ACTIVITY_SERVICE))).getDeviceConfigurationInfo().getGlEsVersion();

            if(Double.parseDouble(openGlVersion) >= 3.0){
                return true;
            }
            else{
                Toast.makeText(activity, "App needs OpenGL version 3.0+", Toast.LENGTH_SHORT).show();
                activity.finish();
                return false;
            }
        }

        else{
            Toast.makeText(activity, "App does not support required build support", Toast.LENGTH_SHORT).show();
            activity.finish();
            return false;
        }
    }

}
/*
* https://www.geeksforgeeks.org/how-to-build-a-simple-augmented-reality-android-app/
* */