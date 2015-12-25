#lang info

(define collection 'multi)
(define deps '("base"
               "rackunit-lib"
               "scribble-html-lib"))
(define build-deps '("scribble-lib" "racket-doc"))
(define scribblings '(("scribblings/lambda-blog.scrbl" ())))
(define pkg-desc "A static blog generator.")
(define version "0.0")
(define pkg-authors '(Idorobots))
