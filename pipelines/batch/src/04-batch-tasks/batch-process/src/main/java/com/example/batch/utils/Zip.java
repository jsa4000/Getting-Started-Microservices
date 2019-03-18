package com.example.batch.utils;

import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Zip {

    public static int unZip(Resource src, String dest) throws IOException {
        byte[] buffer = new byte[1024];
        File destDir = new File(dest);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zis = new ZipInputStream(src.getInputStream());
        ZipEntry zipEntry = zis.getNextEntry();
        int result = 0;
        while (zipEntry != null) {
            File newFile = newFile(destDir, zipEntry);
            result ++;
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
        return result;
    }

    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }
}
