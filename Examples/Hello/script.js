var EditText = Packages.android.widget.EditText;
var Toast = Packages.android.widget.Toast;
var Gravity = Packages.android.view.Gravity;

// Press "Run Activity" to run this script 
// as a new activity.
function onCreate(icicle) 
{
    var text = "Welcome to DroidScript - "
        + "JavaScript on Android!\n"
        + "With a dynamic language like JavaScript "
        + "applications can be authored interactively and "
        + "also be updated dynamically. "
        + "The vision is a web of linked applications, "
        + "similar in spirit to HyperCard stacks.";
    var editor = new EditText(Activity);
    editor.setGravity(Gravity.TOP);
    editor.setTextSize(20);
    editor.setText(text);
    Activity.setContentView(editor); 
}

// Press "Run" to run only top level code 
// in the script.
Toast.makeText(Activity,
    "Hello World",
    Toast.LENGTH_SHORT).show();

// Press menu button and select "Open script"
// Then type a script file name or url.
// Try one of the following on the SD-card:
// droidscript/Colors.js
// droidscript/Paint.js
