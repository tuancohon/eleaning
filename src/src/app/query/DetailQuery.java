package app.query;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import app.entity.Course;
import app.entity.Detail;

public class DetailQuery {
private SessionFactory ftr;
	
	public DetailQuery(SessionFactory ftr){
		this.ftr = ftr;
	}
	
	public void add(Detail Detail){
		Session session = ftr.openSession();
		Transaction t = session.beginTransaction();
		try{
			session.save(Detail);
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
	public List<Detail> getAll(){
		//Session session = ftr.getCurrentSession();
		Session session = ftr.openSession();
		Transaction t = session.beginTransaction();
		List<Detail> list = session.createQuery("FROM Detail").list();
		t.commit();
		session.close();
		return list;
	}
	public Detail get(String pattern){
		//Session session = ftr.getCurrentSession();
		Session session = ftr.openSession();
		Transaction t = session.beginTransaction();
		String hql = "FROM Detail WHERE "+pattern;
		Query query = session.createQuery(hql);
		Detail d = (Detail)query.uniqueResult();
		t.commit();
		session.close();
		return d== null? null: d;	
	}
	
	public List<Detail> getList(String pattern){
		//Session session = ftr.getCurrentSession();
		Session session = ftr.openSession();
		Transaction t = session.beginTransaction();
		String hql = "FROM Detail WHERE "+pattern;
		Query query = session.createQuery(hql);
		List<Detail> list = query.list();
		t.commit();
		session.close();
		return list;
	}
	
	public void update(Detail Detail){
		Session session = ftr.openSession();
		Transaction t = session.beginTransaction();
		try{
			session.update(Detail);
			t.commit();
		}
		catch(Exception e){
			t.rollback();
		}
		finally{
			session.close();
		}
	}
	public void delete(Detail Detail){
		Session session = ftr.openSession();
		Transaction t = session.beginTransaction();
		try{
			session.delete(Detail);
			t.commit();
		}
		catch(Exception e){
			t.rollback();
		}
		finally{
			session.close();
		}
	}
}
