package com.multicraft;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVReadWrite {
    public List<List<String>> readCSV(String path) {
        List<List<String>> csv_data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] entries = line.split(",");
                csv_data.add(Arrays.asList(entries));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return csv_data;
    }

    public boolean writeCSV(String path, List<List<String>> data) {
        try (FileWriter fw = new FileWriter(path)) {
            for (List<String> rowData : data) {
                fw.append(String.join(",", rowData));
                fw.append("\n");
            }
            fw.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
