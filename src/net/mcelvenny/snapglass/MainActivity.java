package net.mcelvenny.snapglass;

import java.io.IOException;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView;

public class MainActivity extends Activity {

	private static final String kTag = MainActivity.class.getSimpleName();
	private TextureView mVideoCaptureView = null;
	private GestureDetector mGestureDetector;
	private Camera mCamera = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mVideoCaptureView = (TextureView) findViewById(R.id.takesnap);
		mVideoCaptureView.setKeepScreenOn(true);
		mVideoCaptureView
				.setSurfaceTextureListener(new VideoCaptureTextureListener());

		mGestureDetector = createGestureDetector(this);

		new SnapchatCredential(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()
				+ "/");
	}

	@Override
	protected void onPause() {
		super.onPause();
		stopVideo();
	}

	@Override
	protected void onResume() {
		super.onResume();
		startVideo();
	}

	private void startVideo() {
		if (mCamera != null) {
			mCamera.release();
		}
		mCamera = Camera.open();
		if (null != mVideoCaptureView) {
			try {
				mCamera.setPreviewTexture(mVideoCaptureView.getSurfaceTexture());
			} catch (IOException e) {
				Log.e(kTag, "Error setting preview texture", e);
				return;
			}
		}
		Camera.Parameters params = mCamera.getParameters();
		params.setPreviewFpsRange(30000, 30000);
		mCamera.setParameters(params);
		mCamera.startPreview();
	}

	private void stopVideo() {
		if (null == mCamera)
			return;
		try {
			mCamera.stopPreview();
			mCamera.setPreviewDisplay(null);
			mCamera.setPreviewTexture(null);
			mCamera.release();
		} catch (IOException e) {
			Log.w(kTag, e);
		}
		mCamera = null;
	}

	private final class VideoCaptureTextureListener implements
			TextureView.SurfaceTextureListener {

		@Override
		public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
			stopVideo();
			return true;
		}

		@Override
		public void onSurfaceTextureAvailable(SurfaceTexture surface,
				int width, int height) {
			startVideo();
		}

		@Override
		public void onSurfaceTextureSizeChanged(SurfaceTexture surface,
				int width, int height) {
		}

		@Override
		public void onSurfaceTextureUpdated(SurfaceTexture surface) {
		}

	}

	private GestureDetector createGestureDetector(Context context) {
		GestureDetector gestureDetector = new GestureDetector(context);
		gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
			@Override
			public boolean onGesture(Gesture gesture) {
				if (gesture == Gesture.TAP) {
					takePicture();
					return true;
				} else if (gesture == Gesture.SWIPE_DOWN) {
					finish();
					return true;
				} else if (gesture == Gesture.SWIPE_LEFT) {
					showNewSnaps();
					return true;
				}
				return false;
			}
		});
		return gestureDetector;
	}

	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {
		if (mGestureDetector != null) {
			return mGestureDetector.onMotionEvent(event);
		}
		return false;
	}

	private void takePicture() {
		Intent contactSelectIntent = new Intent(this, TakePictureActivity.class);
		startActivity(contactSelectIntent);

	}

	private void showNewSnaps() {
		Intent showSnapIntent = new Intent(this, ListSnapActivity.class);
		startActivity(showSnapIntent);
	}
}
