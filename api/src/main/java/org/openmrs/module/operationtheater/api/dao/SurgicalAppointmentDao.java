package org.openmrs.module.operationtheater.api.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SurgicalAppointmentDao {

    @Autowired
    SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public SurgicalAppointment save(SurgicalAppointment surgicalAppointment) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(surgicalAppointment);
        session.flush();
        return surgicalAppointment;
    }
}
