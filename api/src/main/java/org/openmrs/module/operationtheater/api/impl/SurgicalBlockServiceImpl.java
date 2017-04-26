package org.openmrs.module.operationtheater.api.impl;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.operationtheater.SurgicalBlock;
import org.openmrs.module.operationtheater.api.SurgicalBlockService;
import org.openmrs.module.operationtheater.api.dao.SurgicalBlockDAO;
import org.openmrs.module.webservices.rest.web.response.IllegalPropertyException;
import org.springframework.beans.factory.annotation.Autowired;

public class SurgicalBlockServiceImpl extends BaseOpenmrsService implements SurgicalBlockService {

    @Autowired
    SurgicalBlockDAO surgicalBlockDAO;

    @Override
    public SurgicalBlock save(SurgicalBlock surgicalBlock) {
        if (surgicalBlock.getEndDatetime().before(surgicalBlock.getStartDatetime())) {
            throw new IllegalPropertyException("Surgical Block start date after end date");
        }
        if (!surgicalBlockDAO.getOverlappingSurgicalBlocks(surgicalBlock).isEmpty()) {
            throw new IllegalPropertyException("Surgical Block has conflicting time with existing block(s)");

        }
        return surgicalBlockDAO.save(surgicalBlock);
    }
}
