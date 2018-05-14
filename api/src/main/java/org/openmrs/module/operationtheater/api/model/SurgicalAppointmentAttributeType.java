package org.openmrs.module.operationtheater.api.model;

import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.OpenmrsData;
import org.openmrs.User;
import org.openmrs.util.OpenmrsUtil;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

public class SurgicalAppointmentAttributeType extends BaseOpenmrsMetadata implements Serializable, Comparable<SurgicalAppointmentAttributeType> {
	
	private Integer id;
	
	private String name;
	
	private String description;
	
	private String format;
	
	private Integer sortWeight;
	
	@Override
	public Integer getId() {
		return id;
	}
	
	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getFormat() {
		return format;
	}
	
	public void setFormat(String format) {
		this.format = format;
	}
	
	public Integer getSortWeight() {
		return sortWeight;
	}
	
	public void setSortWeight(Integer sortWeight) {
		this.sortWeight = sortWeight;
	}
	
	@Override
	public int compareTo(SurgicalAppointmentAttributeType other) {
		SurgicalAppointmentAttributeType.DefaultComparator patDefaultComparator = new SurgicalAppointmentAttributeType.DefaultComparator();
		return patDefaultComparator.compare(this, other);
	}
	
	public static class DefaultComparator implements Comparator<SurgicalAppointmentAttributeType> {
		
		public DefaultComparator() {
		}
		
		public int compare(SurgicalAppointmentAttributeType pat1, SurgicalAppointmentAttributeType pat2) {
			return OpenmrsUtil.compareWithNullAsGreatest(pat1.getId(), pat2.getId());
		}
	}
}
