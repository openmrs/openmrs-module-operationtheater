package org.openmrs.module.operationtheater.api.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointment;
import org.openmrs.module.operationtheater.api.model.SurgicalBlock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class SurgicalBlockDAO {
	
	@Autowired
	SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public SurgicalBlock save(SurgicalBlock surgicalBlock) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(surgicalBlock);
		session.flush();
		return surgicalBlock;
	}
	
	public List<SurgicalBlock> getOverlappingSurgicalBlocksFor(Date startDatetime, Date endDatetime, Provider provider,
	        Location location, Integer surgicalBlockId) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(SurgicalBlock.class, "surgicalBlock");
		criteria.add(Restrictions.lt("startDatetime", endDatetime));
		criteria.add(Restrictions.gt("endDatetime", startDatetime));
		criteria.add(Restrictions.eq("voided", false));
		if (provider != null) {
			criteria.add(Restrictions.eq("provider", provider));
		}
		if (location != null) {
			criteria.add(Restrictions.eq("location", location));
		}
		if (surgicalBlockId != null) {
			criteria.add(Restrictions.ne("id", surgicalBlockId));
		}
		return criteria.list();
	}
	
	public List<SurgicalBlock> getSurgicalBlocksFor(Date startDatetime, Date endDatetime, Provider provider,
	        Location location, Boolean includeVoided, Boolean activeBlocks) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(SurgicalBlock.class, "surgicalBlock");
		if (activeBlocks) {
			criteria.add(Restrictions.ge("endDatetime", startDatetime));
			criteria.add(Restrictions.le("startDatetime", endDatetime));
		} else {
			criteria.add(Restrictions.ge("startDatetime", startDatetime));
			criteria.add(Restrictions.le("endDatetime", endDatetime));
		}
		if (!includeVoided) {
			criteria.add(Restrictions.eq("voided", false));
		}
		if (provider != null) {
			criteria.add(Restrictions.eq("provider", provider));
		}
		if (location != null) {
			criteria.add(Restrictions.eq("location", location));
		}
		return criteria.list();
	}
	
	public List<SurgicalAppointment> getOverlappingSurgicalAppointmentsForPatient(Date startDatetime, Date endDatetime,
	        Patient patient, Integer surgicalBlockId) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(SurgicalAppointment.class, "surgicalAppointment");
		criteria.createAlias("surgicalAppointment.surgicalBlock", "surgicalBlock");
		criteria.add(Restrictions.lt("surgicalBlock.startDatetime", endDatetime));
		criteria.add(Restrictions.gt("surgicalBlock.endDatetime", startDatetime));
		criteria.add(Restrictions.eq("surgicalBlock.voided", false));
		criteria.add(Restrictions.eq("patient", patient));
		criteria.add(Restrictions.not(Restrictions.in("status", new String[] { "POSTPONED", "CANCELLED" })));
		criteria.add(Restrictions.eq("voided", false));
		if (surgicalBlockId != null) {
			criteria.add(Restrictions.ne("surgicalBlock.id", surgicalBlockId));
		}
		return criteria.list();
	}
	
	public SurgicalBlock getSurgicalBlockWithAppointments(String surgicalBlockUuid) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(SurgicalBlock.class, "surgicalBlock");
		criteria.add(Restrictions.eq("voided", false));
		criteria.add(Restrictions.eq("uuid", surgicalBlockUuid));
		return (SurgicalBlock) criteria.uniqueResult();
	}
}
