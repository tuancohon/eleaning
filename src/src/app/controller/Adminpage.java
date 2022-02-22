package app.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import app.entity.Cart;
import app.entity.Course;
import app.entity.Curriculum;
import app.entity.Detail;
import app.entity.Major;
import app.entity.Result;
import app.entity.Test;
import app.entity.User;
import app.query.CartQuery;
import app.query.ResultQuery;
import app.query.CourseQuery;
import app.query.CurriculumQuery;
import app.query.DetailQuery;
import app.query.MajorQuery;
import app.query.TestQuery;
import app.query.UserQuery;
import app.render.AES256;
import app.render.Check;
import app.bean.UploadFileCurr;
import app.bean.UploadFileTest;
@Transactional(readOnly = true)
@Controller
public class Adminpage {
	//AES256 aes = new AES256();
	@Autowired
	SessionFactory factory;
	@Autowired
	UploadFileCurr uploadfilecurr;
	@Autowired
	UploadFileTest uploadfiletest;
	@Autowired
	ServletContext context;

	// ================================================= ABOUT USER// +==============================================
	@RequestMapping("user")
	public String userControl(ModelMap model, HttpServletResponse res, HttpServletRequest req, HttpSession ss) throws IOException {
		User user = (User)ss.getAttribute("user");
		if(user != null && user.getRole() == 1 )
		{
		UserQuery uq = new UserQuery(factory);
		model.addAttribute("list_user", uq.getAll());

		model.addAttribute("link", "public");

		return "admin/adminControl_user";
		}else
		{
			return "redirect:./home.htm";
		}
	}

	// ================================================= ABOUT CART BASED// USER+==============================================
	@RequestMapping("cart")
	public String cartControl(ModelMap model, HttpServletResponse res, HttpServletRequest req,HttpSession ss) throws IOException {
		User user = (User)ss.getAttribute("user");
		if(user != null && user.getRole() == 1 )
		{
		String idstr = (req.getParameter("id") != null) ? req.getParameter("id") : "0";
			if(Check.checkSQLInjectURL(idstr) == false)
			{
				return "redirect:./user.htm";
			}
		int id = Integer.parseInt(idstr);
		UserQuery uq = new UserQuery(factory);
		CartQuery cq = new CartQuery(factory);
		if (id == 0 || uq.get("id = " + id) == null) {
			return "redirect:./user.htm";
		}
		
		List<Cart> listcart = cq.getlist("useridc = " + id);
//		List<Detail> abc = cart.get(0).getDetail();
//		List<Detail> abcd = cart.get(1).getDetail();
		for(int i=0; i< listcart.size(); i++)
		{
			List<Detail> ldetailtemp = new ArrayList<Detail>();
			for(Detail t: listcart.get(i).getDetail())
			{
				if(!ldetailtemp.contains(t))
				{
					ldetailtemp.add(t);
				}
			}
			listcart.get(i).setDetail(ldetailtemp);
		}
		model.addAttribute("list_cart", listcart);
		model.addAttribute("user", uq.get("id = " + id));
		model.addAttribute("link", "public");
		return "admin/adminControl_cart";
		}else
		{
			return "redirect:./home.htm";
		}
	}

	// ================================================= ABOUT COURSE// +==============================================
	@RequestMapping("course")
	public String courseControl(ModelMap model, HttpServletResponse res,HttpSession ss) throws IOException {
		User user = (User)ss.getAttribute("user");
		if(user != null && user.getRole() == 1 )
		{
		MajorQuery mq = new MajorQuery(factory);
		CourseQuery cq = new CourseQuery(factory);

		model.addAttribute("list_major", mq.getAll());
		model.addAttribute("list_course", cq.getAll());
		model.addAttribute("link", "public");
		return "admin/adminControl_course";
		}else
		{
			return "redirect:./home.htm";
		}
	}
	// ================================================= A/U/D SPEC// +==============================================

	@RequestMapping(value = "addspec", method = RequestMethod.POST)
	public void addspec(HttpServletResponse res, @RequestParam("value") String value) throws IOException {
		PrintWriter out = res.getWriter();
		MajorQuery mq = new MajorQuery(factory);
		String valuex = Check.cleanXSS(value);
		Major mj = new Major();
		mj.setName(valuex);
		mq.add(mj);
	}

	@RequestMapping(value = "delspec", method = RequestMethod.POST)
	public void delspec(HttpServletResponse res, @RequestParam("id") int id) throws IOException {
		PrintWriter out = res.getWriter();
		MajorQuery mq = new MajorQuery(factory);

		mq.delete(id);

		out.print(true);
	}

	@RequestMapping(value = "updspec", method = RequestMethod.POST)
	public void updspec(HttpServletResponse res, @RequestParam("id") int id, @RequestParam("value") String value)
			throws IOException {
		PrintWriter out = res.getWriter();
		MajorQuery mq = new MajorQuery(factory);
		//Major updmj = (Major) factory.getCurrentSession().get(Major.class, id);
		Major updmj = mq.get(" id = "+id);
		String valuex = Check.cleanXSS(value);
		updmj.setName(valuex);
		mq.update(updmj);
		out.print(true);
		return;
	}
	// ================================================= A/U/D COURSE// +==============================================

	@RequestMapping(value = "courseAddform", method = RequestMethod.GET)
	public String addcourseget(ModelMap model, HttpServletResponse res,HttpSession ss) throws IOException {
		User user = (User)ss.getAttribute("user");
		if(user != null){
		MajorQuery mq = new MajorQuery(factory);
		UserQuery uq = new UserQuery(factory);

		model.addAttribute("list_major", mq.getAll());
		model.addAttribute("list_user", uq.getAll());
		model.addAttribute("link", "public");

		return "admin/courseControl_addform";
		}
		return "redirect:./home.htm";
	}
	@RequestMapping(value = "courseAddform", method = RequestMethod.POST)
	public void addcoursepost(ModelMap model, HttpSession ss, HttpServletResponse res, @RequestParam("name") String name,
			@RequestParam("price") double price, @RequestParam("instructor") int instructor, @RequestParam("cn") int cn,
			@RequestParam("des") String des) throws IOException {
		PrintWriter out = res.getWriter();
		Session session = factory.getCurrentSession();
		User instruct = (User) session.get(User.class, instructor);
		User user = (User)ss.getAttribute("user");
		Major mj = (Major) session.get(Major.class, cn);
		CourseQuery cq = new CourseQuery(factory);
		List<Course> allcourse = cq.getAll();
		for (Course i : allcourse) {
			if (name.trim().equals(i.getName()))
				if (instruct == i.getInstruid()) {
					out.print(false);
					return;
				}
		}
		Course newCourse = new Course();
//		if(Check.checkRegrex(des) || Check.checkRegrex(name))
//		{
//			out.print("xss");
//			return;
//		}
		String desx = Check.cleanXSS(des);
		String namex= Check.cleanXSS(name);
		newCourse.setDes(desx);
		newCourse.setInstruid(instruct);
		newCourse.setMajorid(mj);
		newCourse.setName(namex);
		newCourse.setPrice(price);
		cq.add(newCourse);
		if(user.getRole() == 4){
		UserQuery uq = new UserQuery(factory);
		user.setRole(3);
		uq.update(user);
		}
		out.print(true);
		return;	
	}

	@RequestMapping(value = "courseUpdform", method = RequestMethod.GET)
	public String updcourseget(ModelMap model, HttpServletResponse res, HttpServletRequest req,HttpSession ss) throws IOException {
		//int id = (req.getParameter("id") != null) ? Integer.parseInt(req.getParameter("id")) : 0;
		User user = (User)ss.getAttribute("user");
		String idstr = (req.getParameter("id") != null) ? req.getParameter("id") : "0";
		if(Check.checkSQLInjectURL(idstr) == false)
		{
			return "redirect:./home.htm";
		}
		int id = Integer.parseInt(idstr);
		if (id == 0) {
			return "redirect:./course.htm";
		}
		
		CourseQuery cq = new CourseQuery(factory);
		MajorQuery mq = new MajorQuery(factory);
		UserQuery uq = new UserQuery(factory);
		Course a = cq.get(" id = "+id);
		if(a==null)
		{
			return "redirect:./home.htm";
		}
		if(Check.checkAcess(user) == false || (a.getInstruid().getId() != user.getId() && user.getRole() != 1))
		{
			model.addAttribute("id",id);
			return "redirect:./coursepage.htm";
		}
		model.addAttribute("list_major", mq.getAll());
		model.addAttribute("list_user", uq.getAll());
		model.addAttribute("course",cq.get(" id = "+id));
		model.addAttribute("link", "public");
		return "admin/courseControl_updateform";
	}
	@RequestMapping(value = "courseUpdform", method = RequestMethod.POST)
	public void updcoursepost(ModelMap model, HttpServletResponse res, HttpServletRequest req
			,@RequestParam("id") int id
			,@RequestParam("name") String name
			,@RequestParam("price") double price
			,@RequestParam("instructor") int instructor
			,@RequestParam("cn") int cn
			,@RequestParam("des") String des
			) throws IOException {
//		Session session = factory.getCurrentSession();
		UserQuery  uq = new UserQuery(factory);
		MajorQuery mq = new MajorQuery(factory);
//		User instruct = (User) session.get(User.class, instructor);
		CourseQuery cq = new CourseQuery(factory);
//		Major mj = (Major) session.get(Major.class, cn);
		Course cupdate = cq.get(" id = "+id);
		User instruct = uq.get(" id = "+instructor);
		Major mj = mq.get(" id = "+cn);
		PrintWriter out = res.getWriter();
		
		List<Course> allcourse = cq.getAll();
		for (Course i : allcourse) {
			if (name.trim().equals(i.getName()) && id!=i.getId() )
				if (instruct == i.getInstruid()) {
					if(mj == i.getMajorid()){
					out.print(false);
					return;
					}
				}
		}
		
		String desx = Check.cleanXSS(des);
		String namex= Check.cleanXSS(name);
		cupdate.setName(namex);
		cupdate.setPrice(price);
		cupdate.setDes(desx);
		cupdate.setInstruid(instruct);
		cupdate.setMajorid(mj);
		
		cq.update(cupdate);
		
		out.print(true);
		return;
	}
	@RequestMapping(value = "courseDel",method=RequestMethod.POST)
	public void delcourse(ModelMap model, HttpServletResponse res, HttpServletRequest req,
			@RequestParam("id") int id) throws IOException, InterruptedException {
		PrintWriter out= res.getWriter();
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		CourseQuery cq = new CourseQuery(factory);
		Course thiscourse = cq.get("id = "+id);
		DetailQuery dq = new DetailQuery(factory);
		List<Detail> detail = dq.getList(" courseidd.id = "+ id);
		for (Detail h : detail )
		{
			if(id == h.getCourseidd().getId()) {
				out.print(false);
				return;}}
		CurriculumQuery ccq = new CurriculumQuery(factory);
		List<Curriculum> curris = ccq.getList(" courseid.id = "+id);
		for(Curriculum j : curris)
		{
			ccq.delete(j);
		}		
		TestQuery tq = new TestQuery(factory);
		List<Test> test = tq.getList(" courseidt.id = "+id);
		for(Test j : test)
		{
			tq.delete(j);			
		}	
		session = factory.openSession();
		t = session.beginTransaction();
		try{
			session.delete(session.get(Course.class, id));
			t.commit();}
		catch(Exception e){
			t.rollback();}
		finally{
			session.close();
		}
		out.print(true);
	}
	// ================================================= ABOUT CURRICULUM BASED// COURSE +==============================================
	@RequestMapping("curriculum")
	public String curriculumControl(ModelMap model, HttpServletResponse res, HttpServletRequest req, HttpSession ss)
			throws IOException {
		String idstr = (req.getParameter("id") != null) ? req.getParameter("id") : "0";
		if(Check.checkSQLInjectURL(idstr) == false)
		{
			return "redirect:./home.htm";
		}
		int id = Integer.parseInt(idstr);
		if (id == 0) {
			return "redirect:./course.htm";
		}
		User user = (User)ss.getAttribute("user");
		CourseQuery cq = new CourseQuery(factory);
		Course a =  cq.get("id=" + id);
		if(a==null)
		{
			return "redirect:./home.htm";
		}
		if(Check.checkAcess(user) == false || (a.getInstruid().getId() != user.getId() && user.getRole() != 1))
		{
			model.addAttribute("id",id);
			return "redirect:./coursepage.htm";
		}
		model.addAttribute("course", a);
		model.addAttribute("courseid", id);
		CurriculumQuery ccq = new CurriculumQuery(factory);
		
		List<Curriculum> curris = ccq.getList(" courseid.id = "+id);
		for(int i=0; i< curris.size();i++)
		{
			String decryptedPdf= AES256.decrypt(curris.get(i).getPdf().trim(), user.getEmail().trim());
			curris.get(i).setPdf(decryptedPdf);
		}
		model.addAttribute("curriculums",curris);
		//Session session = factory.getCurrentSession();
		//List<Test> test = session.createQuery("FROM Test WHERE courseidt.id ="+id).list();
		TestQuery tq = new TestQuery(factory);
		List<Test> test = tq.getList(" courseidt.id = "+id);
		for(int i=0; i< test.size();i++)
		{
			String decryptedPdf= AES256.decrypt(test.get(i).getPdf().trim(), user.getEmail().trim());
			String decryptedAnswer= AES256.decrypt(test.get(i).getAnswer().trim(), user.getEmail().trim());
			test.get(i).setPdf(decryptedPdf);
			test.get(i).setAnswer(decryptedAnswer);
		}
		model.addAttribute("test", test);
		model.addAttribute("link", "public");
		return "admin/adminControl_curriculum";
	}

	@RequestMapping(value="curriculumAddform",method=RequestMethod.GET)
	public String addcurriculumget(ModelMap model, HttpServletResponse res, HttpServletRequest req, HttpSession ss
			) throws IOException {
		String idstr = (req.getParameter("id") != null) ? req.getParameter("id") : "0";
		User user = (User)ss.getAttribute("user");
		if(Check.checkSQLInjectURL(idstr) == false)
		{
			return "redirect:./home.htm";
		}
		int id = Integer.parseInt(idstr);
		if (id == 0) {
			return "redirect:./course.htm";
		}
		CourseQuery cq = new CourseQuery(factory);
		Course a = cq.get("id = "+id);
		if(a==null)
		{
			return "redirect:./home.htm";
		}
		if(Check.checkAcess(user) == false || (a.getInstruid().getId() != user.getId() && user.getRole() != 1))
		{
			model.addAttribute("id",id);
			return "redirect:./coursepage.htm";
		}
		
		
		model.addAttribute("courseid", id);
		model.addAttribute("course",cq.get("id = "+id));
		
		model.addAttribute("link", "public");
		return "admin/curriculumControl_addform";
	}
	@RequestMapping(value="curriculumAddform",method=RequestMethod.POST)
	public String addcurriculumpost(ModelMap model, HttpServletResponse res, HttpServletRequest req, HttpSession ss
			,@RequestParam("name") String name
			,@RequestParam("pdf") MultipartFile pdf
			
			) throws IOException, InterruptedException {
		int id = (req.getParameter("id") != null) ? Integer.parseInt(req.getParameter("id")) : 0;
		if (id == 0) {
			return "redirect:./course.htm";
		}
		boolean check_name = true;
		boolean check_pdf = true;
		User user = (User)ss.getAttribute("user");
		CourseQuery cq = new CourseQuery(factory);
		Course courseid = cq.get("id = "+id);
		model.addAttribute("courseid", id);
		model.addAttribute("course",courseid);
		model.addAttribute("link", "public");
		CurriculumQuery ccq = new CurriculumQuery(factory);
		List<Curriculum> allcurr = ccq.getAll();

		
		if(name.equals("")){
			check_name=false;
			model.addAttribute("name_err","Name is Empty");
		}else{
			for(Curriculum i : allcurr){
				if(name.trim().equals(i.getName().trim())){
					model.addAttribute("name_err","Name is Duplicate");
					check_name=false;
					return "admin/curriculumControl_addform";
				}
			}
			
		}
		if(pdf.isEmpty()){
			check_pdf=false;
			model.addAttribute("file_err","PDF file is Empty");
			}
		
		else if(!pdf.getContentType().equals("application/pdf"))
		{
			check_pdf=false;
			model.addAttribute("file_err","File type isn't pdf");}
		else
			{
			String fname = pdf.getOriginalFilename();
			for(Curriculum i : allcurr){
				if(fname.trim().equals(i.getPdf().trim())){
					check_name=false;
					model.addAttribute("file_err","PDF filename is Duplicate");
					return "admin/curriculumControl_addform";
				}}		
			}	
		if(check_name==true)
		{
			if(check_pdf==true){
				String baseDir = uploadfilecurr.basePath;
				String pathreal= baseDir + File.separator + pdf.getOriginalFilename();
				String path = context.getRealPath("/public/pdf_for_curr/" + pdf.getOriginalFilename());
				pdf.transferTo(new File(pathreal));
//				System.out.println(pdf.getOriginalFilename().trim());
				String encryptedPdf = AES256.encrypt(pdf.getOriginalFilename().trim(), courseid.getInstruid().getEmail().trim());
//				System.out.println(encryptedPdf);
				Curriculum newCurri = new Curriculum();
				String namex=Check.cleanXSS(name);
				newCurri.setName(namex);
				newCurri.setPdf(encryptedPdf.trim());
				newCurri.setCourseid(courseid);
//				System.out.println(encryptedPdf.trim());
				ccq.add(newCurri);
				model.addAttribute("success", "Adding successfully");
			}
		}	
		return "admin/curriculumControl_addform";
	}

	@RequestMapping(value = "curriculumUpdform",method=RequestMethod.GET)
	public String updcurriculumget(ModelMap model, HttpServletResponse res, HttpServletRequest req, HttpSession ss) throws IOException, InterruptedException {
		String idstr = (req.getParameter("id") != null) ? req.getParameter("id") : "0";
		String curristr = (req.getParameter("currid") != null) ? req.getParameter("currid") : "0";
		if(Check.checkSQLInjectURL(idstr) == false || Check.checkSQLInjectURL(curristr) == false )
		{
			return "redirect:./home.htm";
		}
		int id = Integer.parseInt(idstr);
		int currid =  Integer.parseInt(curristr);
		
		if (id == 0) {
			return "redirect:./course.htm";
		}else if (currid == 0) {
			return "redirect:./curriculum.htm?id="+id;
		}
		User user = (User)ss.getAttribute("user");
		CourseQuery cq = new CourseQuery(factory);
		Course courseid = cq.get("id = "+id);
		CurriculumQuery ccq = new CurriculumQuery(factory);
		if(courseid==null)
		{
			return "redirect:./home.htm";
		}
		boolean check = false;
		for(Curriculum cu : courseid.getCurriculum())
		{
			if(cu.getId() == currid)
				check = true;
		}
		
		if(Check.checkAcess(user) == false || (courseid.getInstruid().getId() != user.getId() && user.getRole() != 1) || check == false)
		{
			model.addAttribute("id",id);
			return "redirect:./coursepage.htm";
		}
		
		Curriculum curric = new Curriculum();
		curric = ccq.get("id = "+currid);
		String pdf= AES256.decrypt(curric.getPdf().trim(), user.getEmail().trim());
		curric.setPdf(pdf);

		model.addAttribute("curri",curric);
		model.addAttribute("courseid", id);
		model.addAttribute("course",courseid);
		model.addAttribute("link", "public");
		return "admin/curriculumControl_updateform";

	}
	@RequestMapping(value = "curriculumUpdform",method=RequestMethod.POST)
	public String updcurriculumpost(ModelMap model, HttpServletResponse res, HttpServletRequest req, HttpSession ss
			,@RequestParam("name") String name
			,@RequestParam("pdf") MultipartFile pdf
			) throws IOException, InterruptedException {
		int id = (req.getParameter("id") != null) ? Integer.parseInt(req.getParameter("id")) : 0;
		int currid = (req.getParameter("currid") != null) ? Integer.parseInt(req.getParameter("currid")) : 0;
		if (id == 0) {
			return "redirect:./course.htm";
		}else if (currid == 0) {
			return "redirect:./curriculum.htm?id="+id;
		}		
		User user = (User)ss.getAttribute("user");
//		Handle
		boolean check_name= true;
		boolean check_pdf = false;		
		CourseQuery cq = new CourseQuery(factory);
		Course courseid = cq.get("id = "+id);		
		CurriculumQuery ccq = new CurriculumQuery(factory);
		List<Curriculum> allcurr = ccq.getAll();		
//		If name isn't empty and duplicate
		if(name.equals("")){
			check_name=false;
			model.addAttribute("name_err","Name is Empty");
		}else{
			for(Curriculum i : allcurr){
				if(name.trim().equals(i.getName().trim()) && currid != i.getId()){
					model.addAttribute("name_err","Name is Duplicate or not changed");
					check_name=false;
					break;
				}
			}				
		}		
//		If pdf_name isn't empty and duplicate
		if(!pdf.isEmpty())
		if(!pdf.getContentType().equals("application/pdf"))
		{
			check_pdf=false;
			model.addAttribute("file_err","File type isn't pdf");
		}
			else
			{
			String fname = pdf.getOriginalFilename();
			for(Curriculum i : allcurr){
				if(fname.trim().equals(i.getPdf().trim())){
					model.addAttribute("file_err","PDF filename is Duplicate");
					check_pdf=false;
					break;
					}
				else{
					check_pdf=true;
				}
				}
			
			}	
//		If true then update both
		Curriculum updCurri = ccq.get("id = "+currid);
		String in = courseid.getInstruid().getEmail();
		if(check_name==true)
		{
			if(check_pdf==true){
				String baseDir = uploadfilecurr.basePath;
				String pathreal= baseDir + File.separator + pdf.getOriginalFilename();
				//String path = context.getRealPath("/public/pdf_for_curr/" + pdf.getOriginalFilename());
				pdf.transferTo(new File(pathreal));
				String encryptedPdf = AES256.encrypt(pdf.getOriginalFilename().trim(), courseid.getInstruid().getEmail().trim());
				updCurri.setPdf(encryptedPdf);
			}	
			String namex=Check.cleanXSS(name);
			updCurri.setName(namex);
			ccq.update(updCurri);
			model.addAttribute("success", "Updating successfully");
		}
			
//		Model
		String decryptedPdf= AES256.decrypt(updCurri.getPdf().trim(), courseid.getInstruid().getEmail().trim());
		updCurri.setPdf(decryptedPdf);
		model.addAttribute("curri",updCurri);
		model.addAttribute("courseid", id);
		model.addAttribute("course",courseid);
		model.addAttribute("link", "public");
		if(check_pdf == true)
		TimeUnit.SECONDS.sleep(3);
		return "admin/curriculumControl_updateform";

	}
	@RequestMapping(value = "curriculumDel",method=RequestMethod.POST)
	public void delcurriculum(ModelMap model, HttpServletResponse res, HttpServletRequest req,
			@RequestParam("id") int id) throws IOException, InterruptedException {
		PrintWriter out= res.getWriter();
		
		CurriculumQuery cq = new CurriculumQuery(factory);
		
		Curriculum chosen = cq.get("id = "+id);
		
		cq.delete(chosen);
		out.print(true);
		
	}
	// ================================================= ABOUT TEST BASED COURS // +==============================================
	@RequestMapping(value="testAddform",method=RequestMethod.GET)
	public String addtestget(ModelMap model, HttpServletResponse res, HttpServletRequest req, HttpSession ss) throws IOException {
		String idstr = (req.getParameter("id") != null) ? req.getParameter("id") : "0";
		if(Check.checkSQLInjectURL(idstr) == false)
		{
			return "redirect:./home.htm";
		}
		int id = Integer.parseInt(idstr);
		User user = (User)ss.getAttribute("user");
		if (id == 0) {
			return "redirect:./course.htm";
		}
		
		CourseQuery cq = new CourseQuery(factory);
		Course courseid = cq.get("id = "+id);
		if(courseid==null)
		{
			return "redirect:./home.htm";
		}
		if(Check.checkAcess(user) == false || (courseid.getInstruid().getId() != user.getId() && user.getRole() != 1))
		{
			model.addAttribute("id",id);
			return "redirect:./coursepage.htm";
		}
		model.addAttribute("course", courseid);
		model.addAttribute("courseid", id);
		model.addAttribute("link", "public");
		return "admin/testControl_addform";
	}
	@RequestMapping(value="testAddform",method=RequestMethod.POST)
	public String addtestpost(ModelMap model, HttpServletResponse res, HttpServletRequest req, HttpSession ss
			,@RequestParam("name") String name
			,@RequestParam("time") double time
			,@RequestParam("pdf")  MultipartFile pdf
			,@RequestParam("ques") int ques
			,@RequestParam("answer") String answer
			
			) throws IOException {
		int id = (req.getParameter("id") != null) ? Integer.parseInt(req.getParameter("id")) : 0;
		if (id == 0) {
			return "redirect:./course.htm";
		}
		User user = (User)ss.getAttribute("user");
		//handle
		answer = answer.toUpperCase().trim();
		boolean check_name = true;
		boolean check_ques = true;
		boolean check_answer = true;
		boolean check_pdf = true;
		
		CourseQuery cq = new CourseQuery(factory);
		Course courseid = cq.get("id = "+id);
		model.addAttribute("course", courseid);
		model.addAttribute("courseid", id);
		model.addAttribute("link", "public");
		TestQuery tq = new TestQuery(factory);
		List<Test> alltest = tq.getAll();

		
		if(name.equals("")){
			check_name=false;
			model.addAttribute("name_err","Name is Empty");
		}else{
			for(Test i : alltest){
				if(name.trim().equals(i.getName().trim())){
					model.addAttribute("name_err","Name is Duplicate");
					check_name=false;
					break;
				}
			}
			
		}
		if(answer.equals("")){
			check_answer=false;
			model.addAttribute("answer_err","Answer is Empty");
		}else{
			if(!answer.matches("[ABCD]+")){
				check_answer=false;
				model.addAttribute("answer_err","Answer just have 4 choice A, B, C, D");
			}else if(answer.length() < ques){
				check_answer=false;
				model.addAttribute("answer_err","Don't have enough answer for all questions");
			}else if(answer.length() > ques){
				check_answer=false;
				model.addAttribute("answer_err","Too many answers for all questions");
			}
		}
		
		if(pdf.isEmpty()){
			check_pdf=false;
			model.addAttribute("file_err","PDF file is Empty");
			}
		else if(!pdf.getContentType().equals("application/pdf"))
		{
			check_pdf=false;
			model.addAttribute("file_err","File type isn't pdf");
		}
			else
			{
			String fname = pdf.getOriginalFilename();
			for(Test i : alltest){
				if(fname.trim().equals(i.getPdf().trim())){
					check_name=false;
					model.addAttribute("file_err","PDF filename is Duplicate");
					break;
				}
				}
			}
		
		if(check_name==true)
		{
			if(check_answer==true)
				if(check_pdf==true){
					String baseDir = uploadfiletest.basePath;
					String pathreal= baseDir + File.separator + pdf.getOriginalFilename();
					//String path = context.getRealPath("/public/pdf_for_test/" + pdf.getOriginalFilename());
					pdf.transferTo(new File(pathreal));
					String encryptedPdf = AES256.encrypt(pdf.getOriginalFilename().trim(), courseid.getInstruid().getEmail().trim());
					String encryptedAnswer = AES256.encrypt(answer.trim(), courseid.getInstruid().getEmail().trim());
					Test newTest = new Test();
					String namex = Check.cleanXSS(name);
					newTest.setAnswer(encryptedAnswer.trim());
					newTest.setName(namex);
					newTest.setPdf(encryptedPdf.trim());
					newTest.setQuestion(ques);
					newTest.setTime((float)time);
					newTest.setCourseidt(courseid);
					tq.add(newTest);
					model.addAttribute("success", "Adding successfully");
				}
		}
		
		//model
		
		return "admin/testControl_addform";
	}


	@RequestMapping(value="testUpdform",method=RequestMethod.GET)
	public String updtestget(ModelMap model, HttpServletResponse res, HttpServletRequest req, HttpSession ss) throws IOException {
		String idstr = (req.getParameter("id") != null) ? req.getParameter("id") : "0";
		String teststr = (req.getParameter("testid") != null) ? req.getParameter("testid") : "0";
		if(Check.checkSQLInjectURL(idstr) == false || Check.checkSQLInjectURL(teststr) == false)
		{
			return "redirect:./home.htm";
		}
		int id = Integer.parseInt(idstr);
		int testid =  Integer.parseInt(teststr);
		if (id == 0) {
			return "redirect:./course.htm";
		}else if (testid == 0) {
			return "redirect:./curriculum.htm?id="+id;
		}
		User user = (User)ss.getAttribute("user");
		CourseQuery cq = new CourseQuery(factory);
		Course courseid = cq.get("id = "+id);
		if(courseid==null)
		{
			return "redirect:./home.htm";
		}
		boolean check = false;
		for(Test test : courseid.getTest())
		{
			if(test.getId() == testid)
				check = true;
		}
		if(Check.checkAcess(user) == false || (courseid.getInstruid().getId() != user.getId() && user.getRole() != 1) || check == false)
		{
			model.addAttribute("id",id);
			return "redirect:./coursepage.htm";
		}
		TestQuery tq = new TestQuery(factory);
		Test te = new Test();
		te = tq.get("id="+testid);
		String pdf= AES256.decrypt(te.getPdf().trim(), user.getEmail().trim());
		String answ =  AES256.decrypt(te.getAnswer().trim(), user.getEmail().trim());
		te.setPdf(pdf);
		te.setAnswer(answ);
		model.addAttribute("course", courseid);
		model.addAttribute("test", te);
		model.addAttribute("courseid", id);
		model.addAttribute("link", "public");
		return "admin/testControl_updateform";
	}
	@RequestMapping(value="testUpdform",method=RequestMethod.POST)
	public String updtestpost(ModelMap model, HttpServletResponse res, HttpServletRequest req, HttpSession ss			
			,@RequestParam("name") String name
			,@RequestParam("time") double time
			,@RequestParam("pdf")  MultipartFile pdf
			,@RequestParam("ques") int ques
			,@RequestParam("answer") String answer
			
			) throws IOException, InterruptedException {
		int id = (req.getParameter("id") != null) ? Integer.parseInt(req.getParameter("id")) : 0;
		int testid = (req.getParameter("testid") != null) ? Integer.parseInt(req.getParameter("testid")) : 0;
		if (id == 0) {
			return "redirect:./course.htm";
		}else if (testid == 0) {
			return "redirect:./curriculum.htm?id="+id;
		}
		User user = (User)ss.getAttribute("user");
		//handle
		answer = answer.toUpperCase().trim();
		boolean check_name = true;
		boolean check_answer = true;
		boolean check_pdf = false;
		
		CourseQuery cq = new CourseQuery(factory);
		Course courseid = cq.get("id = "+id);
		
		TestQuery tq = new TestQuery(factory);
		model.addAttribute("test", tq.get("id="+testid));
		
		model.addAttribute("course", courseid);
		model.addAttribute("courseid", id);
		model.addAttribute("link", "public");
		List<Test> alltest = tq.getAll();

		if(name.equals("")){
			check_name=false;
			model.addAttribute("name_err","Name is Empty");
		}else{
			for(Test i : alltest){
				if(name.trim().equals(i.getName().trim()) && testid != i.getId()){
					model.addAttribute("name_err","Name is Duplicate");
					check_name=false;
					break;
				}
			}
			
		}
		
		if(answer.equals("")){
			check_answer=false;
			model.addAttribute("answer_err","Answer is Empty");
		}else{
			if(!answer.trim().matches("[ABCD]+")){
				check_answer=false;
				model.addAttribute("answer_err","Answer just have 4 choice A, B, C, D");
			}else if(answer.trim().length() < ques){
				check_answer=false;
				model.addAttribute("answer_err","Don't have enough answer for all questions");
			}else if(answer.trim().length() > ques){
				check_answer=false;
				model.addAttribute("answer_err","Too many answers for all questions");
			}
		}
		
		 if(!pdf.isEmpty())
		 if(!pdf.getContentType().equals("application/pdf"))
			{
				check_pdf=false;
				model.addAttribute("file_err","File type isn't pdf");
			}
			else
			{
			String fname = pdf.getOriginalFilename();
			for(Test i : alltest){
				if(fname.trim().equals(i.getPdf().trim())){
					check_name=false;
					model.addAttribute("file_err","PDF filename is Duplicate");
					break;
					}else{
						check_pdf=true;
					}
				}
			}
		 
		Test updtest = tq.get("id="+testid);
		if(check_name==true)
		{
			if(check_pdf==true){
				String baseDir = uploadfiletest.basePath;
				String pathreal= baseDir + File.separator + pdf.getOriginalFilename();
				//String path = context.getRealPath("/public/pdf_for_test/" + pdf.getOriginalFilename());
				pdf.transferTo(new File(pathreal));
				String encryptedPdf = AES256.encrypt(pdf.getOriginalFilename().trim(), courseid.getInstruid().getEmail().trim());
				updtest.setPdf(encryptedPdf.trim());
			}
			if(check_answer ==true)
			{
				String encryptedAnswer = AES256.encrypt(answer.trim(), courseid.getInstruid().getEmail().trim());
				updtest.setAnswer(encryptedAnswer.trim());
			}
			String namex = Check.cleanXSS(name);
			updtest.setName(namex);
			updtest.setTime((float)time);
			updtest.setQuestion(ques);
			tq.update(updtest);
			model.addAttribute("success", "Updating successfully");
		}
		String decryptedPdf= AES256.decrypt(updtest.getPdf().trim(),courseid.getInstruid().getEmail().trim());
		String decryptedAnswer= AES256.decrypt(updtest.getAnswer().trim(), courseid.getInstruid().getEmail().trim());
		updtest.setPdf(decryptedPdf);
		updtest.setAnswer(decryptedAnswer);
		model.addAttribute("test", updtest);
		//model
		if(check_pdf == true)
			TimeUnit.SECONDS.sleep(3);
		return "admin/testControl_updateform";
		}
	@RequestMapping(value = "testDel",method=RequestMethod.POST)
	public void deltest(ModelMap model, HttpServletResponse res, HttpServletRequest req,
			@RequestParam("id") int id) throws IOException, InterruptedException {
		PrintWriter out= res.getWriter();
		TestQuery tq = new TestQuery(factory);
		
		Test chosen = tq.get("id = "+id);
		
		tq.delete(chosen);
		out.print(true);
		
	}
}
