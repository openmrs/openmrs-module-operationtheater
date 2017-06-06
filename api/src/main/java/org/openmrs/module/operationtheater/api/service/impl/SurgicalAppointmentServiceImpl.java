package org.openmrs.module.operationtheater.api.service.impl;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.operationtheater.api.dao.SurgicalAppointmentDao;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointment;
import org.openmrs.module.operationtheater.api.service.SurgicalAppointmentService;
import org.openmrs.module.operationtheater.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class SurgicalAppointmentServiceImpl extends BaseOpenmrsService implements SurgicalAppointmentService{

    @Autowired
    SurgicalAppointmentDao surgicalAppointmentDao;

    public void setSurgicalAppointmentDao(SurgicalAppointmentDao surgicalAppointmentDao) {
        this.surgicalAppointmentDao = surgicalAppointmentDao;
    }

    @Override
    public SurgicalAppointment save(SurgicalAppointment surgicalAppointment) {
        List<SurgicalAppointment> overlappingSurgicalAppointments = surgicalAppointmentDao.getOverlappingActualTimeEntriesForAppointment(surgicalAppointment);
        if(overlappingSurgicalAppointments.size() > 0) {
            throw new ValidationException("Surgical Appointment has conflicting actual time with existing appointments in this OT");
        }
        return surgicalAppointmentDao.save(surgicalAppointment);
    }

}
