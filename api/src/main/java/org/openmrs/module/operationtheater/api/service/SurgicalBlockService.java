package org.openmrs.module.operationtheater.api.service;


import org.openmrs.api.OpenmrsService;
import org.openmrs.module.operationtheater.api.model.SurgicalBlock;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface SurgicalBlockService extends OpenmrsService {
    SurgicalBlock save(SurgicalBlock surgicalBlock);

    SurgicalBlock getSurgicalBlockWithAppointments(String surgicalBlockUuid);
}
