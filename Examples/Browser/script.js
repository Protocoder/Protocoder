   
    var content =
    """<html>
        <body>
            <script>
            function showToast(message) {
                activity.eval(
                    "var Toast = Packages.android.widget.Toast;" +
                    "Toast.makeText(Activity, '" + message + "', " +
                    "Toast.LENGTH_SHORT).show();"); }
            </script>
            <h1>Take the pill</h1>
            <input 
                type="button" 
                value="Take pill" 
                onclick="showToast('You have taken the red pill!')">
        </body>
    </html>""";
    
    browser.load(content);
