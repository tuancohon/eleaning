package app.controller;

import java.io.IOException;
import java.io.PrintWriter;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import app.entity.Cart;
import app.entity.Course;
import app.entity.Detail;
import app.entity.Result;
import app.entity.Test;
import app.entity.User;
import app.query.CartQuery;
import app.query.ResultQuery;
import app.query.TestQuery;
import app.render.AES256;
import app.render.Check;

import java.util.Date;
import java.text.SimpleDateFormat;
@Transactional(readOnly = true)
@Controller
public class Testpage {
	@Autowired
	SessionFactory factory;
	
	@RequestMapping(value="testpage",method=RequestMethod.GET)
	public String test(ModelMap model, HttpSession ss,HttpServletResponse res,HttpServletRequest req) throws IOException{
		String idstr = (req.getParameter("id") != null) ? req.getParameter("id") : "0";
		if(Check.checkSQLInjectURL(idstr) == false || idstr == "0")
		{
			return "redirect:./home.htm";
		}
		int id = Integer.parseInt(idstr);
		User user = (User) ss.getAttribute("user");	
		TestQuery tq = new TestQuery(factory);
		Test thistest = tq.get(" id ="+id);
		if(thistest == null)
			return "redirect:./home.htm";
		if(user == null )
		{
			model.addAttribute("id",thistest.getCourseidt().getId());
			return "redirect:./coursepage.htm";
		}
		boolean check = false;
		find:{
			for(Cart c : user.getCart())
			{
				for(Detail d : c.getDetail())
				{
					if(d.getCourseidd().getId() == thistest.getCourseidt().getId() || thistest.getCourseidt().getInstruid().getId() == user.getId())
					{
						check = true;
						break find;
					}
				}
			}
		}
		if(check == false)
		{
			model.addAttribute("id",thistest.getCourseidt().getId());
			return "redirect:./coursepage.htm";
		}
		SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
		Date n = new Date();
		model.addAttribute("today", fmt.format(n) );
		model.addAttribute("link", "public");
		String decryptedPdf= AES256.decrypt(thistest.getPdf().trim(), thistest.getCourseidt().getInstruid().getEmail().trim());
		String decryptedAnswer= AES256.decrypt(thistest.getAnswer().trim(), thistest.getCourseidt().getInstruid().getEmail().trim());
		thistest.setPdf(decryptedPdf);
		thistest.setAnswer(decryptedAnswer);
		model.addAttribute("thistest", thistest );
		return "course/testpage";
		
		
	}
	@RequestMapping(value="testpage",method=RequestMethod.POST)
	public void checkout(HttpServletResponse res,HttpSession ss,HttpServletRequest req,@RequestParam("id") int id,@RequestParam("rank") String rank,
			@RequestParam("correct") int correct,@RequestParam("date") String date) throws IOException{
		PrintWriter out = res.getWriter();
		TestQuery tq = new TestQuery(factory);
		//int id = (req.getParameter("id") != null) ? Integer.parseInt(req.getParameter("id")) : 0;
		Test test = tq.get("id="+id);
		User user = (User) ss.getAttribute("user");
		//Cart existedCart = cq.get(" paid = false and useridc.id = "+user.getId());
		Result result = new Result();
		ResultQuery resq = new ResultQuery(factory);
		result.setCorrect(correct);
		result.setDay(date);
		result.setRank(rank);
		result.setUseridr(user);
		result.setTestid(test);
		resq.add(result);
		out.print(true);
		//result.setTestid(id);
		
	}
	

}
