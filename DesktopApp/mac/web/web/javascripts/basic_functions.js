base_url="http://programalaplaza.medialab-prado.es";





    		function removeSketch() {
    				//$("iframe")[0].remove();
    				$("iframe").remove();
    		}

    		function processingStatus(isWorking, errorName, errorDescription, err) { 
    		    procInit = true;
    			if (isWorking == true) {
    				$("#processing_status").css('background-color', 'rgb(8, 151, 163)').css({opacity: 0.0, visibility: "visible"}).animate({opacity: 1.0});
    				$("#processing_status").find("#msg").text("running");
    				$("#processing_status").find("#errorName").empty();
    				$("#processing_status").find("#errorDescription").empty();
    			} else {
    				$("#processing_status").css('background-color', 'rgb(163, 8, 8)').css({opacity: 0.0, visibility: "visible"}).animate({opacity: 1.0});
    				$("#processing_status").find("#msg").text("compiling error");
    				$("#processing_status").find("#errorName").text(errorName).fadeIn();
    				$("#processing_status").find("#errorDescription").text(errorDescription).fadeIn();
    				$("#processing_status").find("#errorDescription").empty().append(errorDescription).append( "<br/>Check your console.log There is probably more info there").fadeIn();		
    				console.log(err)								
    			}
    		}

    		function getCanvasImg() { 

    			var c =	$("iframe")[0].contentDocument;
    			var canvas1 = $(c).find(".display")[0];		
    			return canvas1.toDataURL(); 

    		}

    		function previewCanvasImg() {
    			var canvas = getCanvasImg();

    			var img = $("#cover")[0];
    			img.src = canvas;
    		} 

    /*only uploads snapshot, it doesn't change database ***/		
    		function sendCanvasImg() {
    		  var filename = $("#file").val();
          var mcanvas=document.getElementById('myCanvas');
          $.ajax({
            type: "POST",
            url: "/upload",
            enctype: 'multipart/form-data',
            data: {'name': global_status['view_id'],
               'theFile': $("#cover")[0].src
            },
            success: function (ret) {
              //showMessage("success","Data Uploaded: ");
              global_status['upload_snapshot']=false
              global_status['snapshot']=true
            },
            complete: updateValues()
          }) ;

    			//req = new XMLHttpRequest();  
    			//req.open("POST", "save.php", true); 
    			//req.setRequestHeader("Content-Type", "multipart/form-data");

    			//var data =  "img="+canvas1.toDataURL(); 
    			//data += "&name=" + $('#name').val();
    			//data += "&msg=" + $('#msg').val(); 
    			//data += "&author=" + $('#author').val();

    			//req.onreadystatechange = callbackFunction;
    			//$('#cfdraw').fadeOut(1000);
    			//req.send(data); 

    			//window.location = 'http://cityfireflies.com/create/respuesta.html'	

    		}

    		function updateFormsScreen(){
    		  				$( '#w_email'  ).val( $('#w_email2').val()  )
     						  $( '#w_author' ).val( $('#w_author2').val() )
    						  $( '#w_title'  ).val( $('#w_title2').val()  )
    						  $( '#w_web'  ).val( $('#w_web2').val()  )
    						  if(global_status['snapshot']==true){
    						    d = new Date();
      				      $('#cover-down').attr('src','../upload/'+global_status['view_id']+'.png?'+d.getTime() )
    						  }		
    		}
    /*****  main error msgs: too small screen. No lcalstorage***/		
    		function preventSmallScreens() {
    				if ($(window).width() > 700 ) {
    				$("#makeitbigger").hide();

    				} else {
    					$("#makeitbigger").show();
    				} 			
    		}


    		function showArtistInfo(b) {
    			$("iframe")[0].contentWindow.showArtistInfo(b);

    		}

    		function setPreviewData() {
    			$("iframe")[0].contentWindow.changeSketchTitle($("#w_title2").val());
    			$("iframe")[0].contentWindow.changeAuthorName($("#w_author2").val());
    			$("iframe")[0].contentWindow.changeAuthorURL($("#w_web2").val());

    		}

    		function previewSketch() {
    			$("iframe")[0].contentWindow.preview();
    			setPreviewData();

    			var posX = document.width / 2 - $("iframe").width();
    		 	$("iframe").css({'position':'absolute','z-index':'2', 'border': '2px solid #bbb'
    			}).animate({ 
    								        width: "576px",
    												height: "471px",
    												top: "85px",
    												left: '25%',
    								      }, 200 );

    			$("#previewProcessing").fadeIn('slow');


    			showArtistInfo(true);
    			setTimeout(function() {
    				showArtistInfo(false);

    			}, 4500);
    		}

    function adjustEditorSize(){
      w_width=$(window).width()
      new_width=w_width-600;
      if(new_width<550) new_width=550;
      $('#central_area').css('width',new_width)
      $('#editor_wrapper').css('width',new_width)
      $('#editor').css('width',new_width)
    }

var global_status = [];
global_status['playingSketch'] = false;
    /***********  *********/		

    		$(document).ready(function() {			


    					//play triggers video && sketch
    					adjustEditorSize();


    					$("#playButton").click(function() { 						
    						if(global_status['playingSketch'] == false) {
    							//$("video")[0].play();
    							saveAndReload(session);
    							$('#playButton span:first-child').text("Stop"); 
    							$('#playButton span.ui-icon').removeClass('ui-icon-play');
    							$('#playButton span.ui-icon').addClass('ui-icon-stop');
    							$(this).addClass('redy')
    							global_status['playingSketch'] = true;
                    //con.play();
    							//saveAndReload(session); 

    						} else {
    							$('#playButton span:first-child').text("Play");
    							$('#playButton span.ui-icon').addClass('ui-icon-play');
    							$('#playButton span.ui-icon').removeClass('ui-icon-stop');
    				
    							$(this).removeClass('redy')
    							removeSketch(); 
    							global_status['playingSketch'] = false;
    							$("#processing_status").css('background-color', 'rgba(255, 91, 77)').animate({opacity: 0.0}); 
    						}
    					}); 					


    					$("#tipButton").click(function() {
    						//window.open($('iframe').attr('src'),'mywindow','width=400,height=200');
    						$('#tips').empty();
    	    	    $('#tips').load("./html/ayuda.html", function(){
    	    	      $("#header").animate({"height": "520px"}, function() {
    								$("#tips").animate({"opacity": "1.0"}).fadeIn(); 
    							});
    	    	    });

    	    	$('#hide-tips').live('click',function(){
    	    	$('#tips').empty();
            $("#header").animate({"height": "50px"}, function() {
    							});
            }  );


    							//$("#tips").fadeOut(function() {
    							//	$("#header").animate({"height": "38px"});
    							//	});
    					});


    					$("#previewButton").click(function() {
    						//console.log("hola");
    						previewSketch();

    					});

    					$("#close_preview").click(function() {
    						//console.log("hola");
    						//previewSketch();
    						$("#previewProcessing").fadeOut('slow');
    						saveAndReload(session);

    					});





    					$("#sendButton").click(function() {
    								$("#submit_details-wrapper").fadeIn('fast');
    								//$(this).fadeOut();
    								$('html, body').animate({
          	           scrollTop: $("#submit_details-wrapper").offset().top} , 2000);
    				  });



    				$("#w_author, #w_author2").keyup(function() {
    					$("iframe")[0].contentWindow.changeSketchTitle($(this).val());
    				});

    				$("#w_title, #w_title2").keyup(function() {					
    					$("iframe")[0].contentWindow.changeAuthorName($(this).val());
    				});

    				$("#w_web, #w_web2").keyup(function() {					
    					$("iframe")[0].contentWindow.changeAuthorURL($(this).val());
    				});




    /****** FORMULARIOS DE ENVIO ***************/

    					$("#submit_form").click(function() {

    					//saveAndFinish(self,w_sketch_id,w_email,w_author,w_content="",w_title="",w_description="",w_twitter=""):
    						var data={
    						  'w_sketch_id':global_status['sketch_id'],
    						  'w_email':$('#w_email').val(),
     						  'w_author':$('#w_author').val(),
    						  'w_title':$('#w_title').val(), 						
    						  'w_description':$('#w_description').val(),						    
    						  'w_web':$('#w_web').val(),
    						  'w_content':session.getValue()
    						}
    						$.ajax({
                  type: "POST",
                  url: '/sketch/saveAndFinish',
                  data: data,
                  success: function(returndata){
                        localStorage.setItem('IhaveSubmitted', "YES");
                        location.href=base_url+'/sketch/view/'+global_status['view_id']
                      }
                  });						
    					});

    				$('#update-name').click(function(){
    				  if(global_status['upload_snapshot']==true){
    				    sendCanvasImg()
    				    //also updateValues with the image so it is like sendcanvas+updateValues
    				    }
              else{
                updateValues()
              }
            });  						

             updateValues=function(){
              var data={
    						  'w_sketch_id' : global_status['sketch_id'],
    						  'w_email' : $('#w_email2').val(),
     						  'w_author' : $('#w_author2').val(),
    						  'w_title' : $('#w_title2').val(),
    						  'w_web' : $('#w_web2').val(),
    						  'w_snapshot' : global_status['snapshot']
    					}

    				if (data.w_web.indexOf("@") == 0) {

            }	
    				else if (data.w_web.indexOf("http://") !== 0 ) {
                data.w_web = "http://" + data.w_web
                 $('#w_web2').val(data.w_web)
            }

    					$.ajax({
                  type: "POST",
                  url: '/sketch/updateName',
                  data: data,
                  success: function(returndata){
                        showMessagehide("success","cool, data uploaded")
                        updateFormsScreen()
                      },
                  error:function(returndata){
                        showMessagehide("error","Sth went wrong. check your net connection. But maybe is our fault. ")
                    }                  
              });        
            }						


    					$("#cancel_form").click(function() {
    					     //$("#sendButton").fadeIn();
    								$("#submit_details-wrapper").fadeOut('fast');					
    						    $('html, body').animate({
          	           scrollTop: $("#content").offset().top} , 2000);
    					});



    					function callbackFunction(){
    						//console.log(" aaa:" + req.responseText); 
    						filename=req.responseText;
    						window.location = 'http://cityfireflies.com/create/respuesta.html?drawing='+filename
    					}


    					preventSmallScreens();

    					$(window).resize(function() {
    						preventSmallScreens();
    						adjustEditorSize();
    					});


              $( "#check" ).button({
                  icons: {
                    primary: "ui-icon-check"
                  },
                  text: false
              });


    // -------- VIDEO CTRL -----------          
              $( "#play-ctr" ).button({
                  icons: { primary: "ui-icon-play" }, text: false 
              }).click(function(){
                      //if (typeof con === 'undefined'){
                       // con = new websocketEmulator();
                      //}
                      $('video')[0].play();                  
                      camera.connect();

              });
              $( "#pause-ctr" ).button({
                  icons: { primary: "ui-icon-pause" }, text: false 
              }).click(function(){
                      $('video')[0].pause();
                      if(global_status['playingSketch'] == true && typeof(con)!=undefined)
                        camera.pause();
              });
              $( "#restart-ctr" ).button({
                  icons: { primary: "ui-icon-arrowreturnthick-1-s" }, text: false 
              }).click(function(){
                  $('video')[0].currentTime = 0 
                  if(global_status['playingSketch'] == true && typeof(con)!=undefined)
                    camera.stop(); camera.connect ()
              });

              $( "#check" ).change(function(){
                if(this.checked) {
                   $('#videocontainer').show();
                }else{
                   $('#videocontainer').hide();          
                }         

              });
              $('video').bind('ended',function(){
                //con.play() //como el video está en modo loop, reiniciamos el enviador
              });
        /*** end of video ctrl **/

          		 // Show message
          		 // When message is clicked, hide it
          		 $('.message').click(function(){			  
          				  $(this).animate({top: -$(this).outerHeight()}, 500);
          		  });
        /******* end of MSGS ***********/

        /**** copy links ****/ 		  
          	$('#copylinks').click(function(e){
                    $('#saveblock').css('top',e.pageY+5+'px')
                    $('#saveblock').css('left',e.pageX-190+'px')
                    $('#saveblock').toggle('100');                    
                } 
            );
            $('#close-links-popup').click(function(e){
                  $('#saveblock').fadeOut();
                } 
            );

            /* Set up the clippies! */
            var clippy_swf = "../javascripts/clippy.swf";
      			//$('.clippy').clippy({ clippy_path: clippy_swf });

      			$('#show_url_copy').change(function()
      			{
      				$('#copy_text').html('').clippy({'text': $(this).val(), clippy_path: clippy_swf });
      			}).change();

      		  $('#edit_url_copy').change(function()
      			{
      				$('#copy_text2').html('').clippy({'text': $(this).val(), clippy_path: clippy_swf });
      			}).change();

      /**** end of copy links ****/


      			$('#reset-button').click( function(){
              //TODO preguntar si se está seguro?
               console.log("clicked reset");
               localStorage.removeItem('edit_url');
        			 localStorage.removeItem('code');
               location.href=base_url+'/sketch'
      			});  			

      			$('#loadpreviousurl-button').live('click',function(e){
        			if(localStorage.getItem('edit_url') != undefined || localStorage.getItem('edit_url') != null){
                location.href=localStorage.getItem('edit_url');  			
              }
              e.preventDefault()
              return false;              
      			});          		

      		  $('#removereviousurl-button').live('click',function(e){
        			 localStorage.removeItem('edit_url');
        			 localStorage.removeItem('code');                   
      			});  


      

    				$("#cover").click(function() {
    				  if(global_status['playingSketch']==true){
      					previewCanvasImg();
      					global_status['upload_snapshot']=true
      					global_status['snapshot']=true
    					}
    				});


    	/*****demos menu ****/
        	generateFileList()				
        	   //show/hide menu
    			   $('#examples-button').click(function(e){ 
                    $('#fileList').css('top',e.pageY+5+'px')
                    $('#fileList').css('left',e.pageX-190+'px')
                    $('#fileList').toggle('100');                    
                } 
            );
            //when click
            $('.file').click(function(){            
                mfile=$(this).find('.title a').attr('name')
                location.href=base_url+'/sketch/view/'+mfile;          
            });		

    			});

          window.onbeforeunload = confirmaSalida; 

          function confirmaSalida(){
             if ( global_status['saved'] == false && global_status['editing'] == true ) {
                    return "You have not saved changes. Are you sure you want to exit?";  
             }
          }







    	    // global_status['remote_code']=String ("qq" ) ;
    	     var edit_status=String( "qq2" )
           // global_status['sketch_id']= String("{{sketch_id}}") 
        //    global_status['view_id']= String("{{view_id}}" )
    	  //   if(edit_status  == "edit")
      	//     global_status['editing']=true //editing = true cuando estamos editando un fichero del servidor
      	  // else
      	 //    global_status['editing']=false 
      	    //con = new websocketEmulator(); 	     
      	 //  global_status['saved']=true;
         //  global_status['server-error']= String("qq5");
         //  global_status['snapshot'] = false;
         	base_url ="http://127.0.0.1:8081";



function generateFileList(){
    $('#fileList').empty()
//        $('#fileList').append('<ul> <li> <a href="#"> </a> </li> </ul>')
    $('#fileList').append("<h2>A few demos to learn:</h2>" )
    $('#fileList').append('<div id="file-content"> </div>')
    $.each(fileList,function(index,file){

        $('#file-content').append( 
            '<div class="file">\
                <span class="title">\
                   <a href="#" class="fileload" name="'+"file.archivo"+'"> '+ "file.titulo"+' </a>\
                </span>\
                <span class="subtitle">' + "file.descripcion"+ '\
                </span>\
             </div>' );
    });
      $('#fileList').append('<span class="arrow"></span> ');        

} 


fileList=[
            {   titulo: "Random Circles",
                archivo: "94dbc956fdccc538895de3cbad8dae4c",
                descripcion: "Start here! :D"
            },
            {   titulo: "Circles Moving",
                archivo: "a69e78b48a52bbcb011d9fbc0f41648f",
                descripcion: "They look small but imagine this in 10x10 meters!"
            }, 
            {   titulo: "Using Images",
                archivo: "af5958eff6be9c69a20571cfc8b2e0a6",
                descripcion: "Load images and displaying them"
            }, 
            {   titulo: "Tracking",
                archivo: "ec03ffe03e5b0afbda21ba5b2c6549f9",
                descripcion: "Basic demo of video tracking "
            }, 
            
            {   titulo: "Advance - Use jQuery",
                archivo: "0ed19ece4cf54612eb293700f5af171d",
                descripcion: "jQuery for more complicated data manipulation"
            },
            
            /*
            {   titulo: "Advance - Sketch life time",
                archivo: "ec03ffe03e5b0afbda21ba5b2c6549f9",
                descripcion: "Use the API to know how long your sketch will last been shown"
            },
            */
            
            
        ]