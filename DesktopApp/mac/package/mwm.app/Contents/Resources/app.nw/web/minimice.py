import os

def minifyAndUnite(files, output_file):
  full_text = []
  for f in files:
    full_text.extend([l.lstrip() for l in open(f, "r").readlines()])
  open(output_file, "w").write("".join(full_text))

#What files?
#js = ['javascripts/src/ace.js','javascripts/src/ace.js','javascripts/src/theme-creative2.js','javascripts/src/mode-javascript.js','javascripts/supersized/js/supersized.core.3.2.1.min.js','javascripts/jquery.json-2.4.min.js','javascripts/jquery.clippy.js','javascripts/swfobject.js','javascripts/slides.min.jquery.js','javascripts/codaslider/jquery.coda-slider-3.0.min.js']
#js = ['javascripts/supersized/js/supersized.core.3.2.1.min.js','javascripts/jquery.json-2.4.min.js','javascripts/jquery.clippy.js','javascripts/swfobject.js','javascripts/slides.min.jquery.js','javascripts/codaslider/jquery.coda-slider-3.0.min.js']
css = []
js = ['javascripts/src/ace.js','javascripts/src/theme-twilight.js','javascripts/src/theme-creative2.js','javascripts/src/mode-javascript.js']

#Get full path
cwd = os.getcwd()
full_path_css = ["%s/%s" % (cwd, fp) for fp in css]
full_path_js = ["%s/%s" % (cwd, fp) for fp in js]

#Minify and store
#minifyAndUnite(full_path_css, "%s/static/css_generated.css" % cwd)
minifyAndUnite(full_path_js, "%s/editor-min.js" % cwd)