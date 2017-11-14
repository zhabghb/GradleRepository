package com.cetc.hubble.metagrid.vo;

import java.util.List;

/**
 * Simple Java object class which stands for structured data.
 * 
 * @author tao
 *
 */
public class StructuredDataSetSample {

	private List<String> columnNames;
	private List<List<Object>> sampleDataSet;

	public StructuredDataSetSample() {
	}

	public StructuredDataSetSample(List<String> columnNames, List<List<Object>> sampleDataSet) {
		this.columnNames = columnNames;
		this.sampleDataSet = sampleDataSet;
	}

	public List<String> getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}

	public List<List<Object>> getTableSampleSet() {
		return sampleDataSet;
	}

	public void setTableSampleSet(List<List<Object>> tableSampleSet) {
		this.sampleDataSet = tableSampleSet;
	}

}