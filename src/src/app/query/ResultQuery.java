package app.query;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import app.entity.Result;
import app.entity.Test;

public class ResultQuery {
private SessionFactory ftr;
	
	public ResultQuery(SessionFactory ftr){
		this.ftr = ftr;
	}
	
	public void add(Result Result){
		Session session = ftr.openSession();
		Transaction t = session.beginTransaction();
		try{
			session.save(Result);
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
	public List<Result> getAll(){
		//Session session = ftr.getCurrentSession();
		Session session = ftr.openSession();
		Transaction t = session.beginTransaction();
		List<Result> list = session.createQuery("FROM Result").list();
		t.commit();
		session.close();
		return list;
	}
	public List<Result> getList(String pattern){
		//Session session = ftr.getCurrentSession();
		Session session = ftr.openSession();
		Transaction t = session.beginTransaction();
		String hql = "FROM Result WHERE "+pattern;
		Query query = session.createQuery(hql);
		List<Result> list = query.list();
		t.commit();
		session.close();
		return list;
	}
	public Result get(String pattern){
		//Session session = ftr.getCurrentSession();
		Session session = ftr.openSession();
		Transaction t = session.beginTransaction();
		String hql = "FROM Result WHERE "+pattern;
		Query query = session.createQuery(hql);
		Result rs = (Result)query.uniqueResult();
		t.commit();
		session.close();
		return rs== null? null: rs;
	}

	public void update(Result Result){
		Session session = ftr.openSession();
		Transaction t = session.beginTransaction();
		try{
			session.update(Result);
			t.commit();
		}
		catch(Exception e){
			t.rollback();
		}
		finally{
			session.close();
		}
	}
	public void delete(Result Result){
		Session session = ftr.openSession();
		Transaction t = session.beginTransaction();
		int id = Result.getId();
		try{
			session.delete(session.get(Result.class, id));
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
