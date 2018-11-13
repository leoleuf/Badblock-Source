package fr.badblock.common.shoplinker.bukkit.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.AbstractMap;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;

import fr.badblock.api.common.utils.data.Callback;

public class NetworkUtils 
{
	private static String toString(InputStream inputStream) throws IOException
	{
		if (inputStream == null)
		{
			return null;
		}

		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8")))
		{
			String inputLine;
			StringBuilder stringBuilder = new StringBuilder();
			while ((inputLine = bufferedReader.readLine()) != null)
			{
				stringBuilder.append(inputLine);
			}

			return stringBuilder.toString();
		}
	}

	public static Entry<Integer, String> getURLSource(String url, String playerName) throws IOException
	{
		URL urlObject = new URL(url);
		HttpsURLConnection urlConnection = (HttpsURLConnection) urlObject.openConnection();

		//add reuqest header
		urlConnection.setRequestMethod("POST");
		urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

		String urlParameters = "playerName=" + playerName;

		// Send post request
		urlConnection.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		InputStream io = null;
		String bo = null;
		if (urlConnection != null)
		{
			if (urlConnection.getResponseCode() >= 200 &&  urlConnection.getResponseCode() <= 299)
			{
				io = urlConnection.getInputStream();
				bo = toString(io);
			}
		}

		return new AbstractMap.SimpleEntry<Integer, String>(urlConnection.getResponseCode(), bo);
	}

	
}