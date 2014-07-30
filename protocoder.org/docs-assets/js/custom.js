
  function addAnimMenu(item) { 

    //drawOverlay();
    $("#" + item).hover(function() {
      console.log("hola");
      $("#bg-header").addClass(item);
    }, function() { 
      $("#bg-header").removeClass(item);
    });



  }


  $(document).ready(function() {
    addAnimMenu("home");
    addAnimMenu("contribute");
    addAnimMenu("download_protocoder");
    addAnimMenu("forum");
  });
  
  