package io.nio.myokhttp.body;

import io.nio.myokhttp.ContentType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class ResponseBody {

    public abstract ContentType contentType();

    public long contentLength() { return -1L; }

    public abstract InputStream byteStream() throws IOException;

    public String string() {
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        try {
            inputStream = byteStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder(inputStream.available());
            String line = null;
            String separator = System.getProperty("line.separator");
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append(separator);
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}
