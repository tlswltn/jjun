package dto;

import java.io.File;

public class BoardFileDTO {
	private int fileNumber;
	private int postNumber;
	private String filePath;
	private String fileName;

	public BoardFileDTO() {
		super();
	}

	public int getFileNumber() {
		return fileNumber;
	}

	public void setFileNumber(int fileNumber) {
		this.fileNumber = fileNumber;
	}

	public int getPostNumber() {
		return postNumber;
	}

	public void setPostNumber(int postNumber) {
		this.postNumber = postNumber;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public String toString() {
		return "BoardFileDTO [fileNumber=" + fileNumber + ", postNumber=" + postNumber + ", filePath=" + filePath + "]";
	}

}
