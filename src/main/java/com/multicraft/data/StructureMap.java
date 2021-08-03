package com.multicraft.data;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

public class StructureMap {

    private final HashMap<String, String[]> structureDataMap;
    private final String path;

    public StructureMap(String path) {
        this.path = path;
        structureDataMap = StructureFileHandler.readFile(path);
    }

    public boolean setStructureData(String[] data) {
        String[] oldData = structureDataMap.put(data[0], Arrays.copyOfRange(data, 1, data.length));
        return (oldData != null);
    }

    public String[] getStructureData(String name) {
        return structureDataMap.get(name);
    }

    public void saveStructureData() {
        StructureFileHandler.writeFile(this.path, this.structureDataMap);
    }

    private static class StructureFileHandler {

        public static HashMap<String, String[]> readFile(String path) {
            File structureFile = new File(path);
            HashMap<String, String[]> csvData = new HashMap<>();
            try {
                structureFile.getParentFile().mkdirs();
                if (!structureFile.createNewFile()) {
                    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            String[] entry = line.split(",");
                            csvData.put(entry[0], Arrays.copyOfRange(entry, 1, entry.length));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException | SecurityException e) {
                e.printStackTrace();
            }
            return csvData;
        }

        public static void writeFile(String path, HashMap<String, String[]> structureDataMap) {
            try (FileWriter fw = new FileWriter(path)) {
                for (String structureDataKey : structureDataMap.keySet()) {
                    fw.append(structureDataKey).append(",");
                    fw.append(String.join(",", structureDataMap.get(structureDataKey)));
                    fw.append("\n");
                }
                fw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}

