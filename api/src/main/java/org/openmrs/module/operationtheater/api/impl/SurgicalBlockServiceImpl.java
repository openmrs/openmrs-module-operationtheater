package org.openmrs.module.operationtheater.api.impl;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.operationtheater.SurgicalBlock;
import org.openmrs.module.operationtheater.api.SurgicalBlockService;
import org.openmrs.module.operationtheater.api.dao.SurgicalBlockDAO;
import org.openmrs.module.operationtheater.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SurgicalBlockServiceImpl extends BaseOpenmrsService implements SurgicalBlockService {

    @Autowired
    SurgicalBlockDAO surgicalBlockDAO;

    public void setSurgicalBlockDAO(SurgicalBlockDAO surgicalBlockDAO) {
        this.surgicalBlockDAO = surgicalBlockDAO;
    }

    @Override
    public SurgicalBlock save(SurgicalBlock surgicalBlock) {
        if (surgicalBlock.getEndDatetime().before(surgicalBlock.getStartDatetime())) {
            throw new ValidationException("Surgical Block start date after end date");
        }
        if (!getOverlappingSurgicalBlocksForProvider(surgicalBlock).isEmpty()) {
            throw new ValidationException("Surgical Block has conflicting time with existing block(s) for this provider");
        }
        if (!getOverlappingSurgicalBlocksForLocation(surgicalBlock).isEmpty()) {
            throw new ValidationException("Surgical Block has conflicting time with existing block(s) for this OT");
        }
        return surgicalBlockDAO.save(surgicalBlock);
    }

    private List<SurgicalBlock> getOverlappingSurgicalBlocksForProvider(SurgicalBlock surgicalBlock) {
        return surgicalBlockDAO.getOverlappingSurgicalBlocksFor(
                surgicalBlock.getStartDatetime(), surgicalBlock.getEndDatetime(), surgicalBlock.getProvider(), null);
    }

    private List<SurgicalBlock> getOverlappingSurgicalBlocksForLocation(SurgicalBlock surgicalBlock) {
        return surgicalBlockDAO.getOverlappingSurgicalBlocksFor(
                surgicalBlock.getStartDatetime(), surgicalBlock.getEndDatetime(), null, surgicalBlock.getLocation());
    }
}
