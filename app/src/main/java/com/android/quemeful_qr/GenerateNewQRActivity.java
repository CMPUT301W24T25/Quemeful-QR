package com.android.quemeful_qr;

import static android.graphics.Color.BLACK; // black qr code
import static android.graphics.Color.WHITE; // white background

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.zxing.BarcodeFormat; // import for library used to generate QR code
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

/**
 * This is an activity class for generating QR code.
 * Note: For the purpose of generating and scanning a QR code,
 * zxing library has been implemented in the build.gradle.kts(Module:app)
 */
public class GenerateNewQRActivity extends AppCompatActivity {
    private ImageView QRImage;
    private TextView eventTitle;
    private ImageView poster;

    /**
     * This onCreate method is used to setOnClickListener to the generate button and displays the QR code as image.
     * The generation and appearance of the QR code is done in the createBitmap method below.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_new_qractivity);

        eventTitle = findViewById(R.id.eventName_text);
        poster = findViewById(R.id.posterImage);
        QRImage = findViewById(R.id.QRImage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.backTool);

        Intent intent = getIntent();
        EventHelper event = (EventHelper) intent.getSerializableExtra("event");
        assert event != null;
        eventTitle.setText(event.getTitle());

        assert event.getPoster() != null;
        byte[] imageAsBytes = Base64.decode(event.getPoster().getBytes(), Base64.DEFAULT);
        poster.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));

        Bitmap bitmap = createBitmap(event.getId());
        QRImage.setImageBitmap(bitmap);

        // clicking on the back arrow on top navigates back to the previous page
        toolbar.setNavigationOnClickListener(v -> {
            // back clicked
            finish();
        });
    }

    /**
     * This method is used to create/Encode the QR code using BitMatrix.
     * References: URLs (Access Date: 21.02.2024)
     * <a href="https://www.youtube.com/watch?v=zHStZwXtbj0">...</a>
     * Author- Programmity  , Published Date- Nov 17, 2020
     * <a href="https://www.youtube.com/watch?v=5DEHmN4PmA0">...</a>
     * Author- GeeksforGeeks  , Published Date- Jul 6, 2021
     * <a href="https://stackoverflow.com/questions/8831050/android-how-to-read-qr-code-in-my-application">...</a>
     * Author- Enamul Haque , License- CC BY-SA 3.0  , Published Date- Jan 28, 2018
     * <a href="https://stackoverflow.com/questions/28232116/android-using-zxing-generate-qr-code">...</a>
     * Author- Harpreet , License- CC BY-SA 4.0 , Published Date- Jan 16, 2019
     * @param text Takes string input to create a QR code based on that
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