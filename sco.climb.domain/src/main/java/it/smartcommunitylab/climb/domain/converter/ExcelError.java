package it.smartcommunitylab.climb.domain.converter;

public class ExcelError {
	private String sheet;
	private int row;
	private String error;
	
	public ExcelError() {}
	
	public ExcelError(String sheet, int row, String error) {
		this.sheet = sheet;
		this.row = row;
		this.error = error;
	}
	
	public String getSheet() {
		return sheet;
	}
	public void setSheet(String sheet) {
		this.sheet = sheet;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	
}
