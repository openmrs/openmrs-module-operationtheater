package org.openmrs.module.operationtheater.api.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointmentAttributeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SurgicalAppointmentAttributeTypeDAO {
	
	@Autowired
	SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public List<SurgicalAppointmentAttributeType> getAllAttributeTypes() {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(SurgicalAppointmentAttributeType.class,
		    "surgicalAppointmentAttributeType");
		criteria.add(Restrictions.eq("retired", false));
		criteria.addOrder(Order.asc("sortWeight"));
		return (List<SurgicalAppointmentAttributeType>) criteria.list();
	}
	
	public SurgicalAppointmentAttributeType getSurgicalAppointmentAttributeTypeByUuid(String uuid) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(SurgicalAppointmentAttributeType.class,
		    "surgicalAppointmentAttributeType");
		criteria.add(Restrictions.eq("retired", false));
		criteria.add(Restrictions.eq("uuid", uuid));
		return (SurgicalAppointmentAttributeType) criteria.uniqueResult();
	}
}
