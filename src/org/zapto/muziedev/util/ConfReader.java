package org.zapto.muziedev.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Anton
 */
public class ConfReader {

    private String path = "";
    public static final int DATABASEHOST = 0, DATABASEPORT = 1, DATABASENAME = 2, LOGINPOS = 3, LOGINAREA = 4, MAXTRY = 5, WORLDNAME = 6, DATABASEUSER = 7, DATABASEPASSWORD = 8,DEFAULTSPAWN = 9,DEFAULTWORLD=10 ;

    public ConfReader(String path) {
        this.path = path;
    }

    public String[] getValue(int item) {
        String[] returnee = null;
        String[] searchItem = null;
        switch (item) {
            case DATABASEHOST:
                searchItem = new String[1];
                returnee = new String[1];
                searchItem[0] = "DatabaseAddres";
                break;
            case DATABASEPORT:
                searchItem = new String[1];
                returnee = new String[1];
                searchItem[0] = "DatabasePort";
                break;
            case DATABASENAME:
                searchItem = new String[1];
                returnee = new String[1];
                searchItem[0] = "DatabaseName";
                break;
            case LOGINPOS:
                searchItem = new String[3];
                returnee = new String[3];
                searchItem[0] = "LoginPosX";
                searchItem[1] = "LoginPosY";
                searchItem[2] = "LoginPosZ";
                break;
            case MAXTRY:
                searchItem = new String[1];
                returnee = new String[1];
                searchItem[0] = "DatabaseAddres";
                break;
            case WORLDNAME:
                searchItem = new String[1];
                returnee = new String[1];
                searchItem[0] = "WorldName";
                break;
            case DATABASEUSER:
                searchItem = new String[1];
                returnee = new String[1];
                searchItem[0] = "DatabaseUser";
                break;
            case DATABASEPASSWORD:
                searchItem = new String[1];
                returnee = new String[1];
                searchItem[0] = "DatabasePassword";
                break;
            case DEFAULTSPAWN:
                searchItem = new String[1];
                returnee = new String[1];
                searchItem[0] = "DefaultSpawn";                
                break;
            case DEFAULTWORLD:
                searchItem = new String[1];
                returnee = new String[1];
                searchItem[0] = "DefaultWorld";                
                break;
        }
        if (searchItem != null) {
            List<String> content;
            try {
                content = getFileContentPerLine(path);
                if (!content.get(0).equals("DISABLEDCONFIG")) {
                    int ri = 0;
                    for (String string : content) {
                        for (int i = 0; i < searchItem.length; i++) {
                            if (string.contains(searchItem[i])) {
                                String extracted = string.substring(string.indexOf("=") + 1);
                                returnee[ri] = extracted;
                                ri++;
                            }
                        }

                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(ConfReader.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return returnee;
    }

    public Vector3f getPos() {
        Vector3f returnee = null;
        String[] pos = getValue(LOGINPOS);
        if (pos != null && pos.length == 3) {
            returnee = new Vector3f(Float.parseFloat(pos[0]), Float.parseFloat(pos[1]), Float.parseFloat(pos[2]));
        }
        return returnee;
    }

    private static List<String> getFileContentPerLine(String path) throws IOException {
        List<String> returnee = new ArrayList();
        BufferedReader br = new BufferedReader(new FileReader(path));
        try {
            String strLine = br.readLine();
            while (strLine != null) {
                if (strLine != null && !strLine.equals("")) {
                    //Logger.getLogger(ConfReader.class.getName()).log(Level.INFO, strLine, strLine);
                    if (strLine.contains("##[DISABLED]")) {
                        returnee.clear();
                        returnee.add("DISABLEDCONFIG");
                        break;
                    } else if (!strLine.contains("#")) {
                        returnee.add(strLine);
                    }
                }
                strLine = br.readLine();
            }
        } catch (Exception ex) {
            Logger.getLogger(ConfReader.class.getName()).log(Level.INFO, ex.toString(), ex);
            returnee.clear();
        } finally {
            br.close();
        }
        return returnee;
    }

    public boolean evalConf() {
        boolean returnee = false;
        
        try {
            List<String> lines = ConfReader.getFileContentPerLine(path);
            if (lines != null && lines.size() > 0 && !lines.get(0).equals("DISABLEDCONFIG")) {
                returnee = true;
            }
        } catch (Exception ex) {
            Logger.getLogger(ConfReader.class.getName()).log(Level.INFO, ex.toString(), ex);
        }
        return returnee;
    }

    public static void createEmptyConf(String path) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer;
        writer = new PrintWriter(path, "UTF-8");
        writer.write(ConfTmpl);
    }
    public static final String ConfTmpl = "##[DISABLED] REMOVE THIS LINE TO ENABLE!\\nDatabaseAddres=[HOST]\\n#The ip address of the mysql\\nDatabasePort=[PORT]\\n#Port of the database\\nDatabaseName=[NAME]\\n#NameOfThePort\\nDatabaseUser=[USERNAME]\\n#Name of the user that can connect to the database\\nDatabasePassword=[PASSWORD]\\n#Password of the uses that can connect to the database\\nLoginPosX=0\\nLoginPosY=0\\nLoginPosZ=0\\n#Mid area where players spawn to login\\nLoginArea=5\\n#Area arround the previous position where players could spawn (in blocks)\\nMaxTryAgain=3\\n#times that a player can try to login\\nWorldName=[WORLDNAME]\\n#The name of the world";
}
