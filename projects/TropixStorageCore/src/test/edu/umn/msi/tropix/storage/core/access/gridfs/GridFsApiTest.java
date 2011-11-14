package edu.umn.msi.tropix.storage.core.access.gridfs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.UUID;

import org.testng.annotations.Test;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;

import edu.umn.msi.tropix.common.io.InputContexts;

public class GridFsApiTest {

  @Test(groups = "interaction")
  public void testApi() throws MongoException, IOException {
    final Mongo m = new Mongo( "127.0.0.1" , 27017 );
    final DB db = m.getDB( "mydb" );
    final GridFS gridFs = new GridFS(db);
    String filename = UUID.randomUUID().toString();
    
    final GridFSInputFile input = gridFs.createFile(filename);
    OutputStream outputStream = input.getOutputStream();
    InputContexts.forString("Hello World!").get(outputStream);
    outputStream.close();
    final ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
    gridFs.findOne(filename).writeTo(byteOutputStream);
    final String contents = new String(byteOutputStream.toByteArray());
    assert contents.equals("Hello World!") : contents;
    assert gridFs.findOne(UUID.randomUUID().toString()) == null;
  }
  
}
