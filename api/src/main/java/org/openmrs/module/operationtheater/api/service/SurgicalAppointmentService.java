package org.openmrs.module.operationtheater.api.service;


import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointment;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointmentAttribute;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface SurgicalAppointmentService extends OpenmrsService {
    @Authorized({"Manage OT Schedules"})
    SurgicalAppointment save(SurgicalAppointment surgicalAppointment);

    @Authorized({"View OT Schedules"})
    SurgicalAppointment getSurgicalAppointmentByUuid(String uuid);

    @Authorized({"View OT Schedules"})
    SurgicalAppointmentAttribute getSurgicalAppointmentAttributeByUuid(String uuid);
}
