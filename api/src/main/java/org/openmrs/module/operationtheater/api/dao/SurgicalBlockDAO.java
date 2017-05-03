package org.openmrs.module.operationtheater.api.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Location;
import org.openmrs.Provider;
import org.openmrs.module.operationtheater.SurgicalBlock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
        session.save(surgicalBlock);
        session.flush();
        return surgicalBlock;
    }

    public List<SurgicalBlock> getOverlappingSurgicalBlocksFor(Date startDatetime, Date endDatetime, Provider provider, Location location) {
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
        return criteria.list();
    }
}