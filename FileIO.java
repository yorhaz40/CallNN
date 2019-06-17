package jtags;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class FileIO
{
    public static PrintStream logStream;
    
    static {
        try {
            FileIO.logStream = new PrintStream(new FileOutputStream("log.txt"));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public static String getSimpleFileName(final String fileName) {
        char separator = '/';
        if (fileName.lastIndexOf(92) > -1) {
            separator = '\\';
        }
        final int start = fileName.lastIndexOf(separator) + 1;
        int end = fileName.lastIndexOf(46);
        if (end <= start) {
            end = fileName.length();
        }
        return fileName.substring(start, end);
    }
    
    public static String getSimpleClassName(final String className) {
        final String name = className.substring(className.lastIndexOf(46) + 1);
        return name;
    }
    
    public static String getSVNRepoRootName(final String url) {
        String name = "";
        int end;
        for (end = url.length() - 1; url.charAt(end) == '/' && end >= 0; --end) {}
        if (end >= 0) {
            final int start = url.lastIndexOf(47, end);
            if (start <= end) {
                name = url.substring(start + 1, end + 1);
            }
        }
        return name;
    }
    
    public static String[] splitFileName(final String fileName) {
        char separator = '/';
        if (fileName.lastIndexOf(92) > -1) {
            separator = '\\';
        }
        final int start = fileName.lastIndexOf(separator) + 1;
        int end = fileName.lastIndexOf(46);
        if (end <= start) {
            end = fileName.length() + 1;
        }
        final String[] names = { fileName.substring(0, start - 1), fileName.substring(start, end) };
        return names;
    }
    
    public static String readStringFromFile(final String inputFile) {
        try {
            final BufferedInputStream in = new BufferedInputStream(new FileInputStream(inputFile));
            final byte[] bytes = new byte[(int)new File(inputFile).length()];
            in.read(bytes);
            in.close();
            return new String(bytes);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public static void writeStringToFile(final String string, final String outputFile) {
        try {
            final BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write(string);
            writer.flush();
            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
    
    public static void writeObjectToFile(final Object object, final String objectFile, final boolean append) {
        try {
            final ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(objectFile, append)));
            out.writeObject(object);
            out.flush();
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
    
    public static Object readObjectFromFile(final String objectFile) {
        try {
            final ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(objectFile)));
            final Object object = in.readObject();
            in.close();
            return object;
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public static int countLOC(final File file, final String extension) {
        int numOfLines = 0;
        if (file.isDirectory()) {
            File[] listFiles;
            for (int length = (listFiles = file.listFiles()).length, i = 0; i < length; ++i) {
                final File sub = listFiles[i];
                numOfLines += countLOC(sub, extension);
            }
        }
        else if (file.getName().endsWith("." + extension)) {
            try {
                @SuppressWarnings("resource")
				final BufferedReader in = new BufferedReader(new FileReader(file));
                while (in.readLine() != null) {
                    ++numOfLines;
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return numOfLines;
    }
    
    public static String getHtmlPageContent(final String url, final String query, final String charset) throws MalformedURLException, IOException {
        final URLConnection connection = new URL(String.valueOf(url) + "?" + query).openConnection();
        connection.setRequestProperty("Accept-Charset", charset);
        connection.setReadTimeout(10000);
        final InputStream response = connection.getInputStream();
        final StringBuilder sb = new StringBuilder();
        final BufferedInputStream in = new BufferedInputStream(response);
        final byte[] bytes = new byte[10000];
        for (int len = in.read(bytes); len != -1; len = in.read(bytes)) {
            sb.append(new String(bytes, 0, len));
        }
        in.close();
        return sb.toString();
    }
    
    public static ArrayList<String> getAllFilesInFolder(final String folder) {
        final ArrayList<String> allFiles = new ArrayList<String>();
        File[] listFiles;
        for (int length = (listFiles = new File(folder).listFiles()).length, i = 0; i < length; ++i) {
            final File file = listFiles[i];
            if (file.isFile()) {
                System.out.println(String.valueOf(file.getName()) + ":" + file.length());
                allFiles.add(file.getPath());
            }
            else {
                allFiles.addAll(getAllFilesInFolder(file.getPath()));
            }
        }
        return allFiles;
    }
}
