package org.openmrs.module.operationtheater.api.model;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Location;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsUtil;

import java.util.Comparator;
import java.util.Date;
import java.util.Set;

public class SurgicalAppointmentAttribute extends BaseOpenmrsData implements java.io.Serializable, Comparable<SurgicalAppointmentAttribute>{

    private Integer id;
    private SurgicalAppointment surgicalAppointment;
    private SurgicalAppointmentAttributeType surgicalAppointmentAttributeType;
    private String value;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public SurgicalAppointment getSurgicalAppointment() {
        return surgicalAppointment;
    }

    public void setSurgicalAppointment(SurgicalAppointment surgicalAppointment) {
        this.surgicalAppointment = surgicalAppointment;
    }

    public SurgicalAppointmentAttributeType getSurgicalAppointmentAttributeType() {
        return surgicalAppointmentAttributeType;
    }

    public void setSurgicalAppointmentAttributeType(SurgicalAppointmentAttributeType surgicalAppointmentAttributeType) {
        this.surgicalAppointmentAttributeType = surgicalAppointmentAttributeType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void voidAttribute(String reason) {
        setVoided(true);
        setVoidedBy(Context.getAuthenticatedUser());
        setVoidReason(reason);
        setDateVoided(new Date());
    }

    @Override
    public int compareTo(SurgicalAppointmentAttribute other) {
        DefaultComparator paDComparator = new DefaultComparator();
        return paDComparator.compare(this, other);
    }

    public static class DefaultComparator implements Comparator<SurgicalAppointmentAttribute> {

        @Override
        public int compare(SurgicalAppointmentAttribute saa1, SurgicalAppointmentAttribute saa2) {
            int retValue;
            if ((retValue = OpenmrsUtil.compareWithNullAsGreatest(saa1.getSurgicalAppointmentAttributeType(), saa2.getSurgicalAppointmentAttributeType())) != 0) {
                return retValue;
            }
            if ((retValue = saa1.getVoided().compareTo(saa2.getVoided())) != 0) {
                return retValue;
            }
            if ((retValue = OpenmrsUtil.compareWithNullAsLatest(saa1.getDateCreated(), saa2.getDateCreated())) != 0) {
                return retValue;
            }
            if ((retValue = OpenmrsUtil.compareWithNullAsGreatest(saa1.getValue(), saa2.getValue())) != 0) {
                return retValue;
            }
            return OpenmrsUtil.compareWithNullAsGreatest(saa1.getId(), saa2.getId());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SurgicalAppointmentAttribute)) return false;
        if (!super.equals(o)) return false;

        SurgicalAppointmentAttribute that = (SurgicalAppointmentAttribute) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getSurgicalAppointmentAttributeType() != null ? !getSurgicalAppointmentAttributeType().equals(that.getSurgicalAppointmentAttributeType()) : that.getSurgicalAppointmentAttributeType() != null)
            return false;
        return getValue() != null ? getValue().equals(that.getValue()) : that.getValue() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getId() != null ? getId().hashCode() : 0);
        result = 31 * result + (getSurgicalAppointmentAttributeType() != null ? getSurgicalAppointmentAttributeType().hashCode() : 0);
        result = 31 * result + (getValue() != null ? getValue().hashCode() : 0);
        return result;
    }
}
