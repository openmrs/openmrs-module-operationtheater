package org.openmrs.module.operationtheater.api.service.impl;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointment;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointmentAttribute;
import org.openmrs.module.operationtheater.api.model.SurgicalBlock;
import org.openmrs.module.operationtheater.api.service.SurgicalAppointmentService;
import org.openmrs.module.operationtheater.api.service.SurgicalBlockService;
import org.openmrs.module.operationtheater.api.dao.SurgicalBlockDAO;
import org.openmrs.module.operationtheater.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SurgicalBlockServiceImpl extends BaseOpenmrsService implements SurgicalBlockService {

    @Autowired
    SurgicalBlockDAO surgicalBlockDAO;

    public void setSurgicalBlockDAO(SurgicalBlockDAO surgicalBlockDAO) {
        this.surgicalBlockDAO = surgicalBlockDAO;
    }

    @Override
    public SurgicalBlock save(SurgicalBlock surgicalBlock) {
        checkForOverlappingSurgicalBlocks(surgicalBlock);
        checkForOverlappingSurgicalAppointmentsForThePatient(surgicalBlock);
        return surgicalBlockDAO.save(surgicalBlock);
    }

    private void checkForOverlappingSurgicalAppointmentsForThePatient(SurgicalBlock surgicalBlock) {
        for (SurgicalAppointment surgicalAppointment : surgicalBlock.getSurgicalAppointments()) {
            List<SurgicalAppointment> overlappingSurgicalAppointmentsForPatient = surgicalBlockDAO.getOverlappingSurgicalAppointmentsForPatient(surgicalBlock.getStartDatetime(), surgicalBlock.getEndDatetime(), surgicalAppointment.getPatient());
            if (overlappingSurgicalAppointmentsForPatient.size() >0 ) {
                SurgicalAppointment conflictingSurgicalAppointment = overlappingSurgicalAppointmentsForPatient.get(0);
                SurgicalBlock conflictingSurgicalBlock = conflictingSurgicalAppointment.getSurgicalBlock();
                throw new ValidationException(conflictingSurgicalAppointment.getPatient().getGivenName() + " " + conflictingSurgicalAppointment.getPatient().getFamilyName()
                        + " has conflicting appointment at " + conflictingSurgicalBlock.getLocation().getDisplayString() + " with " + conflictingSurgicalBlock.getProvider().getName());
            }
        }
    }

    private void checkForOverlappingSurgicalBlocks(SurgicalBlock surgicalBlock) {
        if (surgicalBlock.getEndDatetime().before(surgicalBlock.getStartDatetime())) {
            throw new ValidationException("Surgical Block start date after end date");
        } else if (!getOverlappingSurgicalBlocksForProvider(surgicalBlock).isEmpty()) {
            throw new ValidationException("Surgical Block has conflicting time with existing block(s) for this provider");
        } else if (!getOverlappingSurgicalBlocksForLocation(surgicalBlock).isEmpty()) {
            throw new ValidationException("Surgical Block has conflicting time with existing block(s) for this OT");
        }
    }

    private List<SurgicalBlock> getOverlappingSurgicalBlocksForProvider(SurgicalBlock surgicalBlock) {
        return surgicalBlockDAO.getOverlappingSurgicalBlocksFor(
                surgicalBlock.getStartDatetime(), surgicalBlock.getEndDatetime(), surgicalBlock.getProvider(), null, surgicalBlock.getId());
    }

    private List<SurgicalBlock> getOverlappingSurgicalBlocksForLocation(SurgicalBlock surgicalBlock) {
        return surgicalBlockDAO.getOverlappingSurgicalBlocksFor(
                surgicalBlock.getStartDatetime(), surgicalBlock.getEndDatetime(), null, surgicalBlock.getLocation(), surgicalBlock.getId());
    }
}
