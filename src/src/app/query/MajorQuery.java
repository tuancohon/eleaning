package app.query;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import app.entity.Major;

public class MajorQuery {
private SessionFactory ftr;
	
	public MajorQuery(SessionFactory ftr){
		this.ftr = ftr;
	}
	
	public void add(Major major){
		Session session = ftr.openSession();
		Transaction t = session.beginTransaction();
		try{
			session.save(major);
			t.commit();
		}
		catch(Exception e){
			t.rollback();
			System.out.print(e);
		}
		finally{
			session.close();
		}
	}
	public List<Major> getAll(){
		//Session session = ftr.getCurrentSession();
		Session session = ftr.openSession();
		Transaction t = session.beginTransaction();
		List<Major> list = session.createQuery("FROM Major").list();
		t.commit();
		session.close();
		return list;
	}
	public Major get(String pattern){
		//Session session = ftr.getCurrentSession();
		Session session = ftr.openSession();
		Transaction t = session.beginTransaction();
		String hql = "FROM Major WHERE "+pattern;
		Query query = session.createQuery(hql);
		Major m = (Major)query.uniqueResult();
		t.commit();
		session.close();
		return m== null? null: m;
	}

	public void update(Major major){
		Session session = ftr.openSession();
		Transaction t = session.beginTransaction();
		try{
			session.update(major);
			t.commit();
		}
		catch(Exception e){
			t.rollback();
		}
		finally{
			session.close();
		}
	}
	public void delete(int id){
		Session session = ftr.openSession();
		Transaction t = session.beginTransaction();
		try{
			session.delete(session.get(Major.class, id));
			t.commit();
		}
		catch(Exception e){
			t.rollback();
			System.out.print(e);
		}
		finally{
			session.close();
		}
	}
}
