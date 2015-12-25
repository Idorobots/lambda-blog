#lang racket

(require "../html.rkt" "../entities.rkt" "utils.rkt")

(define (navigation entity)
  (let* ((p (get 'path-to-root entity))
         (l (list (img 'src: (path p "media/logo-button.png"))
                  (span 'style: "margin-left: 3px;"
                        (get 'title entity)))))
    (nav 'class: "navbar navbar-default navbar-fixed-top"
         (div 'class: "container navbar-inner"
              (div 'class: "navbar-header"
                   (button 'class: "navbar-toggle navbar-brand pull-left"
                           'type: 'button
                           'data-toggle: 'collapse
                           'data-target: ".navbar-responsive-collapse"
                           l)
                   (div 'class: 'hidden-xs
                        (a 'class: 'navbar-brand
                           'href: (path p)
                           l)))
              (div 'class: "hidden navbar-display-fix"
                   (div 'class: "collapse navbar-collapse navbar-right navbar-responsive-collapse"
                        (listify (get 'navigation entity) #f)))))))

(provide navigation)

(define (listify links nested?)
  (apply ul 'class: (if nested?
                        'dropdown-menu
                        "nav navbar-nav")
         (map (lambda (l)
                (if (list? (cdr l))
                    (li 'class: 'dropdown
                        (a 'href: "#"
                           (car l)
                           (span 'class: 'caret
                                 'style: "margin-left: 3px;"))
                        (listify (cdr l) #t))
                    (li (a 'href: (cdr l)
                           (car l)))))
              links)))

(module+ test
  (require rackunit
           "../html.rkt")

  (check-equal? (listify '((1 . 2) (2 . 3)) #f)
                (ul 'class: "nav navbar-nav"
                    (li (a 'href: 2 1))
                    (li (a 'href: 3 2))))
  (check-equal? (listify '((1 . 2) (2 . 3)) #t)
                (ul 'class: 'dropdown-menu
                    (li (a 'href: 2 1))
                    (li (a 'href: 3 2))))
  (check-equal? (listify '((1 . 2) (2 . ((3 . 4)))) #f)
                (ul 'class: "nav navbar-nav"
                    (li (a 'href: 2 1))
                    (li 'class: 'dropdown
                        (a 'href: "#"
                           2
                           (span 'class: 'caret
                                 'style: "margin-left: 3px;"))
                        (ul 'class: 'dropdown-menu
                            (li (a 'href: 4 3)))))))
