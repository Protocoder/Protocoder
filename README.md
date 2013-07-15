codewithmoto
============

Code With Moto is a unique android application that embeds an Android Integrated Development Environment that provides a direct connection to a live, running android. It enables developers to write android applications in JavaScript, from the browser (or the phone).

### How to hack/contribute

There are two sides of this application, on the phone (java) and the browser (javascript). The application exposes a hidden webview that gives access to the javascript bridge to the phone application. We'll discuss how that works later:

To build the Android App, simply open the project in Eclipse and run and build it.

To build the web view:

    cd skinnyapp
    rake assets:precompile_and_copy

To edit and update the webview:

    check that you have rvm installed in your system
      curl -L https://get.rvm.io | bash -s stable --ruby=1.9.3
      rvm install 1.9.3
      gem install bundler

    cd skinnyapp
    bundle install 

    If you want to launch a web server to check the webapp write 
    thin -R config.ru start

And navigate to your browser at `http://localhost:8080/`. The application is built using Sinatra and AngularJS. The structure of the application looks like:

    app/
      assets/
        css/
        img/
        js/
          app/
            controllers/      # <~ client-side controllers
            directives/
            filters/
            resources/
            services/
            main.js.coffee    # <~ the main entry point for the app
          application.coffee  # <~ The file that includes all the js files
          vendor.coffee
          android.js.coffee
        templates/
      controllers/            # <~ server-side controllers
      helpers/
      models/
      views/                  # <~ server-side rendered views
    config/
    db/
    lib/
    public/
    test/

Only the client-side portion of the app makes it to the phone and is packaged and bundled up in several javascript files with the `assets:precompile_and_copy` command. The rest of the structure is set so we can develop the client-side application independently from needing the phone.

### How the Javascript integration works

Due to the nature of the Android `JavascriptBridge` where we can only send native strings back and forth, we have to create some loopholes in the structure of our javascript to support execution callbacks. Although this might sound difficult, our solution is quite novel.

The app expects there to be two functions defined at the root level (i.e. not in an object): `setup` and `loop`. The android app will call the `setup` function once and only once to allow for the user to setup different parts of their app, to establish pins, and objects. Currently, this part of the app does not wait for the entire execution to complete and thus the `loop` function may be called before the `setup` is complete. This is due to the clash in how JavaScript uses promises/callbacks to complete, while Java does not know about the execution callback chain.

The Java part of the app does not know about defined functions in the app and thus, we built a small javascript shim to sit between the app and the javascript interface bridge. This is located in the `moto_interface.js` file and sets up a global object called `window[moto]`. This global object is how the user interacts with the android interface.

All of the methods that the `moto` object can see are set on the object and call the private `_moto` object, the Android `JavaScript` interface object. This allows us to register methods and of all of the arguments. When we come upon an argument that is a `function` type, we call `generateCallback` that creates a method (with a unique name) on the global `window` object. When the AndroidInterface gets the methods, it simply receives a string of the global method name and thus can call run on the method.

On the otherside, when the android application receives a request to run a javascript method, it builds a window interface to the component. That is, for example when `createDigitalInput` is called, the javascript interface will call `setDigitalInput` on the window, create a variable on the window object named a dynamic name generated on the Java side.

With these harnesses setup in the Java and Javascript bridge, we can then create any method we want with callbacks and non-native argument types. For instance, we can add objects as methods, simply by converting them into JSON strings and then unravelling them on the Java side and then back on the Javascript side.