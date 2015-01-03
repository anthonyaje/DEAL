package mongo.controller;

import com.mongodb.DB;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 *
 * @author Lalala
 */
public class ImageController {

    private ImageController() {
    }

    public static ImageController getInstance() {
        return ImageControllerHolder.INSTANCE;
    }

    private static class ImageControllerHolder {

        private static final ImageController INSTANCE = new ImageController();
    }

    public String StoreImageFile(String id, byte[] image, DB db) {
        //Create GridFS object
        GridFS fs = new GridFS(db);
        //Save image into database
        GridFSInputFile in = fs.createFile(image);
        in.setFilename(id);
        in.save();
        if (in.getId() == null) {
            return null;
        }
        return in.getId().toString();
    }

    public byte[] LoadImage(String filePath) throws Exception {
        File file = new File(filePath);
        int size = (int) file.length();
        byte[] buffer = new byte[size];
        FileInputStream in = new FileInputStream(file);
        in.read(buffer);
        in.close();
        return buffer;
    }

    public byte[] readImageFromDatabase(String objectId, String imageId, DB db) {
        System.out.println("objId: " + objectId);
        System.out.println("imgId: " + imageId);
        //Create GridFS object
        GridFS fs = new GridFS(db);
        //Find saved image
        List<GridFSDBFile> find = fs.find(imageId);
        System.out.println("Find: " + find.size());
//        GridFSDBFile out = fs.findOne(new BasicDBObject("id", imageId));
        GridFSDBFile out = find.get(0);
        InputStream stream = out.getInputStream();
        int nRead;
        int offset = 0;
        byte[] data = new byte[(int) out.getLength()];
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            while ((nRead = stream.read()) != -1) {
                data[offset++] = (byte) nRead;
            }
        } catch (IOException ex) {

        }
        return data;
    }

    public void saveImageIntoFile(byte[] image, String filepath) {
//        try {
//            Files.write(Paths.get(filepath), image);
//        } catch (IOException ex) {
//            System.err.println("Couldn't create image file\n" + ex.getMessage());
//        }
    }
}
