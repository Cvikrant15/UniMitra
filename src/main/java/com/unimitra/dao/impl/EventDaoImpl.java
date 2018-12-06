package com.unimitra.dao.impl;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import com.unimitra.dao.EventDao;
import com.unimitra.entity.EventsEntity;
import com.unimitra.entity.EventsRegisterationEntity;
import com.unimitra.exception.ErrorCodes;
import com.unimitra.exception.UnimitraException;

@Repository
public class EventDaoImpl implements EventDao {

	SessionFactory sessionFactory;
	private boolean flase;

	@Override
	public List<EventsEntity> getEventDetails() throws UnimitraException {
		Timestamp time = new Timestamp(System.currentTimeMillis());
		Session session = sessionFactory.getCurrentSession();
		@SuppressWarnings("unchecked")
		List<EventsEntity> eventList = session
				.createQuery(
						"from EventsEntity e where event_date_time >= '" + time + "'order by e.eventCreationDate desc")
				.list();
		nullCheckForEntity(eventList, ErrorCodes.EVENT_NOT_PRESENT);
		return eventList;
	}

	@Override
	public EventsEntity getEventDetailById(int eventId) throws UnimitraException {
		Session session = sessionFactory.getCurrentSession();
		EventsEntity eventByEventId = session.get(EventsEntity.class, eventId);
		nullCheckForEntity(eventByEventId, ErrorCodes.EVENT_NOT_PRESENT_FOR_EVENTID);
		return eventByEventId;

	}

	@Override
	public String deleteEventById(int eventId) throws UnimitraException {
		Session session = sessionFactory.getCurrentSession();
		EventsEntity deleteEventById = session.get(EventsEntity.class, eventId);
		nullCheckForEntity(deleteEventById, ErrorCodes.EVENT_NOT_PRESENT_FOR_EVENTID);

		deleteEventById.setEventIsActive(flase);
		session.update(deleteEventById);
		return "200";
	}

	@Override
	public EventsEntity postEvent(EventsEntity postEvent) {
		Session session = sessionFactory.getCurrentSession();
		session.save(postEvent);
		return postEvent;
	}

	@Override
	public List<EventsRegisterationEntity> getUserByUserIdEventId(int userId, int eventId) throws UnimitraException {
		Session session = sessionFactory.getCurrentSession();

		@SuppressWarnings("unchecked")
		List<EventsRegisterationEntity> eventRgistrationUser = session
				.createQuery("from EventsRegisterationEntity er where er.userId = '" + userId + "' and er.eventId = '"
						+ eventId + "'")
				.getResultList();
		nullCheckForEntity(eventRgistrationUser, ErrorCodes.MAPPING_NOT_PRESENT_FOR_EVENTID_USERID);

		return eventRgistrationUser;
	}

	public EventsRegisterationEntity updateRegistrationFlag(EventsRegisterationEntity registerForEvent) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(registerForEvent);

		return registerForEvent;

	}

	@Override
	public void updateExsistingRegistrationFlag(EventsRegisterationEntity eventsRegistrationEntity1) {
		Session session = sessionFactory.getCurrentSession();
		session.update(eventsRegistrationEntity1);

	}

	private void nullCheckForEntity(Object entity, String errorCode) throws UnimitraException {
		if (ObjectUtils.isEmpty(entity)) {
			throw new UnimitraException(errorCode);
		}
	}

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}
