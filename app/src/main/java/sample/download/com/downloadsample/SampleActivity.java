package sample.download.com.downloadsample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import it.tiwiz.download.lib.DownloadActions;
import it.tiwiz.download.lib.DownloadService;


public class SampleActivity extends Activity implements View.OnClickListener{

    private static final String PATH_ON_SD_CARD = Environment.getExternalStorageDirectory() + "/DemoDownload";
    private Intent mDownloadIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sample, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        String path = "";
        switch (view.getId()) {
            case R.id.button1:
                path = "https://s3.amazonaws.com/ooomf-com-files/HWijjF7RwOPGEJ1nb4Zb_IMG_3773.jpg";
                break;
            case R.id.button2:
                path = "https://s3.amazonaws.com/ooomf-com-files/Vo7YbYQQ8iyOo4J9bOoj_ggb24.jpg";
                break;
            case R.id.button3:
                path = "https://s3.amazonaws.com/ooomf-com-files/eDLHCtzRR0yfFtU0BQar_sylwiabartyzel_themap.jpg";
                break;
        }

        mDownloadIntent = DownloadService.start(this, path, PATH_ON_SD_CARD);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDownloadIntent != null) {
            stopService(mDownloadIntent);
        }
    }
}
