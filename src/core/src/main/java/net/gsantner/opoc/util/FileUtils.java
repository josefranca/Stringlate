/*
 * ------------------------------------------------------------------------------
 * Gregor Santner <gsantner.github.io> & Lonami Exo <lonamiwebs.github.io> wrote
 * this. You can do whatever you want with it. If we meet some day, and you
 * think it is worth it, you can buy us a coke in return. Provided as is without
 * any kind of warranty. Do not blame or sue us if something goes wrong.
 * No attribution required.    - Gregor Santner & Lonami Exo
 *
 * License: Creative Commons Zero (CC0 1.0)
 *  http://creativecommons.org/publicdomain/zero/1.0/
 * ----------------------------------------------------------------------------
 */
package net.gsantner.opoc.util;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"WeakerAccess", "unused", "SameParameterValue", "SpellCheckingInspection", "deprecation"})
public class FileUtils {
    // Used on methods like copyFile(src, dst)
    private static final int BUFFER_SIZE = 4096;

    public static String readTextFile(final File file) {
        try {
            return readCloseTextStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            System.err.println("readTextFile: File " + file + " not found.");
        }

        return "";
    }

    public static String readCloseTextStream(final InputStream stream) {
        return readCloseTextStream(stream, true).get(0);
    }

    public static List<String> readCloseTextStream(final InputStream stream, boolean concatToOneString) {
        final ArrayList<String> lines = new ArrayList<>();
        BufferedReader reader = null;
        String line = "";
        try {
            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(stream));

            while ((line = reader.readLine()) != null) {
                if (concatToOneString) {
                    sb.append(line).append('\n');
                } else {
                    lines.add(line);
                }
            }
            line = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (concatToOneString) {
            lines.clear();
            lines.add(line);
        }
        return lines;
    }

    public static boolean writeFile(final File file, final String content) {
        BufferedWriter writer = null;
        try {
            if (!file.getParentFile().isDirectory() && !file.getParentFile().mkdirs())
                return false;

            writer = new BufferedWriter(new FileWriter(file));
            writer.write(content);
            writer.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean copyFile(final File src, final File dst) {
        InputStream is = null;
        FileOutputStream os = null;
        try {
            try {
                is = new FileInputStream(src);
                os = new FileOutputStream(dst);
                byte[] buf = new byte[BUFFER_SIZE];
                int len;
                while ((len = is.read(buf)) > 0) {
                    os.write(buf, 0, len);
                }
            } finally {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            }
        } catch (IOException ex) {
            return false;
        }
        return true;
    }

    // Returns -1 if the file did not contain any of the needles, otherwise,
    // the index of which needle was found in the contents of the file.
    //
    // Needless MUST be in lower-case.
    public static int fileContains(File file, String... needles) {
        try {
            FileInputStream in = new FileInputStream(file);

            int i;
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            while ((line = reader.readLine()) != null) {
                for (i = 0; i != needles.length; ++i)
                    if (line.toLowerCase().contains(needles[i])) {
                        return i;
                    }
            }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static boolean deleteRecursive(final File file) {
        boolean ok = true;
        if (file.exists()) {
            if (file.isDirectory()) {
                for (File child : file.listFiles())
                    ok &= deleteRecursive(child);
            }
            ok &= file.delete();
        }
        return ok;
    }
}
