package org.openmrs.module.operationtheater.api.service.impl;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.operationtheater.api.dao.SurgicalAppointmentDao;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointment;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointmentAttribute;
import org.openmrs.module.operationtheater.api.service.SurgicalAppointmentService;
import org.openmrs.module.operationtheater.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class SurgicalAppointmentServiceImpl extends BaseOpenmrsService implements SurgicalAppointmentService{

    @Autowired
    SurgicalAppointmentDao surgicalAppointmentDao;

    public void setSurgicalAppointmentDao(SurgicalAppointmentDao surgicalAppointmentDao) {
        this.surgicalAppointmentDao = surgicalAppointmentDao;
    }

    @Override
    @Transactional
    public SurgicalAppointment save(SurgicalAppointment surgicalAppointment) {
        validateSurgicalAppointment(surgicalAppointment);
        return surgicalAppointmentDao.save(surgicalAppointment);
    }

    @Override
    public void validateSurgicalAppointment(SurgicalAppointment surgicalAppointment) {
        List<SurgicalAppointment> overlappingSurgicalAppointments = surgicalAppointmentDao.getOverlappingActualTimeEntriesForAppointment(surgicalAppointment);
        if(overlappingSurgicalAppointments.size() > 0) {
            throw new ValidationException("Surgical Appointment has conflicting actual time with existing appointments in this OT");
        }
    }

    @Override
    @Transactional
    public SurgicalAppointment getSurgicalAppointmentByUuid(String uuid) {
        return surgicalAppointmentDao.getSurgicalAppointmentByUuid(uuid);
    }

    @Override
    @Transactional
    public SurgicalAppointmentAttribute getSurgicalAppointmentAttributeByUuid(String uuid) {
        return surgicalAppointmentDao.getSurgicalAppointmentAttributeByUuid(uuid);
    }


}
