package app.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import app.entity.Cart;
import app.entity.Course;
import app.entity.Curriculum;
import app.entity.Detail;
import app.entity.Result;
import app.entity.Test;
import app.entity.User;
import app.query.CartQuery;
import app.query.CourseQuery;
import app.query.CurriculumQuery;
import app.query.DetailQuery;
import app.query.MajorQuery;
import app.query.ResultQuery;
import app.query.TestQuery;
import app.query.UserQuery;
import app.render.AES256;
import app.render.Check;

@Transactional(readOnly = true)
@Controller
public class Coursepage {
	@Autowired
	SessionFactory factory;

	@RequestMapping("/coursepage")
	public String coursepage(ModelMap model, HttpServletResponse res, HttpSession ss, HttpServletRequest req)
			throws IOException {
		
		if (req.getParameter("id") == null) {
			return "redirect:./home.htm";
		} else {
			String idstr = (req.getParameter("id") != null) ? req.getParameter("id") : "0";
			if(Check.checkSQLInjectURL(idstr) == false)
			{
				return "redirect:./home.htm";
			}
			int id = Integer.parseInt(idstr);
			int process = 1;
			boolean isPaid = false;		
			CourseQuery ccq = new CourseQuery(factory);			
			Course thiscourse = ccq.get(" id ="+id);
			if(thiscourse == null)
			{
				return "redirect:./home.htm";
			}
			model.addAttribute("allcourse", ccq.getAll());
			model.addAttribute("link", "public");
			model.addAttribute("thiscourse", thiscourse );
			MajorQuery mq = new MajorQuery(factory);
			model.addAttribute("allmajor", mq.getAll());
			Course c = ccq.get(" id = "+id);
			User user = (User) ss.getAttribute("user");
			if (user != null) {
				CartQuery cq = new CartQuery(factory);
				List<Cart> listfc = cq.getlist("paid = true and useridc.id =" + user.getId());
				for(int i=0; i< listfc.size(); i++)
				{
					List<Detail> ldetailtemp = new ArrayList<Detail>();
					for(Detail t: listfc.get(i).getDetail())
					{
						if(!ldetailtemp.contains(t))
						{
							ldetailtemp.add(t);
						}
					}
					listfc.get(i).setDetail(ldetailtemp);
				}
				model.addAttribute("followcourse", listfc);
				for (Cart j : listfc)
					for (Detail k : j.getDetail()) {
						if (k.getCourseidd().getId() == id) {
							process = k.getProcess();
							isPaid = true;
						}
					}
				ccq = new CourseQuery(factory);
				List<Course> listc = ccq.getList("instruid = " + user.getId());
				model.addAttribute("createdcourse", listc);
				model.addAttribute("process", process);	
			} else {
				model.addAttribute("followcourse", "");
				model.addAttribute("isVisit", true);
			}
			CurriculumQuery curriq = new CurriculumQuery(factory);
			List<Curriculum> curriculumms = curriq.getList(" courseid.id = "+thiscourse.getId());
			for(int i=0; i< curriculumms.size();i++)
			{
				String decryptedPdf= AES256.decrypt(curriculumms.get(i).getPdf().trim(), thiscourse.getInstruid().getEmail().trim());
				curriculumms.get(i).setPdf(decryptedPdf);
			}
			model.addAttribute("curriculums",curriculumms);
			TestQuery testq = new TestQuery(factory);
			List<Test> tests = testq.getList("courseidt.id = "+thiscourse.getId());
			model.addAttribute("tests",tests);
			model.addAttribute("isPaid", isPaid);
			
			ResultQuery resultq = new ResultQuery(factory);
			List<Test> listtest = testq.getList("courseidt.id = " + thiscourse.getId());
		//	List<Result> allresult = resultq.getAll();
			if(user != null)
			{
			List<Result> listresulttoview = new ArrayList<>(); 
			for(Test i : listtest)
			{
				//Result rsul = resultq.get("testid.id = " + i.getId() + " and useridr.id = " + user.getId());
				List<Result> rsulchecklist = resultq.getList("testid.id = " + i.getId() + " and useridr.id = " + user.getId());
				Result rsulalone = new Result();				
				if(rsulchecklist.size()>0)
				{
					rsulalone = rsulchecklist.get(0);
					for(int jj=0; jj < rsulchecklist.size() - 1 ;jj++)
					{
						for(int zz = 1 ; zz < rsulchecklist.size();zz++)
						{
							if(rsulchecklist.get(jj).getCorrect() < rsulchecklist.get(zz).getCorrect())
							{
								rsulalone = rsulchecklist.get(zz);
							}
						}
					}
					listresulttoview.add(rsulalone);
				}
			}
			model.addAttribute("listresulttoview", listresulttoview);
			}
			if(user != null)
			{
				model.addAttribute("genderbg", user.getGender());
			}
			else
			{
				model.addAttribute("genderbg", "null");
			}
			return "course/coursepage";
		}
	}
	// ================================================= ABOUT USER// +==============================================
	@RequestMapping("managecourse")
	public String manageControl(ModelMap model, HttpServletResponse res, HttpServletRequest req, HttpSession ss) throws IOException {
		User user = (User)ss.getAttribute("user");
		if(user != null && user.getRole() != 4 )
		{
		CourseQuery ccq = new CourseQuery(factory);	
		UserQuery uq = new UserQuery(factory);
		List<Course> listc = ccq.getList("instruid = " + user.getId());
		
		
		model.addAttribute("list_course", listc);
		model.addAttribute("list_user", uq.getAll());
		model.addAttribute("link", "public");

		return "course/manage";
		}else
		{
			return "redirect:./home.htm";
		}
	}
		
	// ================================================ UPDATE PROCESS Khi User hoc toi dau =========================================
	@RequestMapping("updateprocessofuser")
	public void updateprocessofuser( HttpServletResponse res, HttpSession ss
			,@RequestParam("process") int process
			,@RequestParam("courseid") int id) throws IOException{
		PrintWriter out = res.getWriter();
		User user = (User)ss.getAttribute("user");
		if(user != null){
			CartQuery cq = new CartQuery(factory);
			List<Cart> listfc = cq.getlist("paid = true and useridc.id =" + user.getId());
			for (Cart j : listfc)
				for (Detail k : j.getDetail()) {
					if (k.getCourseidd().getId() == id) {
						// If there was a same course in followcourse
						DetailQuery uq  = new DetailQuery(factory);
						k.setProcess(process);
						uq.update(k);
						out.print(id);
						return;
					}
					
				}
			out.print(0);
			return;
		}
		out.print(0);
		return;
		
	}
}
