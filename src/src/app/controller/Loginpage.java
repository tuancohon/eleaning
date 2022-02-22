package app.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
//import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import app.bean.VerifyRecaptcha;
import app.entity.Detail;
import app.entity.User;
import app.query.UserQuery;
import app.render.AES256;
import app.render.Check;

@Transactional(readOnly = true)
@Controller
public class Loginpage {
	public String code;
	@Autowired
	SessionFactory factory;
	@Autowired
	JavaMailSender mailer;
	Check check = new Check();
	// LOGIN
	// Session for user
	// Session for carttribute(
	// Check if email is existed ? return : redirect
	@RequestMapping(value = "/login")
	public String loginGET(ModelMap model, HttpServletResponse res, HttpSession ss) throws IOException {
		res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		res.setHeader("Pragma", "no-cache"); // HTTP 1.0.
		res.setHeader("Expires", "0"); // Proxies.
		/*Session session = factory.openSession();
		Query query = session.createSQLQuery(
				"EXEC pemail :email , :last ")
				.addEntity(User.class);
		query.setParameter("email", "trinhtrung2k@cc.com");
		query.setParameter("last", "Trinhh");
		//User a= query.list();
		List<User> allUsers = query.list();
		User a= allUsers.get(0);
		System.out.println(a.getId());
		System.out.println(a.getFirstname());
		System.out.println(a.getEmail());*/
//		AES256 aes = new AES256();
//		String secretKey = "N18DCAT068";
//		String originalString = "Nguyen Nhat Tan";		 
//	    String encryptedString = AES256.encrypt(originalString, secretKey);
//	    String decryptedString = AES256.decrypt(encryptedString, secretKey);
//	    System.out.println(originalString);
//	    System.out.println(encryptedString);
//	    System.out.println(decryptedString);
//		session.beginTransaction();
//		Query query = session.createSQLQuery("EXEC list")
		if(ss.getAttribute("user") == null){
		model.addAttribute("link", "public");
		return "user/login";
		}else
		{
//			model.addAttribute("link", "public");
			return "redirect:./home.htm";
		}
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public void loginPOST(ModelMap model, HttpServletRequest req, HttpServletResponse res, HttpSession ss)
			throws IOException {
		res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		res.setHeader("Pragma", "no-cache"); // HTTP 1.0.
		res.setHeader("Expires", "0"); 
		String email = req.getParameter("email").trim();
		String password = req.getParameter("password").trim();
		
		String enPass = check.getMd5(password);
//		System.out.println(enPass);
//		System.out.println(password);
		//Session session = factory.getCurrentSession();
		PrintWriter out = res.getWriter();
		
//		String hql = "FROM User u WHERE u.email = '" + email + "'";
//		Query query = session.createQuery(hql);
		//User check = (User) query.uniqueResult();
		UserQuery uq = new UserQuery(factory);
		User check=uq.getsql(email);
		String gRecaptchaResponse = req.getParameter("grecaptcharesponse");
		System.out.println(gRecaptchaResponse);
		boolean verify = VerifyRecaptcha.verify(gRecaptchaResponse);
		if(!verify)
		{
			out.print("captcha");
		}
		if (check != null && verify) {
			if (check.getPassword().trim().equals(enPass)) {
				out.print(check.getLastname().trim());
				ss.setAttribute("user", check);
				ss.setAttribute("cart", new ArrayList<Detail>());
				System.out.println("Put in the session >> user [ "+email+" ]");
				return;
			} else {
				out.print("false");
				return;
			}
		} else {
			out.print("");
			return;
		}
	}
	@RequestMapping(value ="/logout")
	public String logout(ModelMap model, HttpServletResponse res, HttpSession ss) throws IOException {
		ss.removeAttribute("user");
		ss.removeAttribute("cart");
		model.addAttribute("link", "public");
		return "user/login";
	}
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public void registerPOST(ModelMap model, HttpServletRequest req, HttpServletResponse res,
			@RequestParam("firstname") String firstname, @RequestParam("lastname") String lastname,
			@RequestParam("birthday") String birthday, @RequestParam("gender") boolean gender,
			@RequestParam("email") String email, @RequestParam("password") String password) throws IOException {
		res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		res.setHeader("Pragma", "no-cache"); // HTTP 1.0.
		res.setHeader("Expires", "0"); 
		String gRecaptchaResponse = req.getParameter("grecaptcharesponse");
		System.out.println(gRecaptchaResponse);
		boolean verify = VerifyRecaptcha.verify(gRecaptchaResponse);
		UserQuery uq = new UserQuery(factory);
		PrintWriter out = res.getWriter();
		if(!verify)
		{
			out.print("captcha");
			return;
		}
		List<User> list = uq.getAll();
		for (User i : list) {
			if (i.getEmail().trim().equals(email.trim())) {
				out.print("");
				return;
			}
		}
		User n_u = new User();
		n_u.setFirstname(firstname.trim());
		n_u.setLastname(lastname.trim());
		n_u.setEmail(email.trim());
		n_u.setGender(gender);
		n_u.setPassword(check.getMd5(password.trim()));
		n_u.setBirthday(birthday.trim());
		n_u.setIncome(null);
		UserQuery inUser = new UserQuery(factory);
		inUser.insertUserSql(n_u);
		User a=inUser.getsql(email.trim());
		a.setIncome("0"); 
		inUser.updateUserSql(a);
		//uq.add(n_u);
		out.print("true");
		return;
	}

	// FORGOT PASSWORD
	// 0 . Show Client form
	@RequestMapping(value = "/changepw", method = RequestMethod.GET)
	public String changepwGET(ModelMap model, @ModelAttribute User user, HttpServletRequest req,
			HttpServletResponse res) throws IOException {
		res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		res.setHeader("Pragma", "no-cache"); // HTTP 1.0.
		res.setHeader("Expires", "0"); 
		model.addAttribute("mode", 3);
		return "user/login";
	}
	// 1 . Sending Email to Client due to his/her Email && Confirm the code
	// inside that mail

	@RequestMapping(value = "/changepw", method = RequestMethod.POST)
	public void changepwPOST(ModelMap model, @ModelAttribute User user, HttpServletRequest req,
			HttpServletResponse res, @RequestParam("email") String email) throws IOException {
		res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		res.setHeader("Pragma", "no-cache"); // HTTP 1.0.
		res.setHeader("Expires", "0"); 
		model.addAttribute("mode", 3);
		PrintWriter out = res.getWriter();
		// Nếu Email có tồn tại trong database
		String gRecaptchaResponse = req.getParameter("grecaptcharesponse");
		System.out.println(gRecaptchaResponse);
		boolean verify = VerifyRecaptcha.verify(gRecaptchaResponse);
		if(!verify)
		{
			out.print("captcha");
			return;
		}
		Session session = factory.getCurrentSession();
		String hql = "FROM User u WHERE u.email = :data";
		Query query = session.createQuery(hql);
		query.setParameter("data", email.trim());
		User onlyEmail = (User) query.uniqueResult();
		System.out.println("data");
		
		if (onlyEmail != null) {
			int c = 0;
			for (int i = 0; i < 6; i++) {
				c += (int) (Math.random() * (9 - 0 + 1) + 0);
				c *= 10;
			}
			  code = c + "";
			String from = "Eleaning";
			String to = email.trim();
			String subject = "From Elearning";
			String body = "Hi "+email+", type this is code " + code +" into input and reset password";
			try {
				// Tạo Mail
				MimeMessage mail = mailer.createMimeMessage();
				// Sử dụng lớp Helper
				MimeMessageHelper helper = new MimeMessageHelper(mail);
				helper.setFrom(from, from);
				helper.setTo(to);
				helper.setReplyTo(from, from);
				helper.setSubject(subject);
				helper.setText(body, true);

				// Gửi mail
				mailer.send(mail);
				out.print(code+","+onlyEmail.getId());
				return;
			} catch (Exception ex) {
				System.out.print(ex);
				out.print("");
				return;
			}
		} else {
			out.print("false");
			return;
		}
		
	}

	// 2. Get new Password and one again type new rePassword to confirm Client
	// has changed his/her password
	@RequestMapping(value = "/confirmpw", method = RequestMethod.POST)
	public void confirmpwGET(ModelMap model, @ModelAttribute User user, HttpServletRequest req,
			HttpServletResponse res, @RequestParam("pass") String pass,
			@RequestParam("id") int id, @RequestParam("code") String vcode) throws IOException {
		res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		res.setHeader("Pragma", "no-cache"); // HTTP 1.0.
		res.setHeader("Expires", "0"); 
			UserQuery uq = new UserQuery(factory);
			PrintWriter out = res.getWriter();
			Session session = factory.getCurrentSession();
			String enPas=check.getMd5(pass);
			User u = (User) session.get(User.class, id);
			
			if(vcode.trim().equals(code.trim())) {
			if(!enPas.equals(check.getMd5(u.getPassword().trim()))){
//			u.setPassword(enPas);
//			uq.update(u);
				Session session1 = factory.openSession();
				Query query = session1.createSQLQuery(
						"EXEC UPDATE_PASS :email , :newpass ")
						.addEntity(User.class);
				query.setParameter("email", u.getEmail());
				query.setParameter("newpass", pass);
				query.executeUpdate();
			}
			}
			else {
				out.print("false");
				return;
			}

	}
//	 public String getMd5(String input)
//	    {
//	        try {
//	  
//	            // Static getInstance method is called with hashing MD5
//	            MessageDigest md = MessageDigest.getInstance("MD5");
//	  
//	            // digest() method is called to calculate message digest
//	            //  of an input digest() return array of byte
//	            byte[] messageDigest = md.digest(input.getBytes());
//	  
//	            // Convert byte array into signum representation
//	            BigInteger no = new BigInteger(1, messageDigest);
//	  
//	            // Convert message digest into hex value
//	            String hashtext = no.toString(16);
//	            while (hashtext.length() < 32) {
//	                hashtext = "0" + hashtext;
//	            }
//	            return hashtext;
//	        } 
//	  
//	        // For specifying wrong message digest algorithms
//	        catch (NoSuchAlgorithmException e) {
//	            throw new RuntimeException(e);
//	        }
//	    }
	// REGISTER
	// Check REGEX and Null
	// Insert Table User *
	// FORGOT PASSWORD
	// Sending mail
	// Confirm code
	// Change Password ( at least 8 characters )

}
