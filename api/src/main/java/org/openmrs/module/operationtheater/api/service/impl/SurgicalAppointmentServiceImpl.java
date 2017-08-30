package org.openmrs.module.operationtheater.api.service.impl;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.operationtheater.api.dao.SurgicalAppointmentDao;
import org.openmrs.module.operationtheater.api.dao.SurgicalBlockDAO;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointment;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointmentAttribute;
import org.openmrs.module.operationtheater.api.model.SurgicalBlock;
import org.openmrs.module.operationtheater.api.service.SurgicalAppointmentService;
import org.openmrs.module.operationtheater.exception.ValidationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class SurgicalAppointmentServiceImpl extends BaseOpenmrsService implements SurgicalAppointmentService{

    private SurgicalAppointmentDao surgicalAppointmentDao;
    private SurgicalBlockDAO surgicalBlockDao;

    public void setSurgicalAppointmentDao(SurgicalAppointmentDao surgicalAppointmentDao) {
        this.surgicalAppointmentDao = surgicalAppointmentDao;
    }

    public void setSurgicalBlockDao(SurgicalBlockDAO surgicalBlockDao) {
        this.surgicalBlockDao = surgicalBlockDao;
    }

    @Override
    @Transactional
    public SurgicalAppointment save(SurgicalAppointment surgicalAppointment) {
        validateSurgicalAppointment(surgicalAppointment);
        validatePatientForConflictingSurgicalAppointment(surgicalAppointment);
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

    private void validatePatientForConflictingSurgicalAppointment(SurgicalAppointment surgicalAppointment) {
        List<SurgicalAppointment> overlappingSurgicalAppointmentsForPatient = surgicalBlockDao.getOverlappingSurgicalAppointmentsForPatient(surgicalAppointment.getSurgicalBlock().getStartDatetime(), surgicalAppointment.getSurgicalBlock().getEndDatetime(), surgicalAppointment.getPatient(), surgicalAppointment.getSurgicalBlock().getId());
        if (overlappingSurgicalAppointmentsForPatient.size() >0 ) {
            SurgicalAppointment conflictingSurgicalAppointment = overlappingSurgicalAppointmentsForPatient.get(0);
            SurgicalBlock conflictingSurgicalBlock = conflictingSurgicalAppointment.getSurgicalBlock();
            throw new ValidationException(conflictingSurgicalAppointment.getPatient().getGivenName() + " " + conflictingSurgicalAppointment.getPatient().getFamilyName()
                + " has conflicting appointment at " + conflictingSurgicalBlock.getLocation().getDisplayString() + " with " + conflictingSurgicalBlock.getProvider().getName());
        }
    }
}
