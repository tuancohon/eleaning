<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="referrer" content="strict-origin-when-cross-origin">
    <meta http-equiv="Content-Security-Policy" content="script-src 'self';"> 
    <title>Manage Course</title>
    <link rel="icon" type="image/png" href="${link}/img/logo-coral.png">
    <link href="${link}/css/bootstrap.css" rel="stylesheet">
    <link href="${link}/css/font-awesome.css" rel="stylesheet">
    <link href="${link}/css/admin.css" rel="stylesheet">
    <style>
        .border-2 {
            border: 1px solid black !important;
        }
    </style>
</head>

<body>
    <nav class="d-flex navbar border bg-white">
      <a class="logo col-1" href="home.htm">
            <img src="${link}/img/logo-coral.png" alt="Udemy" width="90px">
        </a>        <div class="col-6 d-flex justify-content-end align-items-center">
            Welcome back, <span class="mx-2 text-info font-weight-bold">${sessionScope.user.firstname } ${sessionScope.user.lastname }</span>
        </div>
    </nav>

    <main class="container mt-4 p-4 bg-white " id="table">
        <a class="btn my-3" href="home.htm"><img src="${link}/img/Arrow 1.png" alt="back" width="30px">Back</a>
        <!-- <h2>Order by Date dec</h2> -->
		<c:if test="${sessionScope.user.role != 4 }"> 
        <h2 class="p-2"><span class="font-weight-bold">Courses</span> belong to <span class="text-info">${sessionScope.user.firstname } ${sessionScope.user.lastname }</span></h2>
		</c:if>
        <h4 class="pl-2">Count: <span class="badge badge-pill badge-primary">   	
        	 <c:if test="${list_course == null }">0</c:if>
           	<c:if test="${list_course !=null}">${list_course.size()}</c:if>
        </span>
        <a class="btn bg-success text-white font-weight-bold p-2 mx-2 cour" name="add" href="courseAddform.htm"><i class="fa fa-plus"></i></a>
        </h4>
        <div class="database cart">
            <c:if test="${list_course == null }">Course is empty, Sorry</c:if>
           	<c:if test="${list_course !=null}">    
            <!-- ForEach Cart-->
            <c:forEach items = "${list_course}" var ="c" varStatus="i">
            <div class="form-control-lg d-flex border-2">
                <div class="col-9 tog">
                    Course#${i.index + 1} <a href="coursepage.htm?id=${c.id}" ><span class="text-primary">${c.name}</span></a>
                </div>
                <div class="col-3 d-flex align-items-center justify-content-end">
                    <div class="mx-3 text-success font-weight-bold total">Price:${c.price} </div>		
                    <div class="badge badge-pill badge-success">Major <span class="d-block sm p-1"> ${c.majorid.name} </span></div>
                </div>

           </div>
            </c:forEach>
		</c:if>
        </div>
            <!-- ForEach Detail -->

			

            <!-- ForEach Cart-->
    </main>
    <script src="${link}/js/bootstrap.js"></script>
    <script src="${link}/js/jquery.js"></script>
    <script src="${link}/js/sweetalert.min.js"></script>
	<script>
       /*  $('.tog').on('click', function() {
            $(this).parent().next('.detail').toggleClass('d-none');
        })
        var cash = [...$('.cash')];
        console.log(cash);
        var totals = 0;
        cash.map(each => {
        	totals += parseFloat($(each).text());
        })
       	$('.total').text("$"+totals); */
    </script>
</body>

</html>