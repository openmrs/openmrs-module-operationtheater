package org.openmrs.module.operationtheater.api;


import org.openmrs.api.OpenmrsService;
import org.openmrs.module.operationtheater.SurgicalBlock;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface SurgicalBlockService extends OpenmrsService {
    SurgicalBlock save(SurgicalBlock surgicalBlock);
}
