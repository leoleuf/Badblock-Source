package fr.badblock.bukkit.games.pvpbox.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class JsonUtils {
	
	private static Gson gs = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).create();
	
	public static <T> T convert(JsonElement element, Class<T> clazz) {
		return gs.fromJson(element, clazz);
	}

	private static InputStreamReader getInputStream(File file)
			throws UnsupportedEncodingException, FileNotFoundException {
		return new InputStreamReader(new FileInputStream(file), Charsets.UTF_8.name());
	}

	public static <T> T load(File file, Class<T> clazz) {
		try {
			if (!file.exists())
				save(file, "{}");
			T t = gs.fromJson(getInputStream(file), clazz);
			if (t == null) {
				try
				{
					t = clazz.newInstance();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				
				JsonUtils.save(file, t, true);
			}
			return t;
		} catch (JsonSyntaxException | JsonIOException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		} catch (FileNotFoundException unused) {
			return null;
		}
	}
	
	public static <T> T load(File file, Type type, T def) {
		try {
			if (!file.exists())
				save(file, "{}");
			T t = gs.fromJson(getInputStream(file), type);
			if (t == null) {
				t = def;
				JsonUtils.save(file, t, true);
			}
			return t;
		} catch (JsonSyntaxException | JsonIOException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		} catch (FileNotFoundException unused) {
			return null;
		}
	}

	public static JsonArray loadArray(File file) {
		if (!file.exists() || file.length() == 0) {
			save(file, "[]");
		}

		try {
			JsonParser parser = new JsonParser();
			JsonElement jsonElement = parser.parse(getInputStream(file));

			return jsonElement.getAsJsonArray();
		} catch (FileNotFoundException unused) {

		} catch (JsonParseException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return new JsonArray();
	}

	public static JsonObject loadObject(File file) {
		if (!file.exists() || file.length() == 0) {
			save(file, "{}");
		}

		try {
			JsonParser parser = new JsonParser();
			JsonElement jsonElement = parser.parse(getInputStream(file));

			return jsonElement.getAsJsonObject();
		} catch (JsonParseException | UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException unused) {
		}

		return new JsonObject();
	}

	public static void save(File file, JsonElement element, boolean indented) {
		String toSave = !indented ? gs.toJson(element) : gs.toJson(element);
		;
		save(file, toSave);
	}

	public static void save(File file, Object object, boolean indented) {
		JsonElement element = gs.toJsonTree(object);
		save(file, element, indented);
	}

	public static void save(File file, String toSave) {
		try {
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8.name());

			writer.write(toSave);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
