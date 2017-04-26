package org.openmrs.module.operationtheater.api.impl;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.operationtheater.SurgicalBlock;
import org.openmrs.module.operationtheater.api.SurgicalBlockService;
import org.openmrs.module.operationtheater.api.dao.SurgicalBlockDAO;
import org.openmrs.module.webservices.rest.web.response.IllegalPropertyException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

public class SurgicalBlockServiceImpl extends BaseOpenmrsService implements SurgicalBlockService {

    @Autowired
    SurgicalBlockDAO surgicalBlockDAO;

    @Override
    public SurgicalBlock save(SurgicalBlock surgicalBlock) {
        if (surgicalBlock.getEndDatetime().before(surgicalBlock.getStartDatetime())) {
            throw new IllegalPropertyException("Surgical Block start date after end date");
        }
        if (!getOverlappingSurgicalBlocksForProvider(surgicalBlock).isEmpty()) {
            throw new IllegalPropertyException("Surgical Block has conflicting time with existing block(s) for this provider");
        }
        if (!getOverlappingSurgicalBlocksForLocation(surgicalBlock).isEmpty()) {
            throw new IllegalPropertyException("Surgical Block has conflicting time with existing block(s) for this OT");
        }

        return surgicalBlockDAO.save(surgicalBlock);
    }

    private ArrayList<SurgicalBlock> getOverlappingSurgicalBlocksForProvider(SurgicalBlock surgicalBlock) {
        return surgicalBlockDAO.getOverlappingSurgicalBlocksFor(
                surgicalBlock.getStartDatetime(), surgicalBlock.getEndDatetime(), surgicalBlock.getProvider(), null);
    }

    private ArrayList<SurgicalBlock> getOverlappingSurgicalBlocksForLocation(SurgicalBlock surgicalBlock) {
        return surgicalBlockDAO.getOverlappingSurgicalBlocksFor(
                surgicalBlock.getStartDatetime(), surgicalBlock.getEndDatetime(), null, surgicalBlock.getLocation());
    }
}
