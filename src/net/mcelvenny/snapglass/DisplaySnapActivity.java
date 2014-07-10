package net.mcelvenny.snapglass;

import java.util.Timer;
import java.util.TimerTask;

import com.google.android.glass.app.Card;
import com.google.android.glass.app.Card.ImageLayout;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.TextureView;

public class DisplaySnapActivity extends Activity {
	
	private SurfaceView display;
	private String filepath;
	private int time;
	private Timer timer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_displaysnap);
		// setTitle(R.string.app_name);
		
		filepath = getIntent().getStringExtra("filename");
		time = getIntent().getIntExtra("time", 10);
		
		Card imageCard = new Card(this);
		imageCard.setImageLayout(ImageLayout.FULL);
		imageCard.addImage(BitmapFactory.decodeFile(filepath));
		setContentView(imageCard.getView());
		//For every second that the snap is, create a 1 second timer and then update it
		for (int i=time; i>1; i--) {
			imageCard.setFootnote(""+i);
			try {
	//			Thread.sleep(1000);
			} catch(Exception e) {
				System.out.println("Exception: ");
				e.printStackTrace();
			}
		}
	//	finish();
		
	}

}
