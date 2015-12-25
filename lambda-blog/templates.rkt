#lang racket

(require "templates/footer.rkt"
         "templates/header.rkt"
         "templates/hello.rkt"
         "templates/navigation.rkt")

(provide (all-from-out "templates/footer.rkt"
                       "templates/header.rkt"
                       "templates/hello.rkt"
                       "templates/navigation.rkt"))
