package app.query;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import app.entity.Course;
import app.entity.Result;
import app.entity.Test;

public class TestQuery {
private SessionFactory ftr;
	
	public TestQuery(SessionFactory ftr){
		this.ftr = ftr;
	}
	
	public void add(Test Test){
		Session session = ftr.openSession();
		Transaction t = session.beginTransaction();
		try{
			session.save(Test);
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
	public List<Test> getList(String pattern){
		//Session session = ftr.getCurrentSession();
		Session session = ftr.openSession();
		Transaction t = session.beginTransaction();
		String hql = "FROM Test WHERE "+pattern;
		Query query = session.createQuery(hql);
		List<Test> list = query.list();
		t.commit();
		session.close();
		return list;
	}
	public List<Test> getAll(){
		//Session session = ftr.getCurrentSession();
		Session session = ftr.openSession();
		Transaction t = session.beginTransaction();
		List<Test> list = session.createQuery("FROM Test").list();
		t.commit();
		session.close();
		return list;
	}
	public Test get(String pattern){
		//Session session = ftr.getCurrentSession();
		Session session = ftr.openSession();
		Transaction t = session.beginTransaction();
		String hql = "FROM Test WHERE "+pattern;
		Query query = session.createQuery(hql);
		Test te = (Test)query.uniqueResult();
		t.commit();
		session.close();
		return te== null? null: te;
	}

	public void update(Test Test){
		Session session = ftr.openSession();
		Transaction t = session.beginTransaction();
		try{
			session.update(Test);
			t.commit();
		}
		catch(Exception e){
			t.rollback();
		}
		finally{
			session.close();
		}
	}
	public void delete(Test Test){
		Session session = ftr.openSession();
		Transaction t = session.beginTransaction();
		int id = Test.getId();
		ResultQuery resq = new ResultQuery(ftr);
		List<Result> listres = resq.getList(" testid.id = "+ id);
		for(Result i : listres)
		{
			resq.delete(i);
		}
		try{
			session.delete(session.get(Test.class, id));
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
