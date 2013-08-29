$(document).ready(function() {  
  
  
   saveDecider = function(){
      var value=session.getValue();
      if(global_status['editing']==false){ // If this condition is true we are saving the file for first time  
        createToServer(value);
        $('#saveblock').show('100'); 
      } else {
        saveToServer(value);
      }
    }
$('#saveButton').bind('click',saveDecider);
//showRoutes()    


function createToServer(value){
      //value=form.val() //TODO encode
      $('#saveButton').addClass('greeny')
      global_status['saved']=true;   
			$.ajax({
					type: "POST",
					url: "/sketch/create",
					data: {'content':value},
					success: function(response) {
            global_sketch_id=response.id;
            $('#show_url_copy').val(base_url+"/sketch/view/"+response.view_id).show();      
            $('#edit_url_copy').val(base_url+"/sketch/edit/"+global_sketch_id).show();
            localStorage.setItem('edit_url', base_url+"/sketch/edit/"+global_sketch_id);                         
            global_status['editing']=true;
            global_status['sketch_id']=global_sketch_id;
            window.history.pushState("", "Programa la plaza: nuevo sketch", "edit/"+global_status['sketch_id']);
            //+info http://stackoverflow.com/questions/824349/modify-the-url-without-reloading-the-page
  					 showMessagehide('success',"File saved")
					},
					error: function(e){
					showMessagehide('error',msg.error_create)
					}					
		  });
}

function saveToServer(value){
      //value=form.val() //TODO encode
      $('#saveButton').addClass('greeny')
      global_status['saved']=true;  
      localStorage.setItem('edit_url',  base_url+"/sketch/edit/"+global_status['sketch_id'] );  
			$.ajax({
					type: "POST",
					url: "/sketch/save",
					data: {'sketch_id':global_status['sketch_id'],'content':value},
					success: function() {
					   					 showMessagehide('success',"File saved")
					}
  });
}

}); // fin document onload
