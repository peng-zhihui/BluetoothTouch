package xyz.pengzhihui.BTremote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.pengzhihui.BTremote.R;

public class SplashActivity extends Activity implements View.OnClickListener
{
    private ImageView im ;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slapsh);

        im= (ImageView) findViewById(R.id.imageView);
        im.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        Intent it = new Intent(this, MainActivity.class);
        startActivity(it);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        finish();
    }
}
