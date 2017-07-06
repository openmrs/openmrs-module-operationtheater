package org.openmrs.module.operationtheater.api.model;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Location;
import org.openmrs.Provider;

import java.util.*;

public class SurgicalBlock extends BaseOpenmrsData {

    private Integer id;
    private Provider provider;
    private Location location;
    private Date startDatetime;
    private Date endDatetime;
    private Set<SurgicalAppointment> surgicalAppointments;


    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Date getStartDatetime() {
        return startDatetime;
    }

    public void setStartDatetime(Date startDatetime) {
        this.startDatetime = startDatetime;
    }

    public Date getEndDatetime() {
        return endDatetime;
    }

    public void setEndDatetime(Date endDatetime) {
        this.endDatetime = endDatetime;
    }

    public Set<SurgicalAppointment> getSurgicalAppointments() {
        if (surgicalAppointments == null) {
            surgicalAppointments = new TreeSet<>();
        }
        return surgicalAppointments;
    }

    public void setSurgicalAppointments(Set<SurgicalAppointment> surgicalAppointments) {
        this.surgicalAppointments = surgicalAppointments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SurgicalBlock)) return false;
        if (!super.equals(o)) return false;

        SurgicalBlock that = (SurgicalBlock) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getProvider() != null ? !getProvider().equals(that.getProvider()) : that.getProvider() != null)
            return false;
        if (getLocation() != null ? !getLocation().equals(that.getLocation()) : that.getLocation() != null)
            return false;
        if (getStartDatetime() != null ? !getStartDatetime().equals(that.getStartDatetime()) : that.getStartDatetime() != null)
            return false;
        return getEndDatetime() != null ? getEndDatetime().equals(that.getEndDatetime()) : that.getEndDatetime() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getId() != null ? getId().hashCode() : 0);
        result = 31 * result + (getProvider() != null ? getProvider().hashCode() : 0);
        result = 31 * result + (getLocation() != null ? getLocation().hashCode() : 0);
        result = 31 * result + (getStartDatetime() != null ? getStartDatetime().hashCode() : 0);
        result = 31 * result + (getEndDatetime() != null ? getEndDatetime().hashCode() : 0);
        return result;
    }
}
