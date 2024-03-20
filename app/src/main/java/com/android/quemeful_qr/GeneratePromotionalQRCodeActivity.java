package com.android.quemeful_qr;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

/**
 * This is an activity class used to generate a new promotional QR Code for an an event.
 */
public class GeneratePromotionalQRCodeActivity extends AppCompatActivity {
    private ImageView promotionQRCode;
    private TextView shareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_promotional_qrcode);

        promotionQRCode = findViewById(R.id.promotion_qrcode);
        shareButton = findViewById(R.id.share_button);

        // clicking on the back arrow on top navigates back to the previous page
        Toolbar toolbar = (Toolbar) findViewById(R.id.backTool);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back clicked
                finish();
            }
        });

        Intent intent = getIntent();
        EventHelper event = (EventHelper) intent.getSerializableExtra("event");
        assert event != null;
        Bitmap bitmap = createBitmap(event.getId());
        promotionQRCode.setImageBitmap(bitmap);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pop up user email ids
                // multiple selection for email ids
                // display toast shared
            }
        });
    }

    /**
     * This method is used to create/Encode the QR code using Bitmatrix.
     * @param text Takes input text (e.g. the event name) to create a QR code based on that
     * @return Returns BitMap map
     */
    private Bitmap createBitmap(String text) {
        BitMatrix matrix;
        try {
            // encode the QR code
            matrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, 250, 250);
        }
        catch(WriterException e){
            e.printStackTrace();
            return null;
        }
        // appearance of QR Code
        int w = matrix.getWidth();
        int h = matrix.getHeight();
        int[] dim = new int[w * h];
        for(int i = 0; i < h ; i++){
            int offset = i * w;
            for(int j = 0; j < w; j++){
                dim[offset + j] = matrix.get(j,i) ? BLACK : WHITE;
            }
        }
        Bitmap map = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        map.setPixels(dim, 0, w,0, 0, w, h);
        return map;
    }

} // activity class closing