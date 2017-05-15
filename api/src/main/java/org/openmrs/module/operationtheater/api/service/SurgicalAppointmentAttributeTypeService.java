package org.openmrs.module.operationtheater.api.service;


import org.openmrs.api.OpenmrsService;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointmentAttributeType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface SurgicalAppointmentAttributeTypeService extends OpenmrsService {
    List<SurgicalAppointmentAttributeType> getAllAttributeTypes();
}
