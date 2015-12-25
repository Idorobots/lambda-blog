#lang racket

(require "templates/banner.rkt"
         "templates/footer.rkt"
         "templates/header.rkt"
         "templates/hello.rkt"
         "templates/navigation.rkt")

(provide (all-from-out "templates/banner.rkt"
                       "templates/footer.rkt"
                       "templates/header.rkt"
                       "templates/hello.rkt"
                       "templates/navigation.rkt"))
