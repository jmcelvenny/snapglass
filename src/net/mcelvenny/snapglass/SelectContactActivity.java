package net.mcelvenny.snapglass;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;
import com.habosa.javasnap.Friend;
import com.habosa.javasnap.Snapchat;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class SelectContactActivity extends Activity implements
		OnItemClickListener {

	private Friend[] friends;
	private Snapchat snapchat;
	private ArrayList<Card> mCards;
	private CardScrollView mCardScrollView;
	private String imageFilepath;
	private Map<String, String> displayTextToUsernameMap;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_contact);
		imageFilepath = getIntent().getStringExtra("filePath");
		displayTextToUsernameMap = new HashMap<String, String>();

		// Interface
		mCardScrollView = new CardScrollView(this);
		CardScrollViewAdapter adapter = new CardScrollViewAdapter();
		mCardScrollView.setOnItemClickListener(this);
		mCardScrollView.setAdapter(adapter);
		mCards = new ArrayList<Card>();

		// Network and Data
		new DownloadFriendsListTask().execute(null, null, null);

		while (friends == null) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException ie) {
				System.out.println("Thread interrupted.");
			}
		}

		if (!(friends.length == 1 && friends[0].getDisplayName().equals(
				"nulluser"))) {
			for (Friend f : friends) {
				String displayText = "Send to: "
						+ (f.getDisplayName().isEmpty() ? f.getUsername() : f
								.getDisplayName());
				displayTextToUsernameMap.put(displayText, f.getUsername());
				Card c = new Card(this);
				c.setText(displayText);
				mCards.add(c);
			}

			mCardScrollView.activate();
			setContentView(mCardScrollView);

		} else {
			System.out.println("Friends is null.");
		}

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

	private class DownloadFriendsListTask extends AsyncTask<URL, Integer, Long> {

		@Override
		protected Long doInBackground(URL... arg0) {
			try {
				snapchat = Snapchat.login(SnapchatCredential.USERNAME, SnapchatCredential.PASSWORD);
				friends = snapchat.getFriends();
			} catch (Exception e) {
				System.out.println("Exception: ");
				e.printStackTrace();
			}
			return 0L;
		}

		protected void onPostExecute(Long result) {
			System.out.println("Friend downloading complete");
		}

	}

	private class SendSnapTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			try {
				// snapchat is already initialized
				ArrayList<String> recipients = new ArrayList<String>();
				Card selectedCard = (Card) mCardScrollView.getSelectedItem();
				String cardText = selectedCard.getText() + "";
				recipients.add(displayTextToUsernameMap.get(cardText));
				// New bitmap testing time
				saveBitmap(rotateImage(imageFilepath));
				snapchat.sendSnap(new File(imageFilepath), recipients, false,
						false, 10000);
				System.out.println("SENT IT.");
				// Sent it!
			} catch (Exception e) {
				System.out.println("Exception sending snap: ");
				e.printStackTrace();
			}
			return null;
		}

	}

	private Bitmap rotateImage(String filepath) {
		Bitmap bmp = BitmapFactory.decodeFile(filepath);
		Matrix mat = new Matrix();
		mat.postRotate(90);
		Bitmap bmpRotate = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
				bmp.getHeight(), mat, true);
		return bmpRotate;
	}

	private void saveBitmap(Bitmap bmp) {
		FileOutputStream out;
		try {
			out = new FileOutputStream(imageFilepath);
			bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
		} catch (Exception e) {
			System.out.println("Error: ");
			e.printStackTrace();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		new SendSnapTask().execute(null, null, null);
	}

}
