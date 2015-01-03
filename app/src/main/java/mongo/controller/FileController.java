package mongo.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author LALALA
 */
public class FileController {
    private FileWriter fw;
    private FileReader fr;
    private BufferedReader br;
    private FileController() {
    }
    
    public static FileController getInstance() {
        return WriterHolder.INSTANCE;
    }
    
    private static class WriterHolder {

        private static final FileController INSTANCE = new FileController();
    }
    
    public void CreateFile(String filename) {
        try {
            /* Remove the content of files */
            fw = new FileWriter(filename);
            if (fw != null) {
                fw.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(FileController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void WriteFile(File filename, String content)
    {
        WriteFile(filename, content, true);
    }
    
    public void WriteFile(String filename, String content)
    {
        WriteFile(filename, content, true);
    }
    
    public void WriteFile(String filename, String content, boolean appendLine)
    {
        WriteFile(new File(filename), content, appendLine);
    }
    
    public void WriteFile(File filename, String content, boolean appendLine)
    {
        try {
            fw = new FileWriter(filename, true);
            if(content==null)
                content = "";
            fw.write(content);
            if(appendLine)
                fw.write("\n");
        } catch (IOException ex) {
            Logger.getLogger(FileController.class.getName()).log(Level.SEVERE, null, ex);
        }finally
        {
            try {
                if(fw!=null) fw.close();
            } catch (IOException ex) {
                Logger.getLogger(FileController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public List<String> ReadFile(String filename)
    {
        List<String> result = new ArrayList();
        try {
            fr = new FileReader(filename);
            br = new BufferedReader(fr);
            String temp;
            while ((temp=br.readLine())!=null)
            {
                result.add(temp);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileController.class.getName()).log(Level.SEVERE, null, ex);
        } finally
        {
            try {
                if(br!=null) br.close();
                if(fr!=null) fr.close();
            } catch (IOException ex) {
                Logger.getLogger(FileController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }
    
    public String ReadFileOneLine(String filename)
    {
        StringBuilder result = new StringBuilder();
        try {
            fr = new FileReader(filename);
            br = new BufferedReader(fr);
            char[] temp = new char[32768];
            while (br.read(temp)!=-1)
            {
                result.append(temp);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileController.class.getName()).log(Level.SEVERE, null, ex);
        } finally
        {
            try {
                if(br!=null) br.close();
                if(fr!=null) fr.close();
            } catch (IOException ex) {
                Logger.getLogger(FileController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result.toString();
    }
}
