module.exports = function (grunt) {

  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),
    watch: {
      main: {
        files: ['src/**/*.js', 'index.html', 'src/tpl/*.html'],
        tasks: ['requirejs'],
        options: { livereload: true }
      },
      styles: {
        files: 'src/styles/**/*.less',
        tasks: ['less'],
        options: {
          nospawn: true
        }
      }
    },
    less: {
      development: {
        options: {
          compress: true,
          yuicompress: true,
          optimization: 2
        },
        files: {
          // target.css file: source.less file
          "css/style.css": "src/styles/less/style.less",
          "css/dashboard.css": "src/styles/less/dashboard.less"
        }
      }
    },

    requirejs: {
      compile: {
        options: {
          include: ["app"],
          baseUrl: ".",
          mainConfigFile: "src/config.js",
          out: "js/app.js",
          optimize: "uglify2",
          useStrict: true,
          preserveLicenseComments: false,
          generateSourceMaps: true,
          findNestedDependencies: true,
          wrap: true
        }
      }
    }
  });

  grunt.loadNpmTasks('grunt-contrib-watch');
  grunt.loadNpmTasks('grunt-contrib-requirejs');
  grunt.loadNpmTasks('grunt-contrib-less');

  grunt.registerTask('default', ['watch']);

};
