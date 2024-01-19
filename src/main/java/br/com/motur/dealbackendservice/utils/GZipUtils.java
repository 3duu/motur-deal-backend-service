package br.com.motur.dealbackendservice.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZipUtils {

    private GZipUtils(){

    }

    public static byte[] compress(final String data) throws IOException {
        if (data != null && !data.isEmpty()) {

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(data.length());
            GZIPOutputStream gzip = new GZIPOutputStream(byteArrayOutputStream);
            gzip.write(data.getBytes());
            gzip.close();

            byte[] compressed = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();

            return compressed;
        }

        return null;
    }

    public static String compressToBase64(final String data) throws IOException {
        byte[] compress = compress(data);
        if (compress != null && compress.length > 0) {
            return Base64.getEncoder().encodeToString(compress);
        }
        return null;
    }

    public static String decompressBites(final byte[] compressed) throws IOException {
        if (compressed != null && compressed.length > 0) {

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressed);

            final GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gzipInputStream, StandardCharsets.UTF_8));

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            bufferedReader.close();
            gzipInputStream.close();
            byteArrayInputStream.close();

            return bufferedReader.toString();
        }

        return null;
    }

    public static String uncompressToString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        try {
            GZIPInputStream ungzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = ungzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            return out.toString(StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decompress(String orig) throws IOException {
        ByteArrayOutputStream baostream = new ByteArrayOutputStream();
        OutputStream outStream = new GZIPOutputStream(baostream);
        outStream.write(orig.getBytes(StandardCharsets.UTF_8));
        outStream.close();
        byte[] compressedBytes = baostream.toByteArray(); // toString not always possible

        // Uncompress it
        InputStream inStream = new GZIPInputStream(
                new ByteArrayInputStream(compressedBytes));
        ByteArrayOutputStream baoStream2 = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int len;
        while ((len = inStream.read(buffer)) > 0) {
            baoStream2.write(buffer, 0, len);
        }
        return baoStream2.toString(StandardCharsets.UTF_8);
    }

    public static String decompress(byte[] compressed) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
        GZIPInputStream gis = new GZIPInputStream(bis);
        BufferedReader br = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        gis.close();
        bis.close();
        return sb.toString();
    }

    public static String decompressFromBase64(final String base64CompressedData) throws IOException {
        byte[] decode = Base64.getDecoder().decode(base64CompressedData);
        if(decode != null && decode.length > 0) {
            return decompress(decode);
        }
        return null;
    }

}
