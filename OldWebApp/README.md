### Webapp side of codewithmoto

CodeWithMoto has two components, the Android side and the web component. The `skinnyapp` is the webcomponent side of code with moto. 

Inside the web component, there are two components. These are the android webview and the WebIDE view. The web IDE is built by compressing all of these files into small, single files that get loaded and sent to the webview the app. This web app is powered by AngularJS. We'll talk about how to modify and extend this side in a few minutes. All of the files that get compressed and sent with this view are listed in the two files:

    app/assets/js/application.coffee
    app/assets/js/vendor.coffee

The `vendor.coffee` file loads the all of the javascript files that we need in the vendor directory. The `application.coffee` file includes all of the webIDE view application javascript files. 

To load a new file inside either one of these files, you can use the prefix: `#= require "FILENAME"`. When the the skinnyapp is built, this will get appended into the single, minified file. Inside the `app/assets/js/app` directory, there is the main component of the AngularJS application: `main.js.coffee`. This file defines the AngularJS root.

The android webview is powered by the javascript files in the `app/assets/js/android_side` directory. 

The two places that are "important" inside the skinnyapp directory. These are the `app/assets` directory and the `app/views` directory.

#### Adding to the WebIDE

The WebIDE is an AngularJS app. This is a really quick introduction to AngularJS.

AngularJS uses it's own internal event loop in the browser that "watches" for changes on components in the DOM (html) and responds to them immediately. This enables the interface to be highly responsive without building a lot of boilerplate code. For instance, when we switch from the builder view to the help view/instruments panel, the browser doesn't refresh, just the template does. 

This is called `routing`. The routing is defined in the `app/assets/js/config/routes.js.coffee.erb` file:

    app.config ['$routeProvider', ($routeProvider) ->
      $routeProvider.when('/',
        templateUrl: "/assets/home.html"
        controller: 'HomeController'
      ).when('/help',
      ...
  
This should be relatively self-explanatory, but basically the `when` method catches the browser location and loads the appropriate controller and template that will load up inside the main view. The templates are located in the `app/assets/js/templates` directory.

An AngularJS controller is pretty simple as well. The controller is where the action is defined for the page. All of the components of the HTML is attached to and has access to the `$scope` variable inside the controller. For instance, the `code` that gets loaded into the view is attached to the `$scope.code` variable. The `$scope.code` is loaded by the `webSocketService`.

The `webSocketService` is an AngularJS `service` or singleton object. Basically, the `webSocketService` is the controller of the main interaction of the websocket communication between the Android app and the WebIDE. Unless you are changing some advanced features, you won't need to touch the `webSocketService` for much. The API is pretty simple:

    subscribe(event, callback) # Subscribe to an event by name and run the `callback`
    runProject(projectObject) # Run a project by name in the projectObject, e.g.: ({name: 'Instruments'})
    onConnected(callback) # Run `callback` when the websocket is connected
    onDisconnected(callback) # Run `callback` when the websocket is disconnected

On the instruments panel, we have a setup of three different views. The `InstrumentsController` tells the instruments project to open upon it loading up (line 120 of `instruments_controller.js`). The rest of the `InstrumentsController` defines the line graph that gets loaded.

#### Compiling new apps

When you've updated any files in the skinnyapp directory, we must package these files and send them with the app. To do this, we've included a tool with the `rake` command. 

    rake assets:precompile_and_copy

This will compile and copy the files into the appropriate directories in MakeWithMoto directories. 

#### app/views

This directory defines the main html files that are loaded by the webview inside the android application as well as for the web view. The `app/views/layout.erb` is the layout file for the WebIDE view. This is the container for the rest of the WebIDE. 

The `app/views/android/index.erb` is the webview that is loaded for every `project` that is loaded. This is a relatively small container that defines the bare minimum that is needed by the android webview. The `app/views/android/help.erb` is the webview that's loaded that shows the help menu inside of the application.

#### app/assets

This directory contains all of the `css`, `images`, as well as the `javascripts` that power the two applications. 

The AngularJS app is `configured` in the `app/assets/js/config/` directory. Each of these files are loaded before the main application. The `routes.js.coffee` directory defines all the routes that are loaded based upon the # location in the webview. 

#### Example apps

All of the files in `android_scripting/MakeWithMoto/assets/ExampleApps` are copied on to the android device every time it runs. This ensures that the example apps are all on every device. Thus, to edit an example app, simply edit the script.js in the example app folder. 