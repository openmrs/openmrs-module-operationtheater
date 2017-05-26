package org.openmrs.module.operationtheater.api.service;


import org.openmrs.api.OpenmrsService;
import org.openmrs.module.operationtheater.api.model.SurgicalBlock;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Transactional
public interface SurgicalBlockService extends OpenmrsService {
    SurgicalBlock save(SurgicalBlock surgicalBlock);

    SurgicalBlock getSurgicalBlockWithAppointments(String surgicalBlockUuid);

    List<SurgicalBlock> getSurgicalBlocksBetweenStartDatetimeAndEndDatetime(Date startDatetime, Date endDatetime);
}
