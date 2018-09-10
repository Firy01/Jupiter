package rpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import entity.Item;

public class RpcHelper {
	public static void writeJSONObj(HttpServletResponse response, JSONObject obj) {
		response.setContentType("application/json");
		response.addHeader("Access-Control-Allow-Origin", "*");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.format("%s", obj);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			out.close();
		}

	}

	public static void writeJSONArray(HttpServletResponse response, JSONArray array) {
		response.setContentType("application/json");
		response.addHeader("Access-Control-Allow-Origin", "*");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.format("%s", array);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			out.close();
		}
	}

	// Parses a JSONObject from http request.
	public static JSONObject readJsonObject(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader reader = request.getReader()) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			System.out.println(sb);
			return new JSONObject(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new JSONObject();

	}

	public static JSONArray getJSONArray(List<Item> items) {
		JSONArray result = new JSONArray();
		try {
			for (Item item : items) {
				result.put(item.toJSONObject());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
