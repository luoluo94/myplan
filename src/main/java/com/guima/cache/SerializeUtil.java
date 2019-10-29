package com.guima.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializeUtil {
	public static byte[] serialize(Object object) throws IOException {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			// 序列化
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			byte[] bytes = baos.toByteArray();
			return bytes;

		} catch (IOException e) {
          baos.close();
          oos.close();
          
		}

		return null;

	}

	public static Object unserialize(byte[] bytes)  throws IOException, ClassNotFoundException  {

		ByteArrayInputStream bais = null;
		ObjectInputStream ois=null;
		try {
			// 反序列化
			 bais = new ByteArrayInputStream(bytes);
			 ois = new ObjectInputStream(bais);
			 return ois.readObject();
		} catch (IOException e) {
           bais.close();
           ois.close();
           
		}

		return null;

	}
	

}
