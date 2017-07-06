package org.openmrs.module.operationtheater.api.service;


import org.openmrs.api.OpenmrsService;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointment;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointmentAttribute;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface SurgicalAppointmentService extends OpenmrsService {
    SurgicalAppointment save(SurgicalAppointment surgicalAppointment);
    SurgicalAppointment getSurgicalAppointmentByUuid(String uuid);
    SurgicalAppointmentAttribute getSurgicalAppointmentAttributeByUuid(String uuid);
}
