#lang racket/base

(require scribble/html)
(provide (all-from-out scribble/html))

(define (nav . args)
  (apply element 'nav args))

(provide nav)

(define (header . args)
  (apply element 'header args))

(provide header)

(define (article . args)
  (apply element 'article args))

(provide article)

(define (footer . args)
  (apply element 'footer args))

(provide footer)
