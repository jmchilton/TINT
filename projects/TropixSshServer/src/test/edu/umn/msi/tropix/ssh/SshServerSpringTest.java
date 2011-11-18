package edu.umn.msi.tropix.ssh;

import static org.testng.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Inject;

import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Joiner;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

import edu.umn.msi.tropix.common.test.ConfigDirBuilder;
import edu.umn.msi.tropix.common.test.FreshConfigTest;

@ContextConfiguration(locations = "test-context.xml")
public class SshServerSpringTest extends FreshConfigTest {

  @Inject
  private SshServerWrapper sshServer;

  @Inject
  private TestDataCreator testDataCreator;

  private Session session;

  private OutputStream latestChannelOutputStream;
  private InputStream latestChannelInputStream;
  private ChannelExec c;

  private ChannelSftp sftpChannel;

  @AfterClass(alwaysRun = true)
  public void tearDownServer() {
    sshServer.stop();
  }

  @Override
  protected void initializeConfigDir(final ConfigDirBuilder configDirBuilder) {
    final ConfigDirBuilder sshConfigDirBuilder = configDirBuilder.createSubConfigDir("ssh");
    sshConfigDirBuilder.getOutputContext("hostkey.pem").put(getClass().getResource("hostkey.pem"));
  }

  @BeforeClass(alwaysRun = true)
  public void initServer() throws JSchException {
    testDataCreator.create();
    sshServer.start();

    final JSch jsch = new JSch();
    session = jsch.getSession("admin", "localhost", SshServerWrapper.DEFAULT_PORT);
    final java.util.Properties config = new java.util.Properties();
    config.put("StrictHostKeyChecking", "no");
    session.setConfig(config);
    session.setPassword("admin");
    session.connect();
  }

  @Test(groups = "spring", expectedExceptions = SftpException.class)
  public void testCannotMoveObjectsNotInFolders() throws SftpException {
    sftpChannel.rename(buildPath(TestDataCreator.DATABASE_NAME, TestDataCreator.DATABASE_FILE_NAME), "/");
  }

  @Test(groups = "spring")
  public void testCanMoveObjectsInFolders() throws SftpException {
    sftpChannel.rename(TestDataCreator.TO_MOVE_FILE_NAME, "toloc");
    sftpChannel.lstat("toloc");
  }

  private String buildPath(final String... args) {
    return Joiner.on("/").join(args);
  }

  @Test(groups = "spring")
  public void testDelete() throws SftpException {
    final Iterable<?> iterable = sftpChannel.ls(".");
    for(Object object : iterable) {
      com.jcraft.jsch.ChannelSftp.LsEntry entry = (LsEntry) object;
      System.out.println(entry.getFilename());
    }
    // assert sftpChannel.lstat("to_delete") == null;
    sftpChannel.put(new ByteArrayInputStream("Hello World!".getBytes()), "to_delete");
    assert sftpChannel.lstat("to_delete") != null;
    sftpChannel.rm("to_delete"); // Will throw exception if fails
  }

  @Test(groups = "spring")
  public void testReadAndWrite() throws Exception {
    this.sendFile("/My Home/moo", "moo", "Hello World");
    assertEquals(this.readFile("/My Home/moo"), "Hello World");
  }

  @Test(groups = "spring")
  public void testReadAndWriteRelative() throws Exception {
    this.sendFile("./moo2", "moo2", "Hello World");
    assertEquals(this.readFile("./moo2"), "Hello World");
  }

  @BeforeMethod(groups = "spring")
  public void initSftp() throws JSchException {
    sftpChannel = (ChannelSftp) session.openChannel("sftp");
    sftpChannel.connect();
  }

  @AfterMethod(groups = "spring")
  public void closeSftp() {
    sftpChannel.exit();
  }

  @Test(groups = "spring")
  public void testMkDir() throws JSchException, SftpException {
    sftpChannel.mkdir("./moodir");
    sftpChannel.cd("./moodir");
  }

  @Test(groups = "spring")
  public void testCannotWriteExistingFile() throws SftpException {
    SftpATTRS attrs = sftpChannel.stat("Test Folder/Subfile");
    assert attrs.getPermissionsString().contains("r");
    assert !attrs.getPermissionsString().contains("w");
  }

  @Test(groups = "spring")
  public void testCannotWriteNonFolderObject() throws SftpException {
    SftpATTRS attrs = sftpChannel.stat("HUMAN");
    assert attrs.getPermissionsString().contains("r");
    assert !attrs.getPermissionsString().contains("w");
  }

  @Test(groups = "spring")
  public void testCantWriteToFolders() throws SftpException {
    SftpATTRS attrs = sftpChannel.stat("Test Folder");
    assert attrs.getPermissionsString().contains("r");
    assert attrs.getPermissionsString().contains("w");
  }

  @Test(groups = "spring")
  public void testCantWriteToCwd() throws SftpException {
    SftpATTRS attrs = sftpChannel.stat(".");
    assert attrs.getPermissionsString().contains("r");
    assert attrs.getPermissionsString().contains("w");
  }

  // @Test(groups = "spring", expectedExceptions = Exception.class)
  // public void testCannotWriteToAbsentDir() throws Exception {
  // this.sendFile("/moo-absent/cow", "cow", "Hello World", false);
  // }

  protected void sendFile(final String path, final String name, final String data) throws Exception {
    sendFile(path, name, data, true);
  }

  private void writeAndFlush(final String data, final boolean checkAck) throws IOException {
    latestChannelOutputStream.write(data.getBytes());
    flush(checkAck);
  }

  private void writeAndFlush0() throws IOException {
    latestChannelOutputStream.write(0);
    flush(false);
  }

  private void flush(final boolean checkAck) throws IOException {
    latestChannelOutputStream.flush();
    if(checkAck) {
      assertEquals(0, latestChannelInputStream.read());
    }
  }

  // From org.apache.sshd.ScpTest.java
  protected void sendFile(final String path, final String name, final String data, final boolean checkAck) throws Exception {
    final String command = "scp -t " + path;
    executeCommand(command);
    if(checkAck) {
      assertEquals(0, latestChannelInputStream.read());
    }
    writeAndFlush("C7777 " + data.length() + " " + name + "\n", checkAck);
    writeAndFlush(data, checkAck);
    writeAndFlush0();
    Thread.sleep(100);
    c.disconnect();
  }

  private void executeCommand(final String command) throws JSchException, IOException {
    c = (ChannelExec) session.openChannel("exec");
    c.setCommand(command);
    latestChannelOutputStream = c.getOutputStream();
    latestChannelInputStream = c.getInputStream();
    c.connect();

  }

  // From org.apache.sshd.ScpTest.java
  protected String readFile(final String path) throws Exception {
    executeCommand("scp -f " + path);
    final String header = readLine(latestChannelInputStream);
    writeAndFlush0();

    int length = Integer.parseInt(header.substring(6, header.indexOf(' ', 6)));
    byte[] buffer = new byte[length];
    length = latestChannelInputStream.read(buffer, 0, buffer.length);
    assertEquals(length, buffer.length);
    assertEquals(0, latestChannelInputStream.read());
    writeAndFlush0();

    c.disconnect();
    final String result = new String(buffer);
    assertEquals(String.format("C0644 %d %s", result.length(), path.substring(path.lastIndexOf("/") + 1)), header);
    return result;
  }

  // From org.apache.sshd.ScpTest.java
  private String readLine(final InputStream in) throws IOException {
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    for(;;) {
      int c = in.read();
      if(c == '\n') {
        return baos.toString();
      } else if(c == -1) {
        throw new IOException("End of stream");
      } else {
        baos.write(c);
      }
    }
  }

}
