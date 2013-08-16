
var content =
"""<html>
    <body>
        <script>
        function vibrate() {
            activity.eval(
                "android.vibrate(500)"); }
        </script>
        <h1>Vibrate!</h1>
        <input 
            type="button" 
            value="brrrbrbrbr" 
            onclick="vibrate()">
    </body>
</html>""";

browser.loadData(content);
