package net.mcelvenny.snapglass;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;

public class DisplaySnapActivity extends Activity {

	private ImageView imageView;
	private String filePath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_displaysnap);

		imageView = (ImageView) findViewById(R.id.imageView);

		filePath = getIntent().getStringExtra("filename");

		Bitmap bmp = BitmapFactory.decodeFile(filePath);

		imageView.setImageBitmap(bmp);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}

		return super.onKeyDown(keyCode, event);

	}

}
