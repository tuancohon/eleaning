package app.query;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import app.entity.Cart;
import app.entity.Curriculum;

public class CartQuery {
private SessionFactory ftr;
	
	public CartQuery(SessionFactory ftr){
		this.ftr = ftr;
	}
	
	public void add(Cart Cart){
		Session session = ftr.openSession();
		Transaction t = session.beginTransaction();
		try{
			session.save(Cart);
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
	public List<Cart> getAll(){
		//Session session = ftr.getCurrentSession();
		Session session = ftr.openSession();
		Transaction t = session.beginTransaction();
		List<Cart> list = session.createQuery("FROM Cart").list();
		t.commit();
		session.close();
		return list;
	}
	public Cart get(String pattern){
		//Session session = ftr.getCurrentSession();
		Session session = ftr.openSession();
		Transaction t = session.beginTransaction();
		String hql = "FROM Cart WHERE "+pattern;
		Query query = session.createQuery(hql);
		Cart ca = (Cart)query.uniqueResult();
		t.commit();
		session.close();
		return ca == null? null: ca;
	}
	public List<Cart> getlist(String pattern){
		//Session session = ftr.getCurrentSession();
		Session session = ftr.openSession();
		Transaction t = session.beginTransaction();
		String hql = "FROM Cart WHERE "+pattern;
		Query query = session.createQuery(hql);
		List<Cart> list = query.list();
		t.commit();
		session.close();
		return list;
	}

	public void update(Cart Cart){
		Session session = ftr.openSession();
		Transaction t = session.beginTransaction();
		try{
			session.update(Cart);
			t.commit();
		}
		catch(Exception e){
			t.rollback();
		}
		finally{
			session.close();
		}
	}
	public void delete(Cart Cart){
		Session session = ftr.openSession();
		Transaction t = session.beginTransaction();
		try{
			session.delete(Cart);
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
