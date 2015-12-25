#lang racket/base

(require scribble/html)
(provide (all-from-out scribble/html))

(define (footer . args)
  (apply element 'footer args))

(provide footer)
