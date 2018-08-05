package edu.wit.seniorproject.recipad;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * Created by nguyenj11 on 6/21/2016.
 */
public class Scanner extends AppCompatActivity {
    private int barcode;

    /**
     * On Create
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanner);
        barcode = 0;
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(AnyOrientationCaptureActivity.class);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setPrompt("Scan Barcode");
        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
    }

    /**
     * Barcode scanner on activity results
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                barcode = Integer.parseInt(result.getContents());
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

}
