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

<p>A script on this page starts this clock:</p>
<p id="demo"></p>

<script>
var myVar=setInterval(function(){myTimer()},1000);

function myTimer()
{
var d=new Date();
var t=d.toLocaleTimeString();
document.getElementById("demo").innerHTML=t;
}
</script>

</body>
</html>""";
    
    webview.loadData(content, "text/html", "utf-8");
}







