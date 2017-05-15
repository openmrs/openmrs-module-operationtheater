package org.openmrs.module.operationtheater.api.service.impl;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.operationtheater.api.dao.SurgicalAppointmentAttributeTypeDAO;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointmentAttributeType;
import org.openmrs.module.operationtheater.api.service.SurgicalAppointmentAttributeTypeService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class SurgicalAppointmentAttributeTypeServiceImpl extends BaseOpenmrsService implements SurgicalAppointmentAttributeTypeService {

    @Autowired
    SurgicalAppointmentAttributeTypeDAO surgicalAppointmentAttributeTypeDAO;

    public void setSurgicalAppointmentAttributeTypeDAO(SurgicalAppointmentAttributeTypeDAO surgicalAppointmentAttributeTypeDAO) {
        this.surgicalAppointmentAttributeTypeDAO = surgicalAppointmentAttributeTypeDAO;
    }

    @Override
    public List<SurgicalAppointmentAttributeType> getAllAttributeTypes() {
        return surgicalAppointmentAttributeTypeDAO.getAllAttributeTypes();
    }
}
