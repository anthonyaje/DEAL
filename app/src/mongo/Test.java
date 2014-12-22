package mongo;

import cloud.controller.DbController;
import cloud.controller.ImageController;
import cloud.entity.Setting;
import cloud.entity.User;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lalala
 */
public class Test {
    static String imageId = null;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        DbController.getInstance().Open();
        //testEntityUser();
        testEntitySetting();
        DbController.getInstance().Close();
    }
    
    /* Entity Setting */
    private static void testEntitySetting(){
        testDeleteTable(new Setting().getCollectionName());
        testInsertSetting();
        testReadSetting("lalala");
    }
    
    private static void testInsertSetting()
    {
        Setting s = new Setting();
        s.setUser_id("lalala");
        s.getSettings()[0] = "100";
    }
    
    private static void testReadSetting(String user)
    {
        
    }
    
    /* Entity User */
    
    private static void testEntityUser(){
        testDeleteTable(new User().getCollectionName());
        testInsertUser("/Users/Lalala/a.jpeg");
        testReadUser("username", "Lalala", "/Users/Lalala/b.jpeg");
    }
    
    private static void testDeleteTable(String table)
    {
        DbController.getInstance().deleteCollection(table);
    }
    
    private static void testInsertUser(String imageFile)
    {
        User u = new User();
        u.setUsername("Lalala");
        u.setPassword("lalala");
        u.setEmail("gunlalala@gmail.com");
        try {
            System.out.println("Read image file: "+imageFile);
            byte[] LoadImage = ImageController.getInstance().LoadImage(imageFile);
            u.setPicture(LoadImage);
            System.out.print("Is the picture null: ");
            System.out.println(u.getPicture()==null);
        } catch (Exception ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
        DBCollection collection = DbController.getInstance().getCollection(u.getCollectionName());
        imageId = u.insertData(u, collection, u.getPicture());
    }
    
    private static void testReadUser(String column, String value, String outputImage)
    {
        User u = new User();
        DBCollection collection = DbController.getInstance().getCollection(u.getCollectionName());
        DBCursor filterCollection = DbController.getInstance().filterCollection(collection, column, value);
        Iterator<DBObject> iterator = filterCollection.iterator();
        while(iterator.hasNext())
        {
            DBObject next = iterator.next();
            u = new User(next);
            System.out.println(next);
            System.out.println("Test imageId: "+imageId);
            // Both method to get the picture is works fine
            byte[] picture = u.getPicture();
            // byte[] picture = ImageController.getInstance().readImageFromDatabase(next.get("id").toString(), next.get("id").toString(), DbController.getInstance().getDB());
            ImageController.getInstance().saveImageIntoFile(picture, outputImage);
            System.out.println("Create a picture: "+outputImage);
        }
    }
    
}
