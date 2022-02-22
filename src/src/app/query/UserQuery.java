package app.query;
import org.hibernate.*;
import app.entity.*;
import java.util.*;


public class UserQuery {
	
	private SessionFactory ftr;
	
	public UserQuery(SessionFactory ftr){
		this.ftr = ftr;
	}
	
	public void add(User user){
		Session session = ftr.openSession();
		Transaction t = session.beginTransaction();
		try{
			session.save(user);
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
	public void insertUserSql(User a){
		  Session session = ftr.openSession(); 
		  Query query =  session.createSQLQuery( 
				  "EXEC insert_user :Firstname , :Lastname , :Email , :Password , :Birthday , :Role , :Gender , :income  ").addEntity(User.class);
		           query.setParameter("Firstname", a.getFirstname()); //User a= query.list();
		            query.setParameter("Lastname", a.getLastname());
		            query.setParameter("Email", a.getEmail());
		            query.setParameter("Password", a.getPassword());
		            query.setParameter("Birthday", a.getBirthday());
		            query.setParameter("Role", a.getRole());
		            query.setParameter("Gender", a.getGender());
		            query.setParameter("income", a.getIncome());
		            System.out.println(a.getIncome());
				  query.executeUpdate();
				  session.close();
				  
	}
	public List<User> getAll(){
		//Session session = ftr.getCurrentSession();
		Session session = ftr.openSession();
		Transaction t = session.beginTransaction();
		Query query = session.createQuery("FROM User");
		List<User> list = query.list();
		t.commit();
		session.close();
		return list;
	}
	public User get(String pattern){
		//Session session = ftr.getCurrentSession();
		Session session = ftr.openSession();
		Transaction t = session.beginTransaction();
		String hql = "FROM User WHERE "+pattern;
		Query query = session.createQuery(hql);
		User u = (User)query.uniqueResult();
		t.commit();
		session.close();
		return u== null? null : u;
	}
	public User getsql(String pattern){
		  Session session = ftr.openSession(); 
		  try {
		  Query query = session.createSQLQuery( //"EXEC pemail :email , :last ")
				  "EXEC SP_SEL_user :email") .addEntity(User.class);
				  query.setParameter("email", pattern); //User a= query.list();
				  List<User> allUsers = query.list(); 
				  User a= allUsers.get(0);
				  session.close();
				  return a;
		  }catch (Exception e) {
			return null;
		}
	}
	public void update(User user){
		Session session = ftr.openSession();
		Transaction t = session.beginTransaction();
		try{
			session.update(user);
			t.commit();
		}
		catch(Exception e){
			t.rollback();
		}
		finally{
			session.close();
		}
	}
	public void updateUserSql(User a){
		  Session session = ftr.openSession(); 
		  Query query =  session.createSQLQuery( 
				  "EXEC update_USERS_INCOME :id , :tien ").addEntity(User.class);
		           query.setParameter("id", a.getId()); //User a= query.list();
		            query.setParameter("tien", a.getIncome());
				  query.executeUpdate();
				  session.close();
				  
	}
	public void updateUserInfo(User a){
		  Session session = ftr.openSession(); 
		  Query query =  session.createSQLQuery( 
				  "EXEC update_USERS :id , :fn , :ln , :bd , :gen ").addEntity(User.class);
		           query.setParameter("id", a.getId()); //User a= query.list();
		            query.setParameter("fn", a.getFirstname());
		            query.setParameter("ln", a.getLastname());
		            query.setParameter("bd", a.getBirthday());
		            query.setParameter("gen", a.getGender());
				  query.executeUpdate();
				  session.close();
				  
	}
}
