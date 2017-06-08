package kxd.net.client;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class FtpClient {
	protected String host, user, pwd;
	protected int port;
	protected FTPClient ftp;
	boolean connected;

	public FtpClient(String host, int port, String user, String pwd) {
		super();
		this.host = host;
		this.user = user;
		this.pwd = pwd;
		this.port = port;
		ftp = new FTPClient();
	}

	public void connect() throws IOException {
		if (connected)
			return;
		ftp.connect(host, port);
		if (!ftp.login(user, pwd)) {
			String reply = ftp.getReplyString();
			disconnect();
			throw new IOException(reply);
		}
		connected = true;
	}

	public void disconnect() {
		connected = false;
		if (ftp.isConnected()) {
			try {
				ftp.logout();
			} catch (Throwable e) {
			}
			try {
				ftp.disconnect();
			} catch (IOException e) {
			}
		}
	}

	public long lastModified(String remoteFile) throws IOException {
		connect();
		remoteFile = remoteFile.trim();
		if (!remoteFile.startsWith("/"))
			remoteFile = "/" + remoteFile;
		changeWorkingDirectory(remoteFile);
		FTPFile[] f = ftp.listFiles(remoteFile);
		if (f.length > 0) {
			return f[0].getTimestamp().getTimeInMillis();
		} else
			return 0;
	}

	public long[] lastModifiedAndSize(String remoteFile) throws IOException {
		connect();
		remoteFile = remoteFile.trim();
		if (!remoteFile.startsWith("/"))
			remoteFile = "/" + remoteFile;
		changeWorkingDirectory(remoteFile);
		FTPFile[] f = ftp.listFiles(remoteFile);
		if (f.length > 0) {
			return new long[] { f[0].getTimestamp().getTimeInMillis(),
					f[0].getSize() };
		} else
			return null;
	}

	public void download(String remoteFile, OutputStream stream)
			throws IOException {
		connect();
		remoteFile = remoteFile.trim();
		if (!remoteFile.startsWith("/"))
			remoteFile = "/" + remoteFile;
		changeWorkingDirectory(remoteFile);
		ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
		if (!ftp.retrieveFile(remoteFile, stream))
			throw new IOException("download failed: " + ftp.getReplyString());
	}

	public void download(String remoteFile, String localFile)
			throws IOException {
		download(remoteFile, new File(localFile));
	}

	public void download(String remoteFile, File localFile) throws IOException {
		remoteFile = remoteFile.trim();
		if (!remoteFile.startsWith("/"))
			remoteFile = "/" + remoteFile;
		changeWorkingDirectory(remoteFile);
		long t = lastModified(remoteFile);
		if (t == 0)
			throw new IOException("File not exists");
		FileOutputStream stream = new FileOutputStream(localFile);
		try {
			download(remoteFile, stream);
		} finally {
			stream.close();
		}
		localFile.setLastModified(t);
	}

	public void mkdirs(String remotePath) throws IOException {
		ArrayList<String> s = new ArrayList<String>();
		while (!remotePath.isEmpty() && !ftp.changeWorkingDirectory(remotePath)) {
			int index = remotePath.lastIndexOf("/");
			if (index > -1) {
				s.add(0, remotePath.substring(index + 1));
				remotePath = remotePath.substring(0, index);
			} else {
				s.add(remotePath);
				remotePath = "";
				break;
			}
		}
		for (String str : s) {
			remotePath += "/" + str;
			if (!ftp.makeDirectory(remotePath))
				throw new IOException("create dir failed: "
						+ ftp.getReplyString());
		}
	}

	private void changeWorkingDirectory(String remoteFile) throws IOException {
		int index = remoteFile.lastIndexOf("/");
		if (index > -1) {
			String dir = remoteFile.substring(0, index);
			ftp.changeWorkingDirectory(dir);
		}
	}

	/**
	 * 重命名
	 * 
	 * @param remoteFile
	 *            远程文件完整路径
	 * @param newFile
	 *            新的文件名，不含路径
	 * @throws IOException
	 */
	public void rename(String remoteFile, String newFile) throws IOException {
		connect();
		remoteFile = remoteFile.trim();
		newFile = newFile.trim();
		if (!remoteFile.startsWith("/"))
			remoteFile = "/" + remoteFile;
		int index = remoteFile.lastIndexOf("/");
		if (index > 0) {
			String dir = remoteFile.substring(0, index);
			mkdirs(dir);
			ftp.changeWorkingDirectory(dir);
			remoteFile = remoteFile.substring(index + 1);
		}
		ftp.rename(remoteFile, newFile);
	}

	public void append(String remoteFile, InputStream stream)
			throws IOException {
		connect();
		remoteFile = remoteFile.trim();
		if (!remoteFile.startsWith("/"))
			remoteFile = "/" + remoteFile;
		int index = remoteFile.lastIndexOf("/");
		if (index > 0) {
			String dir = remoteFile.substring(0, index);
			mkdirs(dir);
			ftp.changeWorkingDirectory(dir);
			remoteFile = remoteFile.substring(index + 1);
		}
		ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
		byte[] data = new byte[10240];
		int len;
		while ((len = stream.read(data)) > 0) {
			ftp.appendFile(remoteFile, new ByteArrayInputStream(data, 0, len));
		}
	}

	public void append(String remoteFile, byte[] data) throws IOException {
		connect();
		remoteFile = remoteFile.trim();
		if (!remoteFile.startsWith("/"))
			remoteFile = "/" + remoteFile;
		int index = remoteFile.lastIndexOf("/");
		if (index > 0) {
			String dir = remoteFile.substring(0, index);
			mkdirs(dir);
			ftp.changeWorkingDirectory(dir);
			remoteFile = remoteFile.substring(index + 1);
		}
		ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
		ftp.appendFile(remoteFile, new ByteArrayInputStream(data));
	}

	public void upload(String remoteFile, InputStream stream)
			throws IOException {
		connect();
		remoteFile = remoteFile.trim();
		if (!remoteFile.startsWith("/"))
			remoteFile = "/" + remoteFile;
		int index = remoteFile.lastIndexOf("/");
		if (index > 0) {
			String dir = remoteFile.substring(0, index);
			mkdirs(dir);
			ftp.changeWorkingDirectory(dir);
			remoteFile = remoteFile.substring(index + 1);
		}
		ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
		if (!ftp.storeFile(remoteFile, stream))
			throw new IOException("upload failed: " + ftp.getReplyString());
	}

	public void upload(String remoteFile, String localFile) throws IOException {
		upload(remoteFile, new File(localFile));
	}

	public void upload(String remoteFile, File localFile) throws IOException {
		FileInputStream stream = new FileInputStream(localFile);
		try {
			upload(remoteFile, stream);
		} finally {
			stream.close();
		}
	}

	public void delete(String remoteFile) throws IOException {
		connect();
		remoteFile = remoteFile.trim();
		if (!remoteFile.startsWith("/"))
			remoteFile = "/" + remoteFile;
		changeWorkingDirectory(remoteFile);
		FTPFile[] f = ftp.listFiles(remoteFile);
		if (f.length > 0)
			ftp.deleteFile(remoteFile);
	}

	public boolean isConnected() {
		return connected;
	}

}
