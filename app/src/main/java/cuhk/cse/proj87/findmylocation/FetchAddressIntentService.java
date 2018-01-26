package cuhk.cse.proj87.findmylocation;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class FetchAddressIntentService extends IntentService {
    private final String LOG_TAG = this.getClass().getSimpleName();
    protected ResultReceiver mReceiver;

    public FetchAddressIntentService() {
        super("FetchAddressIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String errorMsg = "";

        // Get location to this service through an extra
        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        // If location data is not sent over through an extra, send an error message to receiver
        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        if (location == null) {
            errorMsg = getString(R.string.no_location_data_provided);
            Log.wtf(LOG_TAG, errorMsg);
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMsg);
            return;
        }

        // Address found using the Geocoder below
        List<Address> addresses = null;

        // Use the Geocoder to get location given latitude and longitude
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch(IOException e) {
            errorMsg = getString(R.string.service_not_available);
            Log.e(LOG_TAG, errorMsg);
        } catch(IllegalArgumentException e) {
            errorMsg = getString(R.string.invalid_lat_long_used);
            Log.e(LOG_TAG, errorMsg + "Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude());
        }

        if (addresses == null || addresses.size() == 0) {
            // When no address found, send an error message to the receiver
            if (errorMsg.isEmpty()) {
                errorMsg = getString(R.string.no_address_found);
                Log.e(LOG_TAG, errorMsg);
            }
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMsg);
        } else {
            Address address = addresses.get(0);
            String addressStr = "";

            // Fetch address line, join them and send to the thread
            for (int i=0; i<=address.getMaxAddressLineIndex(); i++) {
                if (i!=0) { addressStr += ", "; }
                addressStr += address.getAddressLine(i);
            }
            Log.i(LOG_TAG, getString(R.string.address_found));
            deliverResultToReceiver(Constants.SUCCESS_RESULT,
                    addressStr);
        }
    }

    /**
     * Sends a result code and message to the receiver.
     * @param resultCode
     * @param msg
     */
    private void deliverResultToReceiver(int resultCode, String msg) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, msg);
        mReceiver.send(resultCode, bundle);
    }

}
