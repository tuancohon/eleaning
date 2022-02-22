<%@ page pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<!DOCTYPE html>
<html lang="en">

<head>
    <title>Udemy - test</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="referrer" content="strict-origin-when-cross-origin">
    <meta http-equiv="Content-Security-Policy" content="script-src 'self';"> 
    <link rel="icon" type="image/png" href="${link}/img/logo-coral.png">
    <link rel="stylesheet" href="${link}/css/bootstrap.css">
    <link href="${link}/css/udemytest.css" rel="stylesheet">
 

</head>

<body>
    <!-- Bắt đầu nav -->
    <!-- <div class="container-fluid" style="background-color: rgb(26, 26, 26);height: 10px;"></div> -->
    <nav class=" container-fluid">
        <div class="row navbar-expand-lg ">
            <div class="col-md-6 col-6 ml-4 ml-lg-5 mt-lg-2 ">
                <div class="row justify-content-lg-start justify-content-end ">
                    <a href="home.htm" class="ml-lg-5 mt-lg-2 ">
                        <img src="${link}/img/logo-coral.png" alt="logo " width="200px ">
                    </a>
                </div>
            </div>
            <div class="row flex-end justify-content-end align-items-center col-6 d-lg-none ">
                <div class="justify-content-center ">
                    <button class="navbar-toggler " data-toggle="collapse " data-target="#nav " id="collapse">
            <span class="navbar-toggler-icon "><img src="${link}/img/list.png " alt=""></span>
        </button>
                </div>
            </div>
        </div>
    </nav>

    <!-- Bài thi  -->
    <div class="text-center mt-3">
        <h1>Test #<span class="testid">1</span> : <span class="testname">${thistest.name }</span></h1>
        <h3>Time (minutes): <span class="testtime">${thistest.time }</span>m</h3>
        <h4>Number of question:<span id="socau">${thistest.question }</span></h4>
        <h2 id="timer"></h2>
        <button class="btn bg-primary text-white" id="start">Start your test</button>
    </div>

    <div class=" container">
        <div class="d-none flex flex-column justify-content-center align-items-center" id="baithi">
            <form class="d-flex flex-column align-items-center">

                <embed src="${link }/pdf_for_test/${thistest.getPdf()}" type="application/pdf" width="1150px" height="500px">
                <div id="answer" class="row justify-content-center">
                    <div class="col">
                        <br>
                        A B C D
                    </div>
                    <!-- Add here -->
                </div>
                <button class="btn bg-primary text-white" id="end">Hand in</button>
        	</form>
        </div>
			
        <div class="d-none ketqua">
            <div class="ketquaheader ">
                <h1>${sessionScope.user.firstname } ${sessionScope.user.lastname } </h1>
                <h4>Day: ${today}</h4>

            </div>
            <div class="ketquacontent ">
                <h4> </h4>
                <h3> </h3>
                <h2> </h2>
                <a class="btn bg-danger text-white text-uppercase font-weight-bold" href="">Back</a>
                <a class="btn bg-primary hometi text-white text-uppercase font-weight-bold d-none" href="home.htm">Home</a>
           		<a class="btn bg-success conti text-white text-uppercase font-weight-bold d-none"  >Finish</a>
            </div>
        </div>
    </div>

    <script src="${link}/js/bootstrap.js "></script>
   
    <script src="${link}/js/jquery.js "></script>
    <!-- <script src="${link}/js/jqthi.js "></script> -->
    <script src="${link}/js/sweetalert.min.js"></script>
    <script>
        // Time go here
        var c = parseInt($('.testtime').text()) * 60;
        var t;
        function replaceAt(traloi,cauthu,chon) {
        	//var temp = traloi;
        	//var temp1 = traloi.substring(0, cauthu).trim();
        	//var temp2 = temp.substring(cauthu + 1).trim();
            return traloi.substring(0, cauthu) + chon + traloi.substring(cauthu + 1);
            //return temp1 + chon + temp2;
        }
        var socau = parseInt($('#socau').text());
        var allcautraloi = "${thistest.answer.trim() }";
        var traloi = "";
        let i;
        for (i = 0; i < socau; i++)
            traloi += "0";

        console.log("Bài thi có tất cả " + socau + " câu");
        console.log("Mặc định câu trả lời trước khi làm bài là " + traloi);
        
        $('#start').on('click', function() {
            timedCount()
            $('#baithi').removeClass('d-none');
            $(this).addClass('d-none');
            if(socau <=40)
            $('#answer').append(genAnswer(socau));
            $('input.choose').on('click', function() {
                var cauthu = parseInt($(this).prop('id').match('\\d+'));
                //var cauthu = parseInt($(this).prop('id'));
                //var ct = document.getElementById(10).id;
                
                //console.log("Cau thu" + ct);
                var chon = $(this).prop('id').match('\\w')[0];
                //var temp = traloi;
                console.log("Cau thu" + cauthu);
                console.log("chon " + chon);
                console.log("sau khi tra loi" + traloi);
                traloi = replaceAt(traloi, cauthu, chon);
                console.log("Mặc định câu trả lời trước khi làm bài là " + traloi);
                //console.log("Tra loi cua temp " + temp);
                
        })
        })
        $('.col').on('submit',function(e){ e.preventDefault() })
        
        
        function genAnswer(n) {
            var html = "";
            let i = 0;
            for (i=0; i < n; i++) {
            	 html += '<form class="col"  id="'+i+'" action="abc" method="POST">'+parseInt(i+1)+
                 '<input type="radio" name="chooseone" class="choose" id="A'+i+'"/>'+
                 '<input type="radio" name="chooseone" class="choose" id="B'+i+'"/>'+
                 '<input type="radio" name="chooseone" class="choose" id="C'+i+'"/>'+
                 '<input type="radio" name="chooseone" class="choose" id="D'+i+'"/>'+
                 '</form>';
            }
            return html;
        };
        var correct = 0;
        var rank ="";
        $('#end').on('click', function(e) {
            e.preventDefault();
            var i
            for ( i= 0; i < socau; i++)
            	{
	                if (traloi[i] === allcautraloi[i])
	                    correct += 1;
            	}
            var point = parseFloat((parseInt(correct)/parseInt(socau))*10).toFixed(2);
            console.log(point);
          	 if(point >=8 )
          		 {
          		 	rank="A";
          		 }
          	 else if(point >= 6.5)
          		 {
          		 	rank="B"
          		 }
          	 else if(point >=5)
          		 {
          		 	rank="C";
          		 }
          	 else if(point >=4)
          		 {
          		 	rank="D"
          		 }
          	 else
          		 rank="F";
          	console.log(rank);
            $('#baithi').addClass('d-none');
            $('.ketquacontent h4 ').text("Right Answers " + parseInt(correct) + '/' + socau);
            $('.ketquacontent h3 ').text("Point: " + parseFloat(point) + " | Rank: " + rank);
            if(point < 5)
        	{
            	$('.ketquacontent h2 ').text("Điểm của bạn thấp, ấn BACK để quay lại làm bài thi, ấn HOME để trở về lại khóa học để ôn");
            	$('.hometi').removeClass('d-none');
        	}
            if (point >=5 )
            {
            	$('.ketquacontent h2 ').text("Chúc mừng bạn vượt qua bài kiểm tra, ấn FINISH để lưu kết quả bài thi, ấn BACK để thi lại");
            	 $('.conti').removeClass('d-none');
            }
            $('.ketqua').removeClass('d-none');
            console.log(traloi);
            console.log(correct);
            // AJAX
        })
        
        $('.conti').on('click',function(){
        	   swal({
                   title: "Are you sure?",
                   text: "Do you want to record your point ?",
                   icon: "warning",
                   buttons: true,
                   dangerMode: true,
               })
               .then((willGo) => {
            	   if (willGo) {
            		  var rankc = rank.trim();
            		  var correctc = parseInt(correct);
            		  var datec = "${today}";
            		  var id = ${thistest.id};
             		      $.ajax({
                            type: 'POST',
                           url: 'testpage.htm',
                            data: {
                               correct: correctc,
                               rank: rankc,
                               id: id,
                               date: datec
                           },
                           success: function(d){
                        	   if(d=="true"){
                        	   async function sequence(){
                               	await swal("Recorded your result, continue to study hard!!!", {icon: "success",});
                               	//await location.reload(true);
                               	window.location.href = "coursepage.htm?id="+ ${thistest.getCourseidt().getId()};
                               	}
                        	   sequence();
                        	   }                              
                           },
                           error: function(e) {
                               swal('Oops', "Some error happening when deleting", "error");
                           }
             	   });
            	   }
            	   else
            		   {
            		   swal("Your data is safe!");
            		   }
               
        });
        }) 
        function timedCount() {
        var hours = parseInt(c / 3600) % 24;
        var minutes = parseInt(c / 60) % 60;
        var seconds = c % 60;

        var result = (hours < 10 ? "0" + hours : hours) + ":" + (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds < 10 ? "0" + seconds : seconds);
        $('#timer').html(result);
        $('#end').on('click', function() {
            clearTimeout(t);
            //AJAX
        })
        if (c == 0) {
            //setConfirmUnload(false);
            //$("#quiz_form").submit();
            $('#end').click();
            alert("Hết giờ làm bài");
            return;
        }
        c = c - 1;
        t = setTimeout(function() { timedCount() }, 1000);
        }
    </script>
    <!-- Bài thi  -->
    <!-- <script src="${link}/js/validate.js "></script> -->

</body>

</html>