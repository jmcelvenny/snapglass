package net.mcelvenny.snapglass;

import java.io.File;

import com.google.android.glass.app.Card;
import com.google.android.glass.media.CameraManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.FileObserver;
import android.provider.MediaStore;

public class TakePictureActivity extends Activity {

	private static final int IMAGE_REQUEST_CODE = 1776;

	public Card c;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(takePicture, IMAGE_REQUEST_CODE);

	}

	@Override
	public void onActivityResult(int req, int res, Intent data) {

		if (req == IMAGE_REQUEST_CODE && res == RESULT_OK) {
			@SuppressWarnings("deprecation")
			String picturePath = data
					.getStringExtra(CameraManager.EXTRA_PICTURE_FILE_PATH);
			processPictureWhenReady(picturePath);
			c = new Card(this);
			c.setText("Logging in...");
			c.setFootnote("SnapGlass");
			setContentView(c.getView());
		}

		super.onActivityResult(req, res, data);
	}

	private void processPictureWhenReady(final String picturePath) {
		final File pictureFile = new File(picturePath);

		if (pictureFile.exists()) {
			Intent contactIntent = new Intent(this, SelectContactActivity.class);
			contactIntent.putExtra("filePath", pictureFile.getAbsolutePath());
			startActivity(contactIntent);

		} else {
			final File parentDirectory = pictureFile.getParentFile();
			FileObserver observer = new FileObserver(parentDirectory.getPath(),
					FileObserver.CLOSE_WRITE | FileObserver.MOVED_TO) {
				private boolean isFileWritten;

				@Override
				public void onEvent(int event, String path) {
					if (!isFileWritten) {
						File affectedFile = new File(parentDirectory, path);
						isFileWritten = affectedFile.equals(pictureFile);

						if (isFileWritten) {
							stopWatching();

							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									processPictureWhenReady(picturePath);
								}
							});
						}
					}
				}
			};
			observer.startWatching();
		}
	}

}
