#lang racket

(define (get slot entity)
  (when (well-formed? entity)
    (cond ((null? entity)
           (error (format "Entity ~s does not contain slot ~s."
                          entity
                          slot)))
          ((equal? (car entity) slot)
           (cadr entity))
          (else
           (get slot (cddr entity))))))

(provide get)

(define (well-formed? proplist)
  (equal? (modulo (length proplist)
                  2)
          0))

(module+ test
  (require rackunit)

  (check-equal? (well-formed? '()) #t)
  (check-equal? (well-formed? '(1)) #f)
  (check-equal? (well-formed? '(1 2)) #t)
  (check-equal? (well-formed? '(1 2 3 4)) #t)

  (define ent '(slot-a value slot-b 23))
  (check-equal? (get 'slot-a ent) 'value)
  (check-equal? (get 'slot-b ent) 23)
  (check-exn exn:fail? (lambda () (get 'slot-c ent))))
