package cn.nodemedia.library;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 自动生成Android屏幕适配的dimens.xml
 * Created by Bining.
 */
public class DimenTool {

    public static final int BASEWIDTH = 360;

    public static void main(String[] args) {
        //baseDim();
        gen();
    }

    public static void baseDim() {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("<resources>").append("\n");
        for (int i = 0; i < 1441; i++) {
            stringBuilder.append("    <dimen name=\"common_dp_" + i + "\">" + i + "dp</dimen>").append("\n");
        }
        stringBuilder.append("\n");
        for (int i = 0; i < 145; i++) {
            stringBuilder.append("    <dimen name=\"common_sp_" + i + "\">" + i + "sp</dimen>").append("\n");
        }
        stringBuilder.append("</resources>").append("\n");

        writeFile("./library/src/main/res/values", stringBuilder.toString());
    }

    public static void gen() {

        String baseDimPath = "./library/src/main/res/values/dimens.xml";
        int[] widthDPs = new int[]{320, 360, 480};

        BufferedReader reader = null;

        List<StringBuilder> sbList = new ArrayList<>();

        for (int widthDP : widthDPs) {
            sbList.add(new StringBuilder());
        }

        try {
            System.out.println("生成不同分辨率：");
            reader = new BufferedReader(new FileReader(new File(baseDimPath)));
            String tempString;

            while ((tempString = reader.readLine()) != null) {
                if (tempString.contains("</dimen>")) {
                    String start = tempString.substring(0, tempString.indexOf(">") + 1);
                    String end = tempString.substring(tempString.lastIndexOf("<") - 2);
                    float num = Float.valueOf(tempString.substring(tempString.indexOf(">") + 1, tempString.indexOf("</dimen>") - 2));

                    for (int i = 0; i < widthDPs.length; i++) {
                        sbList.get(i).append(start).append((Math.round(num * 10 * widthDPs[i] / BASEWIDTH) / 10.0)).append(end).append("\n");
                    }
                } else {
                    for (int i = 0; i < widthDPs.length; i++) {
                        sbList.get(i).append(tempString).append("\n");
                    }
                }
            }
            reader.close();

            for (int i = 0; i < widthDPs.length; i++) {
                writeFile("./library/src/main/res/values-sw" + widthDPs[i] + "dp", sbList.get(i).toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static void writeFile(String directory, String text) {
        try {
            File dfile = new File(directory);
            if (!dfile.exists()) {
                dfile.mkdirs();
            }
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new File(dfile, "dimens.xml"))));
            out.println(text);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}