
function onCreate(bubble)
{
    var WebView = Packages.android.webkit.WebView;

    var webview = new WebView(Activity);
    webview.getSettings().setJavaScriptEnabled(true);
    webview.addJavascriptInterface(Activity, "activity");
    Activity.setContentView(webview);    
    
    var content =
    """
<!DOCTYPE html>
<html>
<body>

<p>Click the button to wait 3 seconds, then alert "Hello".</p>
<button onclick="myFunction()">Try it</button>

<script>

function showToast(message) {
                activity.eval(
                    "var Toast = Packages.android.widget.Toast;" +
                    "Toast.makeText(Activity, '" + message + "', " +
                    "Toast.LENGTH_SHORT).show();"); }
                    
                    
function myFunction()
{
setTimeout(function(){showToast("Hello World");},3000);
}
</script>

</body>
</html>
""";
    
    webview.loadData(content, "text/html", "utf-8");
}
