package net.mcelvenny.snapglass;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class SnapchatCredential {

	public static String USERNAME, PASSWORD;

	private static String credentialFilename = "snapglass.xml";

	public SnapchatCredential(String extDir) {

		File credentialFile = new File(extDir + credentialFilename);
		if (!credentialFile.exists()) {
			try {
				credentialFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		XmlPullParserFactory pullParserFactory;
		try {
			pullParserFactory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = pullParserFactory.newPullParser();

			InputStream in_s = new FileInputStream(credentialFile);
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in_s, null);

			parseXML(parser);

		} catch (XmlPullParserException e) {

			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void parseXML(XmlPullParser parser) throws XmlPullParserException,
			IOException {
		int eventType = parser.getEventType();

		while (eventType != XmlPullParser.END_DOCUMENT) {
			String name = null;
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:

				break;
			case XmlPullParser.START_TAG:
				name = parser.getName();
				if (name.equals("username")) {
					USERNAME = parser.nextText();
				} else if (name.equals("password")) {
					PASSWORD = parser.nextText();
				}
				break;
			case XmlPullParser.END_TAG:

			}
			eventType = parser.next();
		}
	}

}
