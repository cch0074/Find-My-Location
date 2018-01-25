package cuhk.cse.proj87.findmylocation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private String LOG_TAG = MainActivity.class.getSimpleName();
    private View mLayout;
    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1101;

    private FusedLocationProviderClient mFusedLocationClient;
    private TextView TV_Location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayout = findViewById(R.id.layout_main);
        TV_Location = findViewById(R.id.tv_location);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        FloatingActionButton fab_locate = findViewById(R.id.fab_locate);
        fab_locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
            }
        });
    }

    private void getLocation() {
        // Check if location permission is granted
        int permission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            // Permission available, start locating.
            Snackbar.make(mLayout,
                    "Start locating...",
                    Snackbar.LENGTH_SHORT).show();
            // TODO here
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                TV_Location.setText(location.toString());
                            }
                        }
                    });
        } else {
            // Permission is missing and must be granted.
            requestLocationPermission();
        }
    }

    private void requestLocationPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Snackbar.make(mLayout, "Location access is required to find your location.",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request permission
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSION_REQUEST_FINE_LOCATION);
                }
            }).show();
        } else {
            Snackbar.make(mLayout,
                    "Permission is not available. Requesting location permission.",
                    Snackbar.LENGTH_SHORT).show();
            // Request permission
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted. Do the task you need to do.
                    Snackbar.make(mLayout, "Location permission was granted.",
                            Snackbar.LENGTH_SHORT).show();
                    // Perform original actions (as included in fab listener)
                    getLocation();
                } else {
                    // Permission denied. Disable the functionality that depends on this permission.
                    Snackbar.make(mLayout, "Location permission request was denied",
                            Snackbar.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
