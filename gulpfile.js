'use strict'

var gulp = require('gulp');
var watch = require('gulp-watch');
var sass = require('gulp-sass');

//css
gulp.task('css', function(){
  gulp.src('./sass/style.scss')
    .pipe(sass())
    .pipe(gulp.dest('./public/css'));
})

gulp.task('watch', function(){
  gulp.watch('./scss/*.scss', ['css'])
});

gulp.task('default', ['css']);