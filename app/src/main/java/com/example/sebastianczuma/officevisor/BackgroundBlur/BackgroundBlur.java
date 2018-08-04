package com.example.sebastianczuma.officevisor.BackgroundBlur;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by sebastianczuma on 18.12.2016.
 */

public class BackgroundBlur {
    private View v;
    private ImageView iv;
    private Context context;

    public BackgroundBlur(View v, ImageView iv, Context context) {
        this.v = v;
        this.iv = iv;
        this.context = context;
    }

    public void blurBackground() {
        v.setDrawingCacheEnabled(true);
        Bitmap original = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(original);
        v.layout(0, 0, v.getWidth(), v.getHeight());
        v.draw(c);

        Bitmap blurred = blurRenderScript(original, 25);

        iv.setImageBitmap(blurred);
        v.setVisibility(View.INVISIBLE);
        iv.setVisibility(View.VISIBLE);
    }

    public void unblurBackground() {
        iv.setVisibility(View.INVISIBLE);
        v.setVisibility(View.VISIBLE);
    }

    private Bitmap blurRenderScript(Bitmap bitmap, int radius) {
        RenderScript renderScript = RenderScript.create(context);

        Allocation blurInput = Allocation.createFromBitmap(renderScript, bitmap);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
                Element.U8_4(renderScript));

        blur.setInput(blurInput);
        blur.setRadius(radius); // radius must be 0 < r <= 25
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmap);
        renderScript.destroy();

        return bitmap;
    }
}
