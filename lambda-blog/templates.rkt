#lang racket

(require "templates/banner.rkt"
         "templates/footer.rkt"
         "templates/header.rkt"
         "templates/hello.rkt"
         "templates/navigation.rkt"
         "templates/static.rkt")

(provide (all-from-out "templates/banner.rkt"
                       "templates/footer.rkt"
                       "templates/header.rkt"
                       "templates/hello.rkt"
                       "templates/navigation.rkt"
                       "templates/static.rkt"))
