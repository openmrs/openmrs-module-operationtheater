package org.openmrs.module.operationtheater.api.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointment;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointmentAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

public class SurgicalAppointmentDao {
	
	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public SurgicalAppointment save(SurgicalAppointment surgicalAppointment) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(surgicalAppointment);
		session.flush();
		return surgicalAppointment;
	}
	
	public List<SurgicalAppointment> getOverlappingActualTimeEntriesForAppointment(SurgicalAppointment surgicalAppointment) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(SurgicalAppointment.class, "surgicalAppointment");
		criteria.createAlias("surgicalAppointment.surgicalBlock", "surgicalBlock");
		criteria.add(Restrictions.lt("actualStartDatetime", surgicalAppointment.getActualEndDatetime()));
		criteria.add(Restrictions.gt("actualEndDatetime", surgicalAppointment.getActualStartDatetime()));
		criteria.add(Restrictions.eq("surgicalBlock.location", surgicalAppointment.getSurgicalBlock().getLocation()));
		criteria.add(Restrictions.not(Restrictions.in("status", new String[] { "POSTPONED", "CANCELLED" })));
		if (surgicalAppointment.getId() != null) {
			criteria.add(Restrictions.ne("surgicalAppointment.id", surgicalAppointment.getId()));
		}
		criteria.add(Restrictions.eq("voided", false));
		return criteria.list();
	}
	
	public SurgicalAppointment getSurgicalAppointmentByUuid(String uuid) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(SurgicalAppointment.class, "surgicalAppointment");
		criteria.add(Restrictions.eq("voided", false));
		criteria.add(Restrictions.eq("uuid", uuid));
		return (SurgicalAppointment) criteria.uniqueResult();
	}
	
	public SurgicalAppointmentAttribute getSurgicalAppointmentAttributeByUuid(String uuid) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(SurgicalAppointmentAttribute.class, "surgicalAppointmentAttribute");
		criteria.add(Restrictions.eq("voided", false));
		criteria.add(Restrictions.eq("uuid", uuid));
		return (SurgicalAppointmentAttribute) criteria.uniqueResult();
	}
}
