package io.bridge.linker.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public enum  FileUtils {
    INSTANCE;
    public byte[] getBytes(String filePath) {
        byte[] buffer = null;
        File file = new File(filePath);
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream bos = new ByteArrayOutputStream(1000)) {
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            log.error("file not found.", e);
        } catch (IOException e) {
            log.error("file read error.", e);
        }
        return buffer;
    }

}
