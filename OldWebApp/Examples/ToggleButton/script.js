var Button = Packages.android.widget.Button;
var ToggleButton = Packages.android.widget.ToggleButton;
var Toast = Packages.android.widget.Toast;
var LinearLayout = Packages.android.widget.LinearLayout;
var LayoutParams = Packages.android.widget.LinearLayout.LayoutParams;

// Called when creating the Activity
function onCreate(icicle)
{
    
    // Button that evaluates the code in the script view.
    var button1 = new Button(Activity);
    button1.setLayoutParams(new LayoutParams(
        LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1));
    button1.setText("B1");
    button1.setOnClickListener(function () { 
        showToast("You clicked a button B1"); });
     
     
     // Button that evaluates the code in the script view.
    var button2 = new Button(Activity);
    button2.setLayoutParams(new LayoutParams(
        LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1));
    button2.setText("B2");
    button2.setOnClickListener(function () { 
        showToast("You clicked a button B2"); });
     
        
        
   // Button that evaluates the code in the script view.
    var button3 = new ToggleButton(Activity);
    button3.setLayoutParams(new LayoutParams(
        LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1));
    button3.setText("B3");
    button3.setOnClickListener(function () { 
        showToast("ToggleButton B3 is " + button3.getText()); });
     
     
     var buttonLayout = new LinearLayout(Activity);
    buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
    buttonLayout.setLayoutParams(new LayoutParams(
        LayoutParams.FILL_PARENT,  LayoutParams.WRAP_CONTENT, 0));
    buttonLayout.addView(button1);
    buttonLayout.addView(button2);
    buttonLayout.addView(button3);
    
    
       var mainLayout = new LinearLayout(Activity);
    mainLayout.setOrientation(LinearLayout.VERTICAL);
    mainLayout.setLayoutParams(new LayoutParams(
        LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    mainLayout.addView(buttonLayout);

    Activity.setContentView(mainLayout);  
     
}

function showToast(message)
{
    Toast.makeText(
        Activity,
        message,
        Toast.LENGTH_SHORT).show();
}