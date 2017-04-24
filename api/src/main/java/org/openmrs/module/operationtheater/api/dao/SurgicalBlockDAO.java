package org.openmrs.module.operationtheater.api.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Location;
import org.openmrs.module.operationtheater.SurgicalBlock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;

@Repository
public class SurgicalBlockDAO {

    @Autowired
    SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    public ArrayList<SurgicalBlock> getSurgicalBlocksBetweenStartDatetimeAndEndDatetimes(Date startDatetime, Date endDatetime, Location location) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(SurgicalBlock.class, "surgicalBlock");
        criteria.add(Restrictions.ge("startDatetime", startDatetime));
        criteria.add(Restrictions.le("startDatetime", endDatetime));
        criteria.add(Restrictions.eq("location", location));
        criteria.add(Restrictions.eq("voided", false));
        return (ArrayList<SurgicalBlock>) criteria.list();

    }

}
