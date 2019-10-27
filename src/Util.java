import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Util {

	public static void writeMsg(DataOutputStream os, JSONObject json_msg) {
		String msg = json_msg.toString();
		System.out.println("json message " + msg.toString());
		System.out.println("length : " + msg.length());
		try {
			os.writeLong(msg.length());
			os.write(msg.getBytes());
			os.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void writeMsg(DataOutputStream os, String msg) {
		System.out.println("json message " + msg.toString());
		System.out.println("length : " + msg.length());
		try {
			os.writeLong(msg.length());
			os.write(msg.getBytes());
			os.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String readMsg(DataInputStream is) {
		byte size_of_msg_bytes[] = new byte[8];
		String msgreadtostr = "";
		try {
			is.read(size_of_msg_bytes);
			ByteBuffer wrapped = ByteBuffer.wrap(size_of_msg_bytes); // big-endian by default
			long size_of_msg = wrapped.getLong(); // 1
			byte msgread[] = new byte[(int) size_of_msg];
			is.read(msgread);
			msgreadtostr = new String(msgread);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return msgreadtostr;
	}

	public static JSONObject parseJSONObjectFromString(String s) throws JSONException {
		JSONObject jsonObject;
		jsonObject = new JSONObject(s);
		return jsonObject;
	}

	private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();
	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = HEX_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static List<String> JSONArrayToList(Object jsonObject) throws JSONException {
		ArrayList<String> list = new ArrayList<String>();     
		JSONArray jsonArray = (JSONArray)jsonObject; 
		if (jsonArray != null) { 
			int len = jsonArray.length();
			for (int i=0;i<len;i++){ 
				list.add(jsonArray.get(i).toString());
			}
		} 
		return list;
	}

	public static JSONArray listToJSONArray(List<String> liste) throws JSONException {
		JSONArray jsonArray = new JSONArray();
		if (liste != null) {
			int len = liste.size();
			for (int i=0;i<len;i++){
				jsonArray.put(liste.get(i));
			}
		}
		return jsonArray;
	}

	public static byte[] longToBytes(long x) {
	    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
	    buffer.putLong(x);
	    return buffer.array();
	}

	public static long bytesToLong(byte[] bytes) {
	    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
	    buffer.put(bytes);
	    buffer.flip();//need flip 
	    return buffer.getLong();
	}

	public static String bytesToString(byte[] b){
		return new String(b);
	}

	public static boolean isRequest(String msgJSON, String request){
		return msgJSON.contains(request);
	}

} 


