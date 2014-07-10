package net.mcelvenny.snapglass;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.android.glass.app.Card;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;
import com.habosa.javasnap.Snap;
import com.habosa.javasnap.Snapchat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class ListSnapActivity extends Activity implements OnItemClickListener {

	private Card c;
	private Snap[] snaps;
	private Snap selectedSnap;
	private Snapchat snapchat;

	private GestureDetector mGestureDetector;
	private CardScrollView csv;
	private ArrayList<Card> mCards;
	private Map<String, Snap> displayToSnap;

	private boolean snapsAreLoaded, snapDataLoaded;
	private byte[] snapdata;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mGestureDetector = createGestureDetector(this);
		snapsAreLoaded = false;
		displayToSnap = new HashMap<String, Snap>();
		mCards = new ArrayList<Card>();

		c = new Card(this);
		c.setText("Logging in...");
		c.setFootnote("SnapGlass");
		setContentView(c.getView());

		new DownloadSnapsTask().execute(null, null, null);

		while (!snapsAreLoaded) {
			try {
				Thread.sleep(10);
			} catch (Exception e) {
				System.out.println("Exception: ");
				e.printStackTrace();
			}
		}
		// filter these bad boys
		ArrayList<Snap> unread = new ArrayList<Snap>();
		Snap[] downloadable = Snap.filterDownloadable(snaps);
		for (Snap s : downloadable) {
			if (s.isImage())
				unread.add(s);
		}
		if (unread.size() == 0) {
			Card c = new Card(this);
			c.setText("No new snaps :(");
			c.setFootnote("SnapGlass");
			mCards.add(c);
		} else {
			for (Snap s : unread) {
				Card c = new Card(this);
				String displayText = "From: " + s.getSender() + "\nLength: "
						+ s.getTime() + "\n\nPress and hold to open";
				System.out.println("Adding card: " + displayText);
				c.setText(displayText);
				displayToSnap.put(displayText, s);
				c.setFootnote("SnapGlass");
				mCards.add(c);
			}
		}

		csv = new CardScrollView(this);
		CardScrollViewAdapter adapter = new CardScrollViewAdapter();
		csv.setAdapter(adapter);
		csv.setOnItemClickListener(this);

		// Do it
		csv.activate();
		setContentView(csv);

	}

	private class CardScrollViewAdapter extends CardScrollAdapter {
		@Override
		public int getPosition(Object item) {
			return mCards.indexOf(item);
		}

		@Override
		public int getCount() {
			return mCards.size();
		}

		@Override
		public Object getItem(int position) {
			return mCards.get(position);
		}

		@Override
		public int getViewTypeCount() {
			return Card.getViewTypeCount();
		}

		@Override
		public int getItemViewType(int position) {
			return mCards.get(position).getItemViewType();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return mCards.get(position).getView(convertView, parent);
		}

	}

	private class DownloadSnapsTask extends AsyncTask<URL, Integer, Long> {

		@Override
		protected Long doInBackground(URL... arg0) {
			try {
				System.out.println("Trying to login with: " + SnapchatCredential.USERNAME + ":" + SnapchatCredential.PASSWORD);
				snapchat = Snapchat.login(SnapchatCredential.USERNAME, SnapchatCredential.PASSWORD);
				snaps = snapchat.getSnaps();
				snapsAreLoaded = true;
			} catch (Exception e) {
				System.out.println("Exception: ");
				e.printStackTrace();
			}
			return 0L;
		}

	}

	private class FetchSnapBytes extends AsyncTask<URL, Integer, Long> {

		@Override
		protected Long doInBackground(URL... arg0) {
			try {
				byte[] bytes = snapchat.getSnap(selectedSnap);
				snapdata = bytes;
				snapDataLoaded=true;
			} catch (Exception e) {
				System.out.println("Exception: ");
				e.printStackTrace();
			}
			return 0L;
		}

	}

	private GestureDetector createGestureDetector(Context context) {
		GestureDetector gestureDetector = new GestureDetector(context);
		// Create a base listener for generic gestures
		gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
			@Override
			public boolean onGesture(Gesture gesture) {
				if (gesture == Gesture.SWIPE_RIGHT) {
					finish();
					return true;
				}
				return false;
			}
		});
		return gestureDetector;
	}

	/*
	 * Send generic motion events to the gesture detector
	 */
	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {
		if (mGestureDetector != null) {
			return mGestureDetector.onMotionEvent(event);
		}
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Card selectedCard = (Card) csv.getSelectedItem();
		selectedSnap = displayToSnap.get(selectedCard.getText());
		Intent displaySnapIntent = new Intent(this, DisplaySnapActivity.class);
		new FetchSnapBytes().execute(null, null, null);
		while (!snapDataLoaded) {
			try {
				Thread.sleep(10);
			} catch (Exception e) {
				System.out.println("Exception: ");
				e.printStackTrace();
			}
		}
		String fname = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
				+ "/" + selectedSnap.getId() + ".jpg";
		File save = new File(fname);
		System.out.println("Saved file to: " + fname);
		FileOutputStream fOut;
		try {
			fOut = new FileOutputStream(save);
			fOut.write(snapdata);
			fOut.close();
			System.out.println("Wrote to file");
		} catch (Exception e) {
			e.printStackTrace();
		}
		displaySnapIntent.putExtra("filename", fname);
		displaySnapIntent.putExtra("snapTime", selectedSnap.getTime());
		startActivity(displaySnapIntent);
	}
}