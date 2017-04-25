package com.util;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * Created by mengdacheng on 2017/3/30.
 */
public class ByteUtil {
    /**
     * 将对象序列化为字节数组
     * @param obj
     * @return
     * @throws IOException
     */
    public static byte[] getBytes(Object obj) throws IOException{
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bout);
        out.writeObject(obj);
        out.flush();
        byte[] bytes = bout.toByteArray();
        bout.close();
        out.close();
        return bytes;
    }

    /**
     * 字节数组反序列化为对象
     * @param bytes
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object getObject(byte[] bytes) throws IOException, ClassNotFoundException{
        ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
        ObjectInputStream oi = new ObjectInputStream(bi);
        Object obj = oi.readObject();
        bi.close();
        oi.close();
        return obj;
    }

    /**
     * 根据对象得到缓冲数组
     * @param obj
     * @return
     * @throws IOException
     */
    public static ByteBuffer getByteBuffer(Object obj) throws IOException{
        byte[] bytes = ByteUtil.getBytes(obj);
        ByteBuffer buff = ByteBuffer.wrap(bytes);
        return buff;
    }
}
