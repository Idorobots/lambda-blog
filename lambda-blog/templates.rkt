#lang racket

(require "templates/hello.rkt"
         "templates/header.rkt")

(provide (all-from-out "templates/hello.rkt"
                       "templates/header.rkt"))
