<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en-us" class="no-js"  itemscope itemtype="http://schema.org/Article">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Android rapid prototyping tool. IOIO, OSC, websockets.">
    <meta name="author" content="">
    <!--
    <link rel="shortcut icon" href="../../docs-assets/ico/favicon.png">
    -->
    <title>Protocoder | Hardware and software prototyping for Android devices</title>


    <!-- Bootstrap core CSS -->
    <link href="./dist/css/bootstrap.css" rel="stylesheet">

    <!-- Just for debugging purposes. Don't actually copy this line! -->
    <!--[if lt IE 9]><script src="../../docs-assets/js/ie8-responsive-file-warning.js"></script><![endif]-->

    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->

    <!-- Custom styles for this template -->
    <link href="./docs-assets/css/template.css" rel="stylesheet">
    <link href="./docs-assets/css/custom.css" rel="stylesheet"> 
    <link href="http://netdna.bootstrapcdn.com/font-awesome/4.0.3/css/font-awesome.css" rel="stylesheet">
  
    <link href='http://fonts.googleapis.com/css?family=Roboto:400,100,100italic,300,300italic,400italic,500,500italic,700,700italic,900,900italic|Roboto+Condensed:300italic,400italic,700italic,400,300,700|Roboto+Slab:400,700,100,300' rel='stylesheet' type='text/css'>


    <link href="docs-assets/libs/prettify/prettify.css" type="text/css" rel="stylesheet" />
    <link href="docs-assets/libs/prettify/sunburst.css" type="text/css" rel="stylesheet" />

    <script type="text/javascript" src="docs-assets/libs/prettify/prettify.js"></script>
  </head>
<!-- NAVBAR
================================================== -->
  <body onload = "prettyPrint()">
    <div class="navbar-wrapper">
      <div class="container">

        <div class="navbar navbar-default navbar-static-top" role="navigation">
          <div class="container">
            <div class="navbar-header">
              <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
              </button>
              <a class="navbar-brand" href="#">protocoder</a>
            </div>
            <div class="navbar-collapse collapse">
              <ul class="nav navbar-nav">
                <li id = "home" class="active"><a href="#">Home</a></li>
                <li id = "contribute"><a href="https://github.com/victordiaz/protocoder/wiki">Contribute</a></li>
                <li id = "download_protocoder"><a href="#download">Download</a></li>
                <li id = "forum"><a href="https://groups.google.com/forum/?hl=es#!forum/protocoder">Forum</a></li>
                <!-- <li id = "showcase"><a href="./showcase.html">Showcase</a></li> -->

                <!--
                <li><a href="#about">About</a></li>
                -->
                <!--
                <li class="dropdown">
                  <a href="#" class="dropdown-toggle" data-toggle="dropdown">Dropdown <b class="caret"></b></a>
                  <ul class="dropdown-menu">
                    <li><a href="#">Action</a></li>
                    <li><a href="#">Another action</a></li>
                    <li><a href="#">Something else here</a></li>
                    <li class="divider"></li>
                    <li class="dropdown-header">Nav header</li>
                    <li><a href="#">Separated link</a></li>
                    <li><a href="#">One more separated link</a></li>
                  </ul>
                </li>
                -->

              </ul>
            </div>
          </div>
        </div>

      </div>
    </div>


    <!-- Carousel
    ================================================== -->
    <div id="myCarousel" class="carousel slide" data-ride="carousel">
      <!-- Indicators 
      <ol class="carousel-indicators">
        <li data-target="#myCarousel" data-slide-to="0" class="active"></li>
        <li data-target="#myCarousel" data-slide-to="1"></li>
        <li data-target="#myCarousel" data-slide-to="2"></li>
      </ol>
      -->

      <div class = "darken"> </div>
      <div class="carousel-inner">
        <div class="item active" id = "bg-header">
          <div class="container">
            <div class="carousel-caption">
              <h1>Rapid prototyping for Android</h1>
              <p id = "description-header">Prototype interactions and apps that use sensors, network, sound and microcontrollers. No need to install anything in your computer. Just a web browser.</p>
              <p><a class="btn btn-lg btn-primary" href="https://github.com/victordiaz/protocoder" role="button">Fork me on Github!</a></p>
            </div>
          </div>
        </div>
      </div>
    </div><!-- /.carousel -->



    <!-- Marketing messaging and featurettes
    ================================================== -->
    <!-- Wrap the rest of the page in another container to center all the content. -->

    <div class="container marketing">

      <!-- Three columns of text below the carousel -->
      <div class="row">
        <div class="span-12 pagination-centered">
          <img class="img-circle" src="./docs-assets/images/separation.png" alt="separation">
          <h2>What is protocoder?</h2>
          <p>Protocoder is a coding environment + Javascript framework for quick prototyping on Android devices having some emphasis on rapid hardware hacking. Just download the app on your phone and you are ready to code. 

          </p>

          <img class = "" style="max-width: 100%;" src = "./docs-assets/images/explanation_diagram.png"> </img>

          <!-- 
          <div class = "flex-video widescreen">
          <iframe src="//www.youtube.com/embed/dNHltOhTs2o" frameborder="0" allowfullscreen></iframe>
          </div>
        --> 
        </div><!-- /.col-lg-4 -->
       </div>

      <div class="row">
        <div class="col-lg-6 pagination-centered">
          <img class="img-circle" src="./docs-assets/images/separation.png" alt="separation">
          <h2>How does it work?</h2>

          <p>Install the app in your Android device and access the web IDE from your computer. Code in javascript using the protocoder framework. No needs to write dozends of lines to access sensors or write an UI, simple to use, fast to code.</p>
        
          <pre class="prettyprint">
  //how to get sensor data
  sensors.startAccelerometer(function(x, y, z)) { 
    console.log(x + " " + " " + y + " " + z); 
  }

  //send a sms
  android.sendSMS(number, "text");

  //play a video
  ui.addVideoView("fileName", 0, 0, 500, 200);
          </pre>

          <p>
          Protocoder runs internally a webserver and a websockets server to serve you an IDE that you can use wirelessly or via USB cable using the companion app. It supports most android hardware functionality, the only requirement is running Android version > 4.0 in your device. That means that it runs on phones, tablets, glasses, tv dongles and smart watches :) 
          </p>

        </div><!-- /.col-lg-4 -->


        <div class="col-lg-6 pagination-centered">
          <img class="img-circle" src="./docs-assets/images/separation.png" alt="separation">
          <h2>Why</h2>
          <p>
          Android tools are getting really mature for developing market ready apps. Still, there is a big need for a fast way of prototyping ideas and interactions without having to wait long times to set up environments or wait eternities to compile things.  
          </p>
          <p>
          In Protocoder you can see your changes in seconds, no delays, no more distractions, feel the flow baby.
          </p>
        </div><!-- /.col-lg-4 -->
      </div><!-- /.row -->


      <!-- START THE FEATURETTES -->
      <hr class="featurette-divider">

      <div class="row featurette">
        <div class="col-md-7">
          <h2 class="featurette-heading">Pocket IDE. <span class="text-muted">Bye bye cloud</span></h2>
          <p class="lead">Protocoder its an environment that you can carry in your pocket. You might go to a different computer and you will still have the same IDE accessible from any modern web browser. 
          And hey, we want the editor to be yours, and be accessible from any place, at any time. That's why its not a cloud-based service. Protocoder lives in your Android device, not in a computer that you don't own. 
          </p>
        </div>
        <div class="col-md-5">
          <img class="featurette-image img-responsive" src="./docs-assets/images/protocoder_ide.png" alt="Protocoder IDE">
        </div>
      </div>

      <hr class="featurette-divider">

      <div class="row featurette">
        <div class="col-md-5">
          <img class="featurette-image img-responsive" src="./docs-assets/images/protocoder_run.png" alt="Generic placeholder image">
        </div>
        <div class="col-md-7">
          <h2 class="featurette-heading">An app that host apps. <span class="text-muted">yep! </span></h2>
          <p class="lead">Protocoder host all your apps, you can create as many as you want and you can carry / edit / hack them with you. Do you want to share them? No problem, just zip the project and send it to whoever you want that has protocoder installed. </p>
        </div>
      </div>

      <hr class="featurette-divider">

     <div class="row featurette">
          <div class="col-md-7">
            <h2 class="featurette-heading">A Dashboard. <span class="text-muted">Remote control! </span></h2>
            <p class="lead">Imagine that you want to use your phone as a brain for your robot. You have the phone between wires and cases, which might be a bit difficult to access for debugging it. 
            No problem. Use the dashboard to see wirelessly information from the sensors or control it. A pieze of cake!</p>
          </div>
          <div class="col-md-5">
            <img class="featurette-image img-responsive" src="./docs-assets/images/protocoder_dashboard.png" alt="Generic placeholder image">
          </div>
        </div>



    <hr class="featurette-divider">
 	<div class="row featurette">
      <div class="col-md-5">
            <img class="featurette-image img-responsive" src="./docs-assets/images/protocoder_libraries.png" alt="Generic placeholder image">
      </div>
        <div class="col-md-7">
        	<h2 class="featurette-heading">More libraries. <span class="text-muted">Be expresive. </span></h2>
        	<p class="lead">Protocoder wants to share the philosophy of amazing projects such as Processing.org, Pure Data, Open Street Maps and many more. That's why they are included so you can start using them straight away. </p>
      	</div>
     

      </div>
    

    <hr class="featurette-divider">
 	<div class="row featurette">
      <div class="col-md-7">
        <h2 class="featurette-heading">Expand your device. <span class="text-muted"> </span></h2>
        <p class="lead">Connect your Arduino, IOIO, makey makey or other boards to your device and create physical stuff with them. Note: Arduino will work only if your device supports USB OTG</p>
      </div>
      <div class="col-md-5">
        <img class="featurette-image img-responsive" src="./docs-assets/images/boards.png" alt="Generic placeholder image">
      </div>
    </div>



    <hr class="featurette-divider">
  	<div class="row featurette">
        <div class="col-md-5">
          <img class="featurette-image img-responsive" src="./docs-assets/images/protocoder_livecoding.gif" alt="Generic placeholder image">
        </div>
        <div class="col-md-7">
          <h2 class="featurette-heading">Live execution. <span class="text-muted"> be water </span></h2>
          <p class="lead">Select the lines, press Cmd / Control + Shift + X and see how things change on your device. Once you master, you will wonder why mobile development tools don't have this as default.</p>
        </div>
      </div>


	<!-- 
    <hr class="featurette-divider">
  	<div class="row featurette">
        <div class="col-md-5">
          <img class="featurette-image img-responsive" src="./docs-assets/images/protocoder_run.png" alt="Generic placeholder image">
        </div>
        <div class="col-md-7">
          <h2 class="featurette-heading">More than phones. <span class="text-muted"> </span></h2>
          <p class="lead">Protocoder works on any Android device with a version higher than 4.0. It means that works in other devices such as tablets, Android tv dongles, glasses, smart watches and many more. Why not creating your own device?</p>
        </div>
      </div>  
  	-->

    <hr class="featurette-divider">



      <!-- /END THE FEATURETTES -->

     <div class="row">
        <div class="span-12 pagination-centered">
        <h1>Download</h1>
            <a name = "download" id = "download"></a>
            <p>Download the apk in your phone. Dont forget to enable installing apps for unknown sources. 
            Once you install it, connect your Android device and your computer to the same Wifi network and and input the ip address in your browser.</p>
            <p><strong> If you are updating, don't forget to reinstall the examples in the Settings</strong></p>
            <p> Protocoder comes in two variants, normal and extended. The only difference is that the Normal version doesnt have SMS and call permissions therefore you cannot use the related API methods. </p>
            <a class="btn btn-lg btn-primary" href="./downloads/protocoder-normal-0_97pre.apk" role="button">Download Normal APK v0.97pre</a>
            <a class="btn btn-lg btn-primary" href="./downloads/protocoder-extended-0_97pre.apk" role="button">Download Extended APK v0.97pre</a>

         	<br />
         	<br />
         	<p>
         	<i> Last version 0.97pre released the 29/11/2014 </i> <a href = "./downloads/protocoder_097pre_changelog.txt"> Changelog </a>
         	</p>

            <!--
            <a class="btn btn-lg btn-primary" href="./downloads/protocoder-release-0_8.apk" role="button">Get it from the Play Store</a>
            -->

            <!-- 
            <p>
            If you don't have a Wifi, you can still use protocoder using the USB companion app. Right now it only works for Mac but soon will work in Linux and Windows. 
            </p>
            -->  
            <p>
            If you know a bit of adb trickery you can still use protocoder with <strong>USB cable</strong> . Just write this command line in your system console and then go to http://localhost:8585 in your browser. Please note that you need to install adb in your system. 
            </p>

          <pre class="prettyprint">
    > adb forward tcp:8585 tcp:8585
    > adb forward tcp:8587 tcp:8587</pre>
	
    		<p>
            If you want you can download Adb for Mac, Linux and Windows using this zip file</p>
            <a class="btn btn-lg btn-primary" href="./downloads/adb.zip" role="button">Download adb </a>
            </p>

          </div>
        </div>

      <div class="row">
          <div class="span-12 pagination-centered">
          <img class="img-circle" src="./docs-assets/images/separation.png" alt="separation">
          <h2>Credits</h2>
          <p>Protocoder is a project originated by Victor Diaz developed using a nomadic approach in different contexts that varies from this or that country, parks, libraries, hotel lobbys, cafes, restaurants, cars, here and there. </p>
          <p>With the great support of <a href="http://bq.com">bq.com</a> since 09/2014</p>
          <p>Started during the MakeWithMoto tour with Motorola ATAP (now Google ATAP)</p>
          <p> Special thanks to José Juan Sánchez Hernández, Ali Javidan, Gopi Palaniappan, Alan Sien and the MWM Crew :)</p>
          <!--
          <p>
          During the <a href="https://makewithmoto.squarespace.com">roadtrip</a> we hacked together with university students and hackerspaces fellows and we realized we needed a tool for fast prototyping on Android that could access microcontrollers such as IOIO board and Arduino. We decided to start coding it during the trip. Protocoder has been </p>  
      	  --> 
        </div><!-- /.col-lg-4 -->
      </div><!-- /.row -->


     <div class="row">
        <div class="span-12 pagination-centered">
        <p class="pull-right"><a href="#">Back to top</a></p>
        </div>
    </div>

      <!-- FOOTER -->
      <footer>
        <span> follow protocoder news on </span>
        <a href = "https://twitter.com/victornomad"><i class="fa fa-twitter-square fa-2x"></i></a> 
<!--         <a href = "http://www.youtube.com/channel/UCv_npDT0QRW9lXAzSpZDocg?feature=watch"><i class="fa fa-youtube fa-2x"></i></a> -->
        <a href = "https://github.com/victordiaz/protocoder"><i class="fa fa-github-alt fa-2x"></i></a> 
        <!-- 
        <a href = "https://twitter.com/protocoderorg"><i class="fa fa-google-plus-square fa-2x"></i></a> 
        -->
        <div>
        </div>
      </footer>

    </div><!-- /.container -->


    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="https://code.jquery.com/jquery-1.10.2.min.js"></script>
    <script src="./dist/js/bootstrap.min.js"></script>
    <script src="./docs-assets/js/holder.js"></script>
    <script src="./docs-assets/js/custom.js"></script>

    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.0/jquery.min.js"></script>
  

     <script type="text/javascript">
        var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
        document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
      </script>
      <script type="text/javascript">
        try {
          var pageTracker = _gat._getTracker("UA-45674106-1");
        pageTracker._trackPageview();
        } catch(err) {}
      </script>

  </body>
</html>
