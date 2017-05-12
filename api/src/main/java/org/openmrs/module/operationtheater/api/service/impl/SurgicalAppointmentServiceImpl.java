package org.openmrs.module.operationtheater.api.service.impl;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.operationtheater.api.dao.SurgicalAppointmentDao;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointment;
import org.openmrs.module.operationtheater.api.service.SurgicalAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;

public class SurgicalAppointmentServiceImpl extends BaseOpenmrsService implements SurgicalAppointmentService{

    @Autowired
    SurgicalAppointmentDao surgicalAppointmentDao;

    public void setSurgicalAppointmentDao(SurgicalAppointmentDao surgicalAppointmentDao) {
        this.surgicalAppointmentDao = surgicalAppointmentDao;
    }

    @Override
    public SurgicalAppointment save(SurgicalAppointment surgicalAppointment) {
        return surgicalAppointmentDao.save(surgicalAppointment);
    }

}
