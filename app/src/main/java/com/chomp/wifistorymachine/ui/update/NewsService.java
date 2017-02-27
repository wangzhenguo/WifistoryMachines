package com.chomp.wifistorymachine.ui.update;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import android.util.Xml;


public class NewsService {

	public static UpdataInfo getListNews() throws Exception {

		String path = "http://www.bbgushiji.com:89/app/chomp/wifistorymachines_app_update.xml";
		URL url = new URL(path);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setConnectTimeout(5000);
		con.setRequestMethod("GET");
		Log.d("wzg","wzg="+con.getResponseCode());
		if (con.getResponseCode() == 200) {
			InputStream inStream = con.getInputStream();
			return parseXML(inStream);
		}
		return null;
	}

	/**
	 *
	 * 
	 * @param inStream
	 */
	private static UpdataInfo parseXML(InputStream inStream) throws Exception {
		
		UpdataInfo news  = new UpdataInfo();
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(inStream, "UTF-8");
		int event = parser.getEventType();
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_TAG:
				if ("version".equals(parser.getName())) {
					news.setVersion(parser.nextText());
				} else if ("name".equals(parser.getName())) {
					news.setName(parser.nextText());
				} else if ("url".equals(parser.getName())) {
					news.setUrl(parser.nextText());
				}
				break;
			case XmlPullParser.END_TAG:
				if ("update".equals(parser.getName())) {
					//newList.add(news);
					//news = null;
				}
				break;
			}
			event = parser.next();
		}
		return news;
	}
}
